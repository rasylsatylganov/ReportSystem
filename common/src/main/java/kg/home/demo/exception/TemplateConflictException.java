package kg.home.demo.exception;

import kg.home.demo.model.ErrorCode;

public class TemplateConflictException extends DomainException {
    public TemplateConflictException(String message) {
        super(ErrorCode.DATA_CONFLICT, message);
    }
}
