package megachj.kpay.assignment.controller;

import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.model.rest.ResponseHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

// TODO
@Slf4j
@ControllerAdvice
public class RestProcessingExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseHeader> handleLaunchingException(HttpServletRequest request, RuntimeException ex) {

        log.error("RuntimeException. \n{}", ex.getMessage());
        log.info("", ex);

        return new ResponseEntity<>(new ResponseHeader(-500, ex.getMessage(), false), HttpStatus.OK);
    }
}
