package de.jodegen.wallet.service.grpc;

import de.jodegen.wallet.grpc.*;
import de.jodegen.wallet.model.ReserveReason;
import de.jodegen.wallet.model.ReservedBalance;
import de.jodegen.wallet.repository.CurrencyBalanceRepository;
import de.jodegen.wallet.service.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class GrpcWalletService extends WalletServiceGrpc.WalletServiceImplBase {

    private final TransactionService transactionService;
    private final GrpcWalletHelper helper;
    private final CurrencyBalanceRepository currencyBalanceRepository;
    private final WalletService walletService;

    @Override
    public void reserveFundsForBid(ReserveFundsForBidRequest request, StreamObserver<ReserveFundsForBidResponse> responseObserver) {
        try {
            var balance = helper.getValidBalance(request.getUserId(), request.getCurrencyCode());
            helper.ensureSufficientBalance(balance, request.getBidAmount());

            BigDecimal bidAmount = helper.toBigDecimal(request.getBidAmount());
            walletService.reserveAmount(balance, bidAmount, request.getAuctionId(), ReserveReason.AUCTION_BID);
            transactionService.createBidPlacedTransaction(balance, bidAmount, request.getAuctionId());

            var response = ReserveFundsForBidResponse.newBuilder()
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
    public void releaseReservedFunds(ReleaseReservedFundsRequest request, StreamObserver<ReleaseReservedFundsResponse> responseObserver) {
        try {
            var balance = helper.getValidBalance(request.getUserId(), request.getCurrencyCode());
            BigDecimal bidAmount = helper.toBigDecimal(request.getBidAmount());
            walletService.releaseReservedAmount(balance, bidAmount, request.getAuctionId());
            transactionService.createBidCancelledTransaction(balance, bidAmount, request.getAuctionId());

            var response = ReleaseReservedFundsResponse.newBuilder()
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
    public void processBuyNowPayment(ProcessBuyNowPaymentRequest request, StreamObserver<ProcessBuyNowPaymentResponse> responseObserver) {
        try {
            var balance = helper.getValidBalance(request.getUserId(), request.getCurrencyCode());
            helper.ensureSufficientBalance(balance, request.getPurchasePrice());
            balance.decreaseAmount(helper.toBigDecimal(request.getPurchasePrice()));
            currencyBalanceRepository.save(balance);

            transactionService.createPurchaseTransaction(balance, helper.toBigDecimal(request.getPurchasePrice()), request.getAuctionId());

            var response = ProcessBuyNowPaymentResponse.newBuilder()
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

    @Override
    public void getCurrencyBalance(GetBalanceRequest request, StreamObserver<GetBalanceResponse> responseObserver) {
        try {
            var balance = helper.getValidBalance(request.getUserId(), request.getCurrencyCode());

            List<ReservedBalance> reservedBalances = balance.getReservedBalances();
            List<de.jodegen.wallet.grpc.ReservedBalance> grpcReservedBalances = reservedBalances.stream()
                    .map(reservedBalance -> de.jodegen.wallet.grpc.ReservedBalance.newBuilder()
                            .setReservedBalanceId(reservedBalance.getId())
                            .setAuctionId(reservedBalance.getAuctionId())
                            .setAmount(reservedBalance.getAmount().doubleValue())
                            .setReason(reservedBalance.getReason().name())
                            .build())
                    .toList();

            var response = GetBalanceResponse.newBuilder()
                    .setUserId(request.getUserId())
                    .setCurrencyCode(request.getCurrencyCode())
                    .setBalance(balance.getAmount().doubleValue())
                    .addAllReservedBalances(grpcReservedBalances)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("Failed to get balance: " + e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unexpected error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void payoutOnAuctionSold(PayoutOnAuctionSoldRequest request, StreamObserver<PayoutOnAuctionSoldResponse> responseObserver) {
        try {
            var balance = helper.getValidBalance(request.getUserId(), request.getCurrencyCode());
            balance.increaseAmount(helper.toBigDecimal(request.getAmount()));
            currencyBalanceRepository.save(balance);

            boolean buyNow = request.getSaleType() == PayoutOnAuctionSoldRequest.SaleType.BUY_NOW;
            transactionService.createPayoutTransaction(balance, helper.toBigDecimal(request.getAmount()), request.getAuctionId(), buyNow);

            var response = PayoutOnAuctionSoldResponse.newBuilder()
                    .setSuccess(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("Failed to process payout: " + e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unexpected error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
