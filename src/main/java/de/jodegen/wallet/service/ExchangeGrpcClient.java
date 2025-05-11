package de.jodegen.wallet.service;

import de.jodegen.exchange.grpc.*;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExchangeGrpcClient {

    private ManagedChannel channel;
    private ExchangeServiceGrpc.ExchangeServiceBlockingStub stub;

    public ExchangeGrpcClient(){
        this.channel = ManagedChannelBuilder.forAddress("127.0.0.1", 9090)
                .usePlaintext() // TODO: use TLS
                .build();
        this.stub = ExchangeServiceGrpc.newBlockingStub(channel);
    }

    public BigDecimal getExchangeRate(String currencyCode) {
        var request = ExchangeRateRequest.newBuilder()
                .setCurrencyCode(currencyCode)
                .build();
        var response = stub.getRate(request);
        return BigDecimal.valueOf(response.getRate());
    }

    public BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal amount) {
        var request = ConversionRequest.newBuilder()
                .setFromCurrency(fromCurrency)
                .setToCurrency(toCurrency)
                .setAmount(amount.doubleValue())
                .build();
        var response = stub.convert(request);
        return BigDecimal.valueOf(response.getConvertedAmount());
    }
}
