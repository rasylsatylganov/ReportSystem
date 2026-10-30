package kg.home.demo.exception;

import kg.home.demo.model.ErrorCode;

public class TemplateValidationException extends DomainException {
    public TemplateValidationException(String message) {
        super(ErrorCode.VALIDATION_ERROR, message);
    }
}
