package org.viettel.vgov.exception;

public class InvalidRoleChangeException extends RuntimeException {
    
    public InvalidRoleChangeException(String message) {
        super(message);
    }
    
    public InvalidRoleChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
