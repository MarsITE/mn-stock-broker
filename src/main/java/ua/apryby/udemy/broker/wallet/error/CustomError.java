package ua.apryby.udemy.broker.wallet.error;

import ua.apryby.udemy.broker.api.RestApiResponse;

public record CustomError(
        int status,
        String error,
        String message
) implements RestApiResponse {
}
