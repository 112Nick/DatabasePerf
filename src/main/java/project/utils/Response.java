package project.utils;


import org.springframework.http.HttpStatus;

public class Response<T> {
    private T body;
    private HttpStatus status;

    public Response () {
        this.status = HttpStatus.IM_USED;
    }

    public Response (T obj, HttpStatus status) {
        this.body = obj;
        this.status = status;
    }

    public void setResponse(T obj, HttpStatus status) {
        this.body = obj;
        this.status = status;
    }


    public HttpStatus getStatus() {
        return this.status;
    }

    public T getBody() {
        return body;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response<?> response = (Response<?>) o;

        if (body != null ? !body.equals(response.body) : response.body != null) return false;
        return status == response.status;
    }

    @Override
    public int hashCode() {
        int result = body != null ? body.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
