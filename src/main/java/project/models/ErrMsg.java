package project.models;


public class ErrMsg {
    private String message;

    public ErrMsg() {
        message = "Error msg";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
         this.message = msg;
    }
}
