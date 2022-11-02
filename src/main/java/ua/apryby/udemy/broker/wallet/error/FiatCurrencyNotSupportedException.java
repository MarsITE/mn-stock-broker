package ua.apryby.udemy.broker.wallet.error;

public class FiatCurrencyNotSupportedException extends RuntimeException {
    public FiatCurrencyNotSupportedException(String message) {
        super(message);
    }
}
