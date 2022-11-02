package ua.apryby.udemy.broker.wallet;

import ua.apryby.udemy.broker.Symbol;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositFiatMoney(
        UUID accountId,
        UUID walletId,
        Symbol symbol,
        BigDecimal amount
) {
}
