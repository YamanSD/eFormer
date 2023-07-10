package eformer.back.eformer_backend.api.v1;


import eformer.back.eformer_backend.model.User;
import eformer.back.eformer_backend.repository.UserRepository;
import eformer.back.eformer_backend.utility.auth.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/users/")
public class UsersApi extends BaseApi {
    private static final String emailPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    private static final String usernamePattern = "^\\w+$";

    final UserRepository manager;

    final PasswordEncoder encoder;

     public UsersApi(UserRepository manager, JwtService jService, PasswordEncoder encoder) {
         super(jService, manager);
         this.manager = manager;
         this.encoder = encoder;
     }

     public boolean isNotValidEmail(String email) {
         return email == null || !email.matches(emailPattern);
     }

     public boolean isNotValidUsername(String username) {
         return username == null || !username.matches(usernamePattern);
     }

     public boolean isNotValidPassword(String password) {
         return password == null || password.length() < 8;
     }

     public StringBuilder checkUser(User user) {
         var error = new StringBuilder();
         String username = user.getUsername();
         String email = user.getEmail();

         if (isNotValidUsername(username) || manager.existsByUsername(username)) {
             error.append("Username already in use or is invalid (Must consist of alphanumeric characters only)\n");
         }

         if (!User.isValidAdLevel(user.getAdLevel())) {
             error.append("Administrative level ")
                     .append(user.getAdLevel())
                     .append(" is invalid, must be <= ")
                     .append(User.getMaxAdLevel())
                     .append('\n');
         }

         if (isNotValidPassword(user.getPassword())) {
             error.append("Invalid password must 8 chars at least");
         }

         if (isNotValidEmail(email) || manager.existsByEmail(email)) {
             error.append("Email already in use or is invalid\n");
         }

         return error;
     }

     public StringBuilder checkUserUpdate(User user) {
         var error = new StringBuilder();

         String email = user.getEmail();
         String password = user.getPassword();
         Integer adLevel = user.getAdLevel();

         if (adLevel != null && !User.isValidAdLevel(user.getAdLevel())) {
             error.append("Administrative level ")
                     .append(user.getAdLevel())
                     .append(" is invalid, must be <= ")
                     .append(User.getMaxAdLevel())
                     .append('\n');
         }

         if (password != null && isNotValidPassword(password)) {
             error.append("Invalid password must 8 chars at least");
         }

         if (email != null && isNotValidEmail(email)) {
             error.append("Email already in use or is invalid\n");
         }

         return error;
     }

