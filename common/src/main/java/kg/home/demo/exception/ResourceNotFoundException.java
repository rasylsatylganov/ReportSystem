package kg.home.demo.exception;

import kg.home.demo.model.ErrorCode;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, resourceName + ": запись с ID " + id + " не найдена.");
    }
}
