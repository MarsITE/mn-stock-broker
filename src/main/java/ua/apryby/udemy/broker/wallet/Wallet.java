package ua.apryby.udemy.broker.wallet;

import ua.apryby.udemy.broker.Symbol;
import ua.apryby.udemy.broker.api.RestApiResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record Wallet(
        UUID accountId,
        UUID walletId,
        Symbol symbol,
        BigDecimal available,
        BigDecimal locked
) implements RestApiResponse {

    public Wallet addAvailable(BigDecimal amountToAdd){
        return new Wallet(
                this.accountId,
                this.walletId,
                this.symbol,
                this.available.add(amountToAdd),
                this.locked
        );
    }
}
