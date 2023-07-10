package eformer.back.eformer_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


/**
 * User class Entity representing the `user` table.
 * User for customers & employees.
 * Distinction is made using the adLevel.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private final Integer userId;

    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createTime;

    @Column(name = "ad_level")
    private Integer adLevel;

    @Transient
    @JsonIgnore
    private static final List<String> roles = List.of(new String[]{"Banned", "Customer", "Employee", "Manager"});

    public static List<String> getRoles() {
        return roles;
    }

    public static boolean isValidAdLevel(Integer adLevel) {
        return adLevel <= getMaxAdLevel();
    }

    public static int getMaxAdLevel() {
        return 2;
    }

    public User() {
        this("", "", "", -1);
    }

    protected User(Integer userId, String username, String email,
                   String password, Timestamp createTime, Integer adLevel,
                   String fullName) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setFullName(fullName);
        setAdLevel(adLevel);
        this.userId = userId;
        setCreateTime(createTime);
    }

    protected User(Integer userId, String username, String email,
                   String password, String createTime, Integer adLevel,
                   String fullName) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setFullName(fullName);
        setAdLevel(adLevel);
        this.userId = userId;
        setCreateTime(createTime);
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public void setCreateTime(String createTime) {
        var temp = createTime.split("T");
        this.createTime = Timestamp.valueOf(temp[0] + ' ' + temp[1]);
    }

    public User(String username, String email, String password, Integer adLevel, String fullName) {
        this(-1, username, email, password, new Timestamp(new Date().getTime()), adLevel, fullName);
    }

    public User(String username, String email, String password, Integer adLevel) {
        this(-1, username, email, password, new Timestamp(new Date().getTime()), adLevel, "");
    }

    public User(String username, String email, String password) {
        this(username, email, password, 0);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return getAdLevel() > -2;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return getCreateTime().isBefore(getCreateTime().plusYears(1));
    }

    @Override
    public boolean isEnabled() {
        return getAdLevel() > -2;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String translateRole() {
        return roles.get(getAdLevel() + 1);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(translateRole()));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }

    public LocalDateTime getCreateTime() {
        return createTime.toLocalDateTime();
    }

    public Integer getAdLevel() {
        return adLevel;
    }

    public void setAdLevel(Integer adLevel) {
        this.adLevel = adLevel;
    }

    public boolean isCustomer() {
        return getAdLevel() == 0;
    }

    public boolean isManager() {
        return getAdLevel() >= 2;
    }

    public boolean isGuest() {
        return getAdLevel() < 0 || getUserId() < 0;
    }

    public boolean isEmployee() {
        return getAdLevel() >= 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getUserId(), user.getUserId())
                && Objects.equals(getUsername(), user.getUsername())
                && Objects.equals(getEmail(), user.getEmail())
                && Objects.equals(getPassword(), user.getPassword())
                && Objects.equals(getCreateTime(), user.getCreateTime())
                && Objects.equals(getAdLevel(), user.getAdLevel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getUserId(),
                getUsername(),
                getEmail(),
                getPassword(),
                getCreateTime(),
                getAdLevel()
        );
    }
}
