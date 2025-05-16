package ch.brix.gql.client;

import lombok.Getter;

@Getter
public class HttpException extends RuntimeException {
    private final int code;
    public HttpException(final int code, final String message) {
        super(message);
        this.code = code;
    }
}
