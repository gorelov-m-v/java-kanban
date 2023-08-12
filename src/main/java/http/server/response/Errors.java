package http.server.response;

import java.util.List;

public class Errors {

    private boolean success;
    private int code;
    private List<PlatformError> errors;

    public Errors(boolean success, int code, List<PlatformError> errors) {
        this.success = success;
        this.code = code;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PlatformError> getErrors() {
        return errors;
    }

    public void setErrors(List<PlatformError> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "Errors{" +
                "success=" + success +
                ", code=" + code +
                ", errors=" + errors +
                '}';
    }
}
