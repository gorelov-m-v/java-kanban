package http.server.response;

public class PlatformError {
    private String error;

    public PlatformError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return error;
    }

    public void setMessage(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "PlatformError{" +
                "error='" + error + '\'' +
                '}';
    }
}
