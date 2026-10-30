package kg.home.demo.exception;

import kg.home.demo.model.ErrorCode;

public class InvalidRequestException extends DomainException {

    public InvalidRequestException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }
}
