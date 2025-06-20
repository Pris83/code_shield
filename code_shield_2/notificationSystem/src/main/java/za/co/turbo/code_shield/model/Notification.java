package za.co.turbo.code_shield.model;

public class Notification {
    private String subject;

    private String message;

    public Notification(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public Notification() {

    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}



