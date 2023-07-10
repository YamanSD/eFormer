package eformer.front.eformer_frontend.connector;

import eformer.front.eformer_frontend.model.Item;
import eformer.front.eformer_frontend.model.Order;
import eformer.front.eformer_frontend.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdersConnector extends RequestsGateway {
    private static final String baseUrl = "orders/";

    private static String getUrl(String target) {
        return baseUrl + target;
    }

    public static List<Order> proccessOrdersList(Object response) {
        var result = new ArrayList<Order>();

        for (var json: (JSONArray) response) {
            result.add(new Order((JSONObject) json));
        }

        return result;
    }

    public static Order getById(Integer id) {
        try {
            var response = (JSONObject) post(getUrl("getById"),
                    id
            );

            return new Order(response);
        } catch (Exception e) {
            displayWarning("Can't fetch order",
                    "Invalid ID or/and not signed in\n" + e);
            return null;
        }
    }

    public static List<Order> getAllByCustomer(User customer) {
        try {
            var response = post(getUrl("getAllByCustomer"),
                    customer);

            return proccessOrdersList(response);
        } catch (Exception e) {
            displayWarning("Can't fetch orders",
                    "Invalid customer or/and not signed in\n" + e);
            return null;
        }
    }

    public static List<Order> getAllByEmployee(User employee) {
        try {
            var response = post(getUrl("getAllByEmployee"),
                    employee);

            return proccessOrdersList(response);
        } catch (Exception e) {
            displayWarning("Can't fetch orders",
                    "Invalid employee or/and not signed in\n" + e);
            return null;
        }
    }

    public static List<Order> getAllByCustomerAndEmployee(User customer,
                                                          User employee) {
        try {
            var body = new JSONObject();

            body.put("customer", customer.getUserId());
            body.put("employee", employee.getUserId());

            var response = post(getUrl("getAllByCustomerAndEmployee"),
                    body);

            return proccessOrdersList(response);
        } catch (Exception e) {
            displayWarning("Can't fetch orders",
                    "Invalid customer " +
                            "or/and employee" +
                            " or/and not signed in");
            return null;
        }
    }

    public static List<Order> getAllByStatus(String status) {
        try {
            var response = post(getUrl("getAllByStatus"),
                    status);

            return proccessOrdersList(response);
        } catch (Exception e) {
            displayWarning("Can't fetch orders",
                    "Invalid status or/and not signed in\n" + e);
            return null;
        }
    }

    public static List<Order> getAll() {
        try {
            var response = post(getUrl("getAll"), null);

            return proccessOrdersList(response);
        } catch (Exception e) {
            displayWarning("Can't fetch orders",
                    "Not signed in or check connection\n" + e);
            return null;
        }
    }

    public static List<Order> getAllBeforeDate(LocalDateTime date) {
        try {
            var response = post(getUrl("getAllBeforeDate"),
                    date);

            return proccessOrdersList(response);
        } catch (Exception e) {
            displayWarning("Can't fetch orders",
                    "Invalid date or/and not signed in\n" + e);
            return null;
        }
    }

    public static List<Order> getAllAfterDate(LocalDateTime date) {
        try {
            var response = post(getUrl("getAllAfterDate"),
                    date);

            return proccessOrdersList(response);
        } catch (Exception e) {
            displayWarning("Can't fetch orders",
                    "Invalid date or/and not signed in\n" + e);
            return null;
        }
    }

    public static List<Order> getAllBetween(LocalDateTime start,
                                            LocalDateTime end) {
        try {
            var dates = new JSONObject();

            dates.put("start", start);
            dates.put("end", end);

            var response = post(getUrl("getAllBetweenDates"),
                    dates);

            return proccessOrdersList(response);
        } catch (Exception e) {
            displayWarning("Can't fetch orders",
                    "Invalid date or/and not signed in\n" + e);
            return null;
        }
    }

    public static Double getTotalSales() {
        try {
            return Double.parseDouble((String) post(getUrl("getTotalSales"),
                    null));
        } catch (Exception e) {
            displayWarning("Can't fetch information",
                    "Not signed in or check connection\n" + e);
            return 0.0;
        }
    }

    public static List<Order> getAllPaid() {
        try {
            var response = post(getUrl("getAllPaid"),
                    null);

            return proccessOrdersList(response);
        } catch (Exception e) {
            displayWarning("Can't fetch information",
                    "Not signed in or check connection\n" + e);
            return null;
        }
    }

    public static Integer getTotalSoldQuantity() {
        try {
            return Integer.parseInt((String) post(getUrl("getTotalSoldQuantity"),
                    null));
        } catch (Exception e) {
            displayWarning("Can't fetch information",
                    "Not signed in or check connection\n" + e);
            return 0;
        }
    }

    public static Double getTotalActualSales() {
        try {
            return Double.parseDouble((String) post(getUrl("getTotalActualSales"),
                    null));
        } catch (Exception e) {
            displayWarning("Can't fetch information",
                    "Not signed in or check connection\n" + e);
            return 0.0;
        }
    }

    public static Double getTotalProfit() {
        try {
            return Double.parseDouble((String) post(getUrl("getTotalProfit"),
                    null));
        } catch (Exception e) {
            displayWarning("Can't fetch information",
                    "Not signed in or check connection\n" + e);
            return 0.0;
        }
    }

    public static void confirm(Integer orderId, Double amountPaid) {
        try {
            var body = new JSONObject();

            body.put("orderId", orderId);
            body.put("amountPaid", amountPaid);

            post(getUrl("confirm"), body);
        } catch (Exception e) {
            displayWarning("Can't fetch information",
                    "Not signed in or check connection\n" + e);
        }
    }

    public static void cancel(Integer orderId) {
        try {
            post(getUrl("cancel"), orderId);
        } catch (Exception e) {
            displayWarning("Can't fetch information",
                    "Not signed in or check connection\n" + e);
        }
    }

    public static Order update(Integer orderId, JSONObject items) {
        try {
            items.put("orderId", orderId);
            var response = (JSONObject) post(getUrl("update"), items);

            return new Order(response);
        } catch (Exception e) {
            displayWarning("Can't update order",
                    "Invalid order or/and not signed in\n" + e);
            return null;
        }
    }

    public static Order create(Integer customerId,
                               JSONObject items,
                               String note) {
        try {
            var body = new JSONObject();

            body.put("items", items);
            body.put("customerId", customerId);
            body.put("note", note);

            var response = (JSONObject) post(getUrl("create"), body);

            return new Order(response);
        } catch (Exception e) {
            displayWarning("Can't create order",
                    "Invalid order or/and not signed in\n" + e);
            return null;
        }
    }

    public static List<Item> processItems(Object response) {
        var result = new ArrayList<Item>();

        for (var json: (JSONArray) response) {
            var values = (JSONArray) json;
            var item = new Item(values.getJSONObject(0));
            item.setRequestedQuantity(values.getInt(1));

            result.add(item);
        }

        return result;
    }

    public static List<Item> getItems(Integer orderId) {
        try {
            var response = post(getUrl("getOrderItems"), orderId);

            return processItems(response);
        } catch (Exception e) {
            displayWarning("Can't get order items",
                    "Invalid order or/and not signed in\n" + e);
            return null;
        }
    }
}
