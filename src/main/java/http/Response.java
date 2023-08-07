package http;

public class Response {
    private int code;
    private String response;

    public Response(int code, String response) {
        this.code = code;
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", response='" + response + '\'' +
                '}';
    }
}
