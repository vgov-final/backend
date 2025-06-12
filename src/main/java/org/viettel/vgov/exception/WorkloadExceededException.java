package org.viettel.vgov.exception;

public class WorkloadExceededException extends RuntimeException {
    
    public WorkloadExceededException(String message) {
        super(message);
    }
    
    public WorkloadExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
