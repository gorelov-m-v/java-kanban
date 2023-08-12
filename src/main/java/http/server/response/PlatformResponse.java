package http.server.response;

public class PlatformResponse {

    private String message;

    public PlatformResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PlatformError{" +
                "error='" + message + '\'' +
                '}';
    }
}
