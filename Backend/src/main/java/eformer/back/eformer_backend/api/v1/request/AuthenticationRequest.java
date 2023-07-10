package eformer.back.eformer_backend.api.v1.request;

public class AuthenticationRequest {
    private String username;

    private String password;

    public AuthenticationRequest(String username, String password) {
        setUsername(username);
        setPassword(password);
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
}
