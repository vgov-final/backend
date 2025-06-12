package org.viettel.vgov.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardResponse<T> {
    
    private boolean success;
    private T data;
    private String message;
    private ErrorInfo error;
    
    // Success response constructors
    public static <T> StandardResponse<T> success(T data) {
        return new StandardResponse<>(true, data, "Operation completed successfully", null);
    }
    
    public static <T> StandardResponse<T> success(T data, String message) {
        return new StandardResponse<>(true, data, message, null);
    }
    
    // Error response constructors
    public static <T> StandardResponse<T> error(String code, String message) {
        ErrorInfo error = new ErrorInfo(code, message, null);
        return new StandardResponse<>(false, null, null, error);
    }
    
    public static <T> StandardResponse<T> error(String code, String message, String details) {
        ErrorInfo error = new ErrorInfo(code, message, details);
        return new StandardResponse<>(false, null, null, error);
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorInfo {
        private String code;
        private String message;
        private String details;
    }
}
