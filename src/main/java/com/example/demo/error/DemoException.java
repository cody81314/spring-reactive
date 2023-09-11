package com.example.demo.error;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class DemoException extends ErrorResponseException {

    public DemoException(HttpStatusCode status) {
        super(status);
    }

    public DemoException(HttpStatusCode status, Throwable cause) {
        super(status, cause);
    }

    public DemoException(HttpStatusCode status, ProblemDetail body, Throwable cause) {
        super(status, body, cause);
    }

    public DemoException(HttpStatusCode status, ProblemDetail body, Throwable cause, String messageDetailCode, Object[] messageDetailArguments) {
        super(status, body, cause, messageDetailCode, messageDetailArguments);
    }
}
