package megachj.kpay.assignment.controller;

import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.constant.ResultCodes;
import megachj.kpay.assignment.exception.SprinklingException;
import megachj.kpay.assignment.model.rest.RestResponse;
import megachj.kpay.assignment.model.rest.RestResponseHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class RestProcessingExceptionHandler {

    @ExceptionHandler(SprinklingException.class)
    public ResponseEntity<RestResponse> handleSprinklingException(HttpServletRequest request, SprinklingException ex) {

        log.error("[{}] SprinklingException. \n{}, {}", ex.getTrackingTime(), ex.getResultCode(), ex.getMessage());
        log.info("", ex);

        return new ResponseEntity<>(new RestResponse(new RestResponseHeader(ex.getResultCode(), ex.getMessage(), false)), HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestResponse> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {

        log.error("IllegalArgumentException. \n{}", ex.getMessage());
        log.info("", ex);

        return new ResponseEntity<>(new RestResponse(new RestResponseHeader(ResultCodes.ILLEGAL_ARGUMENT.getCode(), ex.getMessage(), false)), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> handleException(HttpServletRequest request, Exception ex) {

        log.error("Exception. \n{}", ex.getMessage());
        log.info("", ex);

        return new ResponseEntity<>(new RestResponse(new RestResponseHeader(ResultCodes.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage(), false)), HttpStatus.OK);
    }
}
