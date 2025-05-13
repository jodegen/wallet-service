package de.jodegen.wallet.model;

public enum TransactionReason {
    AUCTION_BID,
    AUCTION_BID_CANCELLED,
    AUCTION_SALE,
    AUCTION_SALE_BUY_NOW,
    AUCTION_BUY_NOW,
    CURRENCY_CONVERSION,
    WALLET_TRANSFER,
    WALLET_CREATED,
    ADMIN_ADJUSTMENT,
    OTHER;

    public String getLabel() {
        return switch (this) {
            case AUCTION_BID -> "Auction bid placed";
            case AUCTION_BID_CANCELLED -> "Auction bid cancelled";
            case AUCTION_SALE -> "Bidding Auction sold";
            case AUCTION_SALE_BUY_NOW -> "Buy It Now Auction sold";
            case AUCTION_BUY_NOW -> "Auction purchased (Buy Now)";
            case CURRENCY_CONVERSION -> "Currency conversion";
            case WALLET_TRANSFER -> "Wallet-to-wallet transfer";
            case WALLET_CREATED -> "Wallet created";
            case ADMIN_ADJUSTMENT -> "Admin adjustment";
            case OTHER -> "Other";
        };
    }
}
