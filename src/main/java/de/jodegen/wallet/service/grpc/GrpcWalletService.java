package de.jodegen.wallet.service.grpc;

import de.jodegen.wallet.grpc.*;
import de.jodegen.wallet.service.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcWalletService extends WalletServiceGrpc.WalletServiceImplBase {

    private final TransactionService transactionService;
    private final GrpcWalletHelper helper;

    @Override
    public void auctionBid(AuctionBidRequest request, StreamObserver<AuctionBidResponse> responseObserver) {
        try {
            var balance = helper.getValidBalance(request.getUserId(), request.getCurrencyCode());
            helper.ensureSufficientBalance(balance, request.getBidAmount());
            balance.reserveAmount(helper.toBigDecimal(request.getBidAmount()));

            transactionService.createBidPlacedTransaction(balance.getWallet(),
                    request.getCurrencyCode(), helper.toBigDecimal(request.getBidAmount()), request.getAuctionId());

            var response = AuctionBidResponse.newBuilder()
                    .setUserId(request.getUserId())
                    .setCurrencyCode(balance.getCurrencyCode())
                    .setBidAmount(request.getBidAmount())
                    .setSuccess(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("Failed to reserve amount: " + e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unexpected error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void auctionBidCancel(AuctionBidCancelRequest request, StreamObserver<AuctionBidCancelResponse> responseObserver) {
        try {
            var balance = helper.getValidBalance(request.getUserId(), request.getCurrencyCode());
            balance.releaseReservedAmount(helper.toBigDecimal(request.getBidAmount()));
            transactionService.createBidCancelledTransaction(balance.getWallet(),
                    request.getCurrencyCode(), helper.toBigDecimal(request.getBidAmount()), request.getAuctionId());

            var response = AuctionBidCancelResponse.newBuilder()
                    .setUserId(request.getUserId())
                    .setCurrencyCode(balance.getCurrencyCode())
                    .setBidAmount(request.getBidAmount())
                    .setSuccess(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("Failed to cancel reservation: " + e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unexpected error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void auctionPurchase(AuctionPurchaseRequest request, StreamObserver<AuctionPurchaseResponse> responseObserver) {
        try {
            var balance = helper.getValidBalance(request.getUserId(), request.getCurrencyCode());
            helper.ensureSufficientBalance(balance, request.getPurchasePrice());

            balance.decreaseAmount(helper.toBigDecimal(request.getPurchasePrice()));
            transactionService.createPurchaseTransaction(balance.getWallet(), request.getCurrencyCode(),
                    helper.toBigDecimal(request.getPurchasePrice()), request.getAuctionId());

            var response = AuctionPurchaseResponse.newBuilder()
                    .setUserId(request.getUserId())
                    .setAuctionId(request.getAuctionId())
                    .setPurchasePrice(request.getPurchasePrice())
                    .setCurrencyCode(balance.getCurrencyCode())
                    .setSuccess(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("Failed to commit purchase: " + e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unexpected error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
