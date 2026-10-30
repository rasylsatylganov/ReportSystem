package kg.home.demo.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import kg.home.demo.model.ErrorCode;
import kg.home.demo.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException exception) {
        HttpStatus status = exception instanceof ResourceNotFoundException ?
                HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        if (exception.isPrintStack()) {
            log.error("Ошибка операции приложения", exception);
        }
        return ResponseEntity.status(status)
                .body(new ErrorResponse(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMissingReference(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ErrorCode.RESOURCE_NOT_FOUND, "Связанная запись не найдена."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataConflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ErrorCode.DATA_CONFLICT,
                        "Операция нарушает ограничения данных: код уже существует, запись используется " +
                                "или содержит недопустимые значения."));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .distinct()
                .sorted()
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorCode.VALIDATION_ERROR, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        log.error("Необработанная ошибка при выполнении запроса", exception);
        return ResponseEntity.internalServerError().body(this.frameworkError(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        String message = exception.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.joining("; "));
        ErrorResponse body = new ErrorResponse(ErrorCode.VALIDATION_ERROR,
                !message.isBlank() ? message : "Проверьте входные данные.");
        return this.handleExceptionInternal(exception, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        ErrorResponse body = new ErrorResponse(ErrorCode.MALFORMED_REQUEST,
                "Некорректный JSON: проверьте структуру, типы данных и формат дат.");
        return this.handleExceptionInternal(exception, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        ErrorResponse body = status.is5xxServerError() ? this.frameworkError(status) :
                new ErrorResponse(ErrorCode.VALIDATION_ERROR,
                        "Проверьте параметры запроса: значения выходят за допустимые границы.");
        return this.handleExceptionInternal(exception, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        if (status.is5xxServerError()) {
            log.error("Ошибка обработки HTTP-запроса", exception);
        }
        Object response = body instanceof ErrorResponse ? body : this.frameworkError(status);
        return super.handleExceptionInternal(exception, response, headers, status, request);
    }

    private ErrorResponse frameworkError(HttpStatusCode status) {
        if (status.is5xxServerError()) {
            return new ErrorResponse(ErrorCode.INTERNAL_ERROR,
                    "Внутренняя ошибка сервера. Обратитесь к администратору.");
        }
        return switch (status.value()) {
            case 400 -> new ErrorResponse(ErrorCode.INVALID_REQUEST, "Проверьте параметры запроса.");
            case 404 -> new ErrorResponse(ErrorCode.RESOURCE_NOT_FOUND, "Запрошенный ресурс не найден.");
            case 405 -> new ErrorResponse(ErrorCode.METHOD_NOT_ALLOWED,
                    "HTTP-метод не поддерживается для этого ресурса.");
            case 415 -> new ErrorResponse(ErrorCode.UNSUPPORTED_MEDIA_TYPE, "Неподдерживаемый формат запроса.");
            default -> new ErrorResponse(ErrorCode.HTTP_ERROR, "Запрос не может быть выполнен в указанном формате.");
        };
    }
}
