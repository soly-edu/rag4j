package soly.dev.rag.entity;

public class HttpResponseBody {

    private boolean success;
    private String message;
    private Object data;

    public HttpResponseBody() {}

    public HttpResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public HttpResponseBody(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static HttpResponseBody success() {
        return success("success");
    }

    public static HttpResponseBody success(String message) {
        return success(message, null);
    }

    public static HttpResponseBody success(String message, Object data) {
        return new HttpResponseBody(true, message, data);
    }

    public static HttpResponseBody fail() {
        return fail("fail");
    }

    public static HttpResponseBody fail(String message) {
        return fail(message, null);
    }

    public static HttpResponseBody fail(String message, Object data) {
        return new HttpResponseBody(false, message, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpResponseBody{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
