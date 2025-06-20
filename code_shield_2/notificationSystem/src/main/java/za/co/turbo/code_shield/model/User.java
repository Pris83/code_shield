package za.co.turbo.code_shield.model;



public class User {

    private Long id;

    private String username;

    private String email;

    private String phoneNumber;


    public User(Long id, String phoneNumber, String email, String username) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.username = username;
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}

