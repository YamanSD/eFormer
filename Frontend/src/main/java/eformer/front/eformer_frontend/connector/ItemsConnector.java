package eformer.front.eformer_frontend.connector;

import eformer.front.eformer_frontend.model.Item;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemsConnector extends RequestsGateway {
    private static final String baseUrl = "items/";

    public static Item getItemById(Integer id) {
        try {
            var params = new JSONObject();

            params.put("id", id);

            var response = (JSONObject) get(
                    getUrl("getById"),
                    params
            );

            return new Item(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch item",
                    "Invalid ID");
            return null;
        }
    }

    private static String getUrl(String target) {
        return baseUrl + target;
    }

    public static List<Item> proccessItemsList(Object response) {
        var result = new ArrayList<Item>();

        for (var json: (JSONArray) response) {
            result.add(new Item((JSONObject) json));
        }

        return result;
    }

    public static Item getItemByName(String name) {
        try {
            var params = new JSONObject();

            params.put("name", name);

            var response = (JSONObject) get(
                    getUrl("getByName"),
                    params
            );

            return new Item(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch item",
                    "Invalid name");
            return null;
        }
    }

    public static List<Item> getAll() {
        try {
            var response = get(
                    getUrl("getAll"),
                    null
            );

            return proccessItemsList(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch items",
                    "Check your connection");
            return null;
        }
    }

    public static Item create(Item item) {
        try {
            var response = (JSONObject) post(
                    getUrl("create"),
                    item
            );

            return new Item(response);
        } catch (Exception ignored) {
            displayWarning("Can't create item",
                    "Invalid Item and/or not signed in");
            return null;
        }
    }

    public static Item create(JSONObject item) {
        return create(new Item(item));
    }

    public static List<Item> getAllAfter(LocalDateTime date) {
        try {
            var response = post(
                    getUrl("getAllAfter"),
                    date
            );

            return proccessItemsList(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch items",
                    "Invalid date or check connection");
            return null;
        }
    }

    public static List<Item> getAllBefore(LocalDateTime date) {
        try {
            var response = post(
                    getUrl("getAllBefore"),
                    date
            );

            return proccessItemsList(response);
        } catch (Exception ignored) {
            displayWarning("Can't fetch items",
                    "Invalid date or check connection");
            return null;
        }
    }

    public static Item update(Item item) {
        try {
            var response = (JSONObject) post(
                    getUrl("update"),
                    item
            );

            return new Item(response);
        } catch (Exception ignored) {
            displayWarning("Can't update item",
                    "Invalid item and/or not signed in");
            return null;
        }
    }

    public static Item update(JSONObject item) {
        return update(new Item(item));
    }
}
