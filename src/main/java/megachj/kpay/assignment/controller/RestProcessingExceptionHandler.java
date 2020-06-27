package megachj.kpay.assignment.controller;

import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.constant.ResultCodes;
import megachj.kpay.assignment.exception.SprinklingException;
import megachj.kpay.assignment.model.rest.ResponseHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class RestProcessingExceptionHandler {

    @ExceptionHandler(SprinklingException.class)
    public ResponseEntity<ResponseHeader> handleSprinklingException(HttpServletRequest request, SprinklingException ex) {

        log.error("[{}] SprinklingException. \n{}, {}", ex.getTrackingTime(), ex.getResultCode(), ex.getMessage());
        log.info("", ex);

        return new ResponseEntity<>(new ResponseHeader(ex.getResultCode(), ex.getMessage(), false), HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseHeader> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {

        log.error("IllegalArgumentException. \n{}", ex.getMessage());
        log.info("", ex);

        return new ResponseEntity<>(new ResponseHeader(ResultCodes.ILLEGAL_ARGUMENT.getCode(), ex.getMessage(), false), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseHeader> handleException(HttpServletRequest request, Exception ex) {

        log.error("Exception. \n{}", ex.getMessage());
        log.info("", ex);

        return new ResponseEntity<>(new ResponseHeader(ResultCodes.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage(), false), HttpStatus.OK);
    }
}
