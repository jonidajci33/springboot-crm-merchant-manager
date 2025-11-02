package merchant_manager.models.ErrorResponse;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime LocalDateTime;
    private String message;
    private String details;
    private int status;

    public ErrorResponse(LocalDateTime LocalDateTime, String message, String details, int status) {
        this.LocalDateTime = LocalDateTime;
        this.message = message;
        this.details = details;
        this.status = status;
    }

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime;
    }

    public void setLocalDateTime(LocalDateTime LocalDateTime) {
        this.LocalDateTime = LocalDateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "LocalDateTime=" + LocalDateTime +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status +
                '}';
    }
}
