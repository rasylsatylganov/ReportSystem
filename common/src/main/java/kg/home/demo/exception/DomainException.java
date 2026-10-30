package kg.home.demo.exception;

import kg.home.demo.model.ErrorCode;
import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {


    private final ErrorCode code;


    protected DomainException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public boolean isPrintStack() {
        return false;
    }
}
