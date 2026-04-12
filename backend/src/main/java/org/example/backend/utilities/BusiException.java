package org.example.backend.utilities;

import lombok.Getter;
import org.example.backend.enums.StatusCode;

import java.io.Serial;

@Getter
public class BusiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private StatusCode code;
    private String message;

    public BusiException(StatusCode code) {
        super(code.getMessage());
        this.code = code;
    }
    public BusiException(StatusCode code, String message) {
        super(message);
        this.code = code;
    }
    public BusiException(Throwable cause) {
        super(cause);
    }
    public BusiException(StatusCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }
    public StatusCode getStatusCode() {
        return code;
    }
}