     public ResponseEntity<Object> getUsers(HashMap<String, String> header,
                                            HashMap<String, Object> body,
                                            boolean isAfter) {
         try {
             var date = (String) body.getOrDefault("date", null);

             if (date == null || header == null) {
                 /* 422 */
                 return new ResponseEntity<>("Missing sender or date fields",
                         HttpStatus.UNPROCESSABLE_ENTITY);
             } else if (!isManager(header)) {
                 /* 403 */
                 return new ResponseEntity<>("Sender is not a manager",
                         HttpStatus.FORBIDDEN);
             }

             /* 200 */
             return new ResponseEntity<>(
                     isAfter ? manager.findAllByCreateTimeAfter(processToDate(date)) :
                             manager.findAllByCreateTimeBefore(processToDate(date)),
                     HttpStatus.OK
             );
         } catch (Exception e) {
             /* Prevent potential server crash */
             return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); /* 400 */
         }
     }

     /**
      * Response Body must contain:
      *     date: Date after which to get the Users;
      * */
    @PostMapping("getAllAfter")
    @ResponseBody
    public ResponseEntity<Object> getUsersAfter(
            @RequestHeader HashMap<String, String> header,
            @RequestBody HashMap<String, Object> body
    ) {
        return getUsers(header, body, true);
    }

    @PostMapping("getAll")
    @ResponseBody
    public ResponseEntity<Object> getAll(@RequestHeader HashMap<String, String> header) {
        try {
            if (header == null) {
                /* 422 */
                return new ResponseEntity<>("Missing sender or date fields",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            } else if (!isManager(header)) {
                /* 403 */
                return new ResponseEntity<>("Sender is not a manager",
                        HttpStatus.FORBIDDEN);
            }

            /* 200 */
            return new ResponseEntity<>(
                    manager.findAll(),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            /* Prevent potential server crash */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); /* 400 */
        }
    }

    /**
     * Response Body must contain:
     *     date: Date before which to get the Users;
     * */
    @PostMapping("getAllBefore")
    @ResponseBody
    public ResponseEntity<Object> getUsersBefore(
            @RequestHeader HashMap<String, String> header,
            @RequestBody HashMap<String, Object> body
    ) {
        return getUsers(header, body, false);
    }

    @PostMapping("create")
    @ResponseBody
    public ResponseEntity<Object> create(
            @RequestHeader HashMap<String, String> header,
            @RequestBody User user
    ) {
        try {
            if (user == null || header == null) {
                /* 422 */
                return new ResponseEntity<>("No user and/or invalid token", HttpStatus.UNPROCESSABLE_ENTITY);
            } else if (!isManager(header)) {
                /* 403 */
                return new ResponseEntity<>("Sender not manager", HttpStatus.FORBIDDEN);
            }

            var error = checkUser(user);

            if (error.length() > 0) {
                /* 422 */
                return new ResponseEntity<>(error.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            /* Encode the password */
            user.setPassword(encoder.encode(user.getPassword()));

            var response = new HashMap<String, Object>();
            user = manager.save(user);

            response.put("userId", user.getUserId());
            response.put("adLevel", user.getAdLevel());
            response.put("role", user.translateRole());

            /* 200 */
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("update")
    @ResponseBody
    public ResponseEntity<Object> update(
            @RequestHeader HashMap<String, String> header,
            @RequestBody HashMap<String, Object> props
    ) {
        try {
            if (props == null || header == null) {
                /* 422 */
                return new ResponseEntity<>("No props and/or invalid token", HttpStatus.UNPROCESSABLE_ENTITY);
            } else if (!isManager(header)) {
                /* 403 */
                return new ResponseEntity<>("Sender not manager", HttpStatus.FORBIDDEN);
            }

            var user = manager.findById((Integer) props.get("userId")).orElseThrow();
            var userByUsername = manager.findByUsername((String) props.get("username")).orElseThrow();

            if (!user.equals(userByUsername)) {
                /* 422 */
                return new ResponseEntity<>("Username & ID don't match", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            if (props.containsKey("password")) {
                var pass = (String) props.get("password");

                if (pass.length() == 0 || encoder.encode(pass).equals(user.getPassword())) {
                    props.remove("password");
                }
            }

            props.remove("username");
            props.remove("userId");

            for (var prop: props.keySet()) {
                try {
                    var value = props.get(prop);

                    /* Use reflection to call setters */
                    User.class.getDeclaredMethod(
                            "set"
                                    + prop.substring(0, 1).toUpperCase()
                                    + prop.substring(1),
                            value.getClass()
                    ).invoke(user, value);
                } catch (Exception ignored) {
                }
            }

            var error = checkUserUpdate(user);

            if (error.length() > 0) {
                /* 422 */
                return new ResponseEntity<>(error.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            if (props.containsKey("password")) {
                /* Encode the new password */
                user.setPassword(encoder.encode(user.getPassword()));
            }

            manager.save(user);

            /* 200 */
            return new ResponseEntity<>("S", HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getByAdLevel")
    @ResponseBody
    public ResponseEntity<Object> getByLevel(@RequestHeader HashMap<String, String> header,
                                             @RequestBody Integer adLevel) {
        try {
            if (header == null) {
                /* 422 */
                return new ResponseEntity<>("Missing sender or date fields",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            } else if (!canUserChange(header)) {
                /* 403 */
                return new ResponseEntity<>("Sender is not a manager",
                        HttpStatus.FORBIDDEN);
            }

            /* 200 */
            return new ResponseEntity<>(
                    manager.findAllByAdLevel(adLevel),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getByUsername")
    @ResponseBody
    public ResponseEntity<Object> getByUsername(@RequestHeader HashMap<String, String> header,
                                             @RequestBody String name) {
        try {
            if (header == null) {
                /* 422 */
                return new ResponseEntity<>("Missing sender or date fields",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            } else if (!canUserChange(header)) {
                /* 403 */
                return new ResponseEntity<>("Sender is not a manager",
                        HttpStatus.FORBIDDEN);
            }

            /* 200 */
            return new ResponseEntity<>(
                    manager.findByUsername(name).orElseThrow(),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getEmployees")
    @ResponseBody
    public ResponseEntity<Object> getEmployees(@RequestHeader HashMap<String, String> header) {
        try {
            if (header == null) {
                /* 422 */
                return new ResponseEntity<>("Missing sender or date fields",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            } else if (!canUserChange(header)) {
                /* 403 */
                return new ResponseEntity<>("Sender is not a manager",
                        HttpStatus.FORBIDDEN);
            }

            /* 200 */
            return new ResponseEntity<>(
                    manager.findAllByAdLevelGreaterThanEqual(1),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getCustomers")
    @ResponseBody
    public ResponseEntity<Object> getCustomers(@RequestHeader HashMap<String, String> header) {
        return getByLevel(header, 0);
    }

    @GetMapping("roles")
    @ResponseBody
    public ResponseEntity<Object> getRoles() {
        return new ResponseEntity<>(User.getRoles(), HttpStatus.OK);
    }
}
