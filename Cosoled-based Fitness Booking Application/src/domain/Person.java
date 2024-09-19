package domain;

public abstract class Person {
    protected String username;
    protected String phone;
    protected String password;


    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Person(String username, String phone, String password) {
        this.username = username;
        this.phone = phone;
        this.password = password;
    }

    // Abstract method for the login functionality
    public abstract void login();
}