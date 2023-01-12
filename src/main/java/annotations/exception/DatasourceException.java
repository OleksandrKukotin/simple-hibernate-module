package annotations.exception;

public final class DatasourceException extends RuntimeException {

    public DatasourceException(String message, Exception exception) {
        super(message, exception);
    }
}
