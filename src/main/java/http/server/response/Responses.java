package http.server.response;

import java.util.List;

public class Responses {

    private boolean success;
    private int code;
    private List<PlatformResponse> messages;

    public Responses(boolean success, int code, List<PlatformResponse> messages) {
        this.success = success;
        this.code = code;
        this.messages = messages;
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

    public List<PlatformResponse> getErrors() {
        return messages;
    }

    public void setErrors(List<PlatformResponse> errors) {
        this.messages = errors;
    }

    @Override
    public String toString() {
        return "Errors{" +
                "success=" + success +
                ", code=" + code +
                ", errors=" + messages +
                '}';
    }
}
