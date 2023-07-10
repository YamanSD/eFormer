package eformer.front.eformer_frontend.connector;

import eformer.front.eformer_frontend.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsersConnector extends RequestsGateway {
    private static final String baseUrl = "users/";

    private static String getUrl(String target) {
        return baseUrl + target;
    }

    public static List<User> proccessUsersList(Object response) {
        var result = new ArrayList<User>();

        for (var json: (JSONArray) response) {
            result.add(new User((JSONObject) json));
        }

        return result;
    }

    public static List<User> getAllAfter(LocalDateTime date) {
        try {
            var response = post(
                    getUrl("getAllAfter"),
                    date
            );

            return proccessUsersList(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch users",
                    "Invalid date and/or not signed in");
            return null;
        }
    }

    public static List<User> getAllBefore(LocalDateTime date) {
        try {
            var response = post(
                    getUrl("getAllBefore"),
                    date
            );

            return proccessUsersList(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch users",
                    "Invalid date and/or not signed in");
            return null;
        }
    }

    public static User create(User user) {
        try {
            var response = (JSONObject) post(
                    getUrl("create"),
                    user
            );

            return new User(response);
        } catch (Exception ignored) {
            displayWarning("Can't create user",
                    "Invalid user and/or not signed in");
            return null;
        }
    }

    public static User getByUsername(String username) {
        try {
            var response = (JSONObject) post(
                    getUrl("getByUsername"),
                    username
            );

            return new User(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch user",
                    "Invalid username and/or not signed in");
            return null;
        }
    }

    public static User create(JSONObject user) {
        return create(new User(user));
    }

    public static boolean update(User user) {
        try {
            var response = (String) post(
                    getUrl("update"),
                    user
            );

            return response.length() > 0;
        } catch (Exception ignored) {
            displayWarning("Can't update user",
                    "Invalid user and/or not signed in");
            return false;
        }
    }

    public static List<User> getCustomers() {
        try {
            var response = post(
                    getUrl("getCustomers"),
                    null
            );

            return proccessUsersList(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch customers",
                    "Not signed in or check connection");
            return null;
        }
    }

    public static List<User> getEmployees() {
        try {
            var response = post(
                    getUrl("getEmployees"),
                    null
            );

            return proccessUsersList(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch employees",
                    "Not signed in or check connection");
            return null;
        }
    }

    public static List<User> getAll() {
        try {
            var response = post(
                    getUrl("getAll"),
                    null
            );

            return proccessUsersList(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch users",
                    "Not signed in or check connection");
            return null;
        }
    }

    public static List<String> roles() {
        try {
            var temp = ((JSONArray) get(
                    getUrl("roles"),
                    null
            ));

            var result = new ArrayList<String>();

            for (Object val: temp) {
                result.add((String) val);
            }

            return result;
        } catch (Exception ignored) {
            displayWarning("Can't fetch roles",
                    "Check connection");
            return null;
        }
    }
}
