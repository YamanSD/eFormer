package eformer.back.eformer_backend.api.v1.request;

public class RegisterRequest {
    private String fullName;

    private String email;

    private String username;

    private String password;

    private Integer adLevel;

    public RegisterRequest(String fullName, String email,
                           String username, String password,
                           Integer adLevel) {
        setFullName(fullName);
        setEmail(email);
        setUsername(username);
        setPassword(password);
        setAdLevel(adLevel);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAdLevel() {
        return adLevel;
    }

    public void setAdLevel(Integer adLevel) {
        this.adLevel = adLevel;
    }
}
