package merchant_manager.customExceptions;

public class CustomExceptions {

    public static class ResourceNotFoundException extends NullPointerException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }

    public static class CustomValidationException extends RuntimeException {
        public CustomValidationException(String message) {
            super(message);
        }
    }

    public static class SystemErrorException extends RuntimeException{
        public SystemErrorException(String message){ super(message);}
    }

}
