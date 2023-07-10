package eformer.back.eformer_backend.api.v1;

import eformer.back.eformer_backend.model.Order;
import eformer.back.eformer_backend.model.User;
import eformer.back.eformer_backend.repository.OrderRepository;
import eformer.back.eformer_backend.repository.UserRepository;
import eformer.back.eformer_backend.utility.auth.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/api/v1/orders/")
public class OrdersApi extends BaseApi {
    final OrderRepository manager;

    final UserRepository usersManager;

    final JwtService jService;

    public OrdersApi(OrderRepository manager,
                     UserRepository usersManager,
                     JwtService jService) {
        super(jService, usersManager);
        this.manager = manager;
        this.usersManager = usersManager;
        this.jService = jService;
    }

    public ResponseEntity<Object> getStatistics(HashMap<String, String> header,
                                                Integer type) {
        if (!canUserChange(header)) {
            System.out.println(header);
            System.out.println(extractUser(header));
            /* 403 */
            return new ResponseEntity<>("User is not a manager", HttpStatus.FORBIDDEN);
        }

        Object result;

        switch (type) {
            case 1 -> result = manager.getTotalSales();
            case 2 -> result = manager.getAllPaid();
            case 3 -> result = manager.getTotalSoldQuantity();
            case 4 -> result = manager.getTotalActualSales();
            case 5 -> result = manager.findAll();
            case 6 -> result = manager.getTotalProfit();
            default -> result = null;
        }

        if (result == null) {
            /* 400 */
            return new ResponseEntity<>("Internal error, contact IT", HttpStatus.BAD_REQUEST);
        }

        /* 200 */
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity<Object> getOrders(HashMap<String, String> header,
                                           String date,
                                           boolean isAfter) {
        try {
            if (date == null || header == null) {
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
                    isAfter ? manager.findAllByCreationDateAfter(processToDate(date)) :
                            manager.findAllByCreationDateBefore(processToDate(date)),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            /* Prevent potential server crash */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); /* 400 */
        }
    }

    @PostMapping("getById")
    @ResponseBody
    public ResponseEntity<Object> getById(
            @RequestHeader HashMap<String, String> header,
            @RequestBody Integer id
    ) {
        try {
            if (canUserChange(header)) {
                /* 200 */
                return new ResponseEntity<>(manager.findById(id).orElseThrow(), HttpStatus.OK);
            }

            /* 403 */
            return new ResponseEntity<>("Sender is not an employee",
                    HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getAllByCustomer")
    @ResponseBody
    public ResponseEntity<Object> getByCustomer(
            @RequestHeader HashMap<String, String> header,
            @RequestBody User customer
    ) {
        try {
            if (canUserChange(header)) {
                /* 200 */
                return new ResponseEntity<>(manager.findAllByCustomer(customer), HttpStatus.OK);
            }

            /* 403 */
            return new ResponseEntity<>("Sender is not an employee",
                    HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getAllByEmployee")
    @ResponseBody
    public ResponseEntity<Object> getByEmployee(
            @RequestHeader HashMap<String, String> header,
            @RequestBody User employee
    ) {
        try {
            if (canUserChange(header)) {
                /* 200 */
                return new ResponseEntity<>(manager.findAllByEmployee(employee), HttpStatus.OK);
            }

            /* 403 */
            return new ResponseEntity<>("Sender is not an employee",
                    HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Must include `customer` & `employee` in the body
     * */
    @PostMapping("getAllByCustomerAndEmployee")
    @ResponseBody
    public ResponseEntity<Object> getByCustomerAndEmployee(
            @RequestHeader HashMap<String, String> header,
            @RequestBody HashMap<String, Integer> users
    ) {
        try {
            var customerId = users.get("customer");
            var employeeId = users.get("employee");

            var customer = usersManager.findById(customerId).orElseThrow();
            var employee = usersManager.findById(employeeId).orElseThrow();

            if (canUserChange(header)) {
                /* 200 */
                return new ResponseEntity<>(manager.findAllByCustomerAndEmployee(customer, employee),
                        HttpStatus.OK);
            }

            /* 403 */
            return new ResponseEntity<>("Sender is not an employee",
                    HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getAllByStatus")
    @ResponseBody
    public ResponseEntity<Object> getByStatus(
            @RequestHeader HashMap<String, String> header,
            @RequestBody String status
    ) {
        try {
            if (canUserChange(header)) {
                /* 200 */
                return new ResponseEntity<>(manager.findAllByStatus(status), HttpStatus.OK);
            }

            /* 403 */
            return new ResponseEntity<>("Sender is not an employee",
                    HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getAllBeforeDate")
    @ResponseBody
    public ResponseEntity<Object> getByDateBefore(
            @RequestHeader HashMap<String, String> header,
            @RequestBody String date
    ) {
        return getOrders(header, date, false);
    }

    @PostMapping("getAllAfterDate")
    @ResponseBody
    public ResponseEntity<Object> getByDateAfter(
            @RequestHeader HashMap<String, String> header,
            @RequestBody String date
    ) {
        return getOrders(header, date, true);
    }

    @PostMapping("getAllBetweenDates")
    @ResponseBody
    public ResponseEntity<Object> getByDateBetween(
            @RequestHeader HashMap<String, String> header,
            @RequestBody HashMap<String, String> body
    ) {
        try {
            var start = body.getOrDefault("start", null);
            var end = body.getOrDefault("end", null);


            if (start == null || end == null || header == null) {
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
                    manager.findAllByCreationDateBetween(processToDate(start),
                            processToDate(end)),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            /* Prevent potential server crash */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); /* 400 */
        }
    }

    @PostMapping("getTotalSales")
    @ResponseBody
    public ResponseEntity<Object> getTotalSales(
            @RequestHeader HashMap<String, String> header
    ) {
        return getStatistics(header, 1);
    }

    @PostMapping("getAllPaid")
    @ResponseBody
    public ResponseEntity<Object> getAllPaid(
            @RequestHeader HashMap<String, String> header
    ) {
        return getStatistics(header, 2);
    }

    @PostMapping("getTotalSoldQuantity")
    @ResponseBody
    public ResponseEntity<Object> getTotalQuantity(
            @RequestHeader HashMap<String, String> header
    ) {
        return getStatistics(header, 3);
    }

    @PostMapping("getTotalActualSales")
    @ResponseBody
    public ResponseEntity<Object> getTotalActualSales(
            @RequestHeader HashMap<String, String> header
    ) {
        return getStatistics(header, 4);
    }

    @PostMapping("getAll")
    @ResponseBody
    public ResponseEntity<Object> getAll(
            @RequestHeader HashMap<String, String> header
    ) {
        return getStatistics(header, 5);
    }

    @PostMapping("getTotalProfit")
    @ResponseBody
    public ResponseEntity<Object> getProfit(
            @RequestHeader HashMap<String, String> header
    ) {
        return getStatistics(header, 6);
    }

    /**
     * Requires:
     *  orderId: The ID of the order
     *  amountPaid: Amount paid for the order
     * */
    @PostMapping("confirm")
    @ResponseBody
    public ResponseEntity<Object> confirm(
            @RequestHeader HashMap<String, String> header,
            @RequestBody HashMap<String, Object> body
    ) {
        try {
            var orderId = (Integer) body.get("orderId");
            Double amountPaid;

            try {
                amountPaid = (Double) body.get("amountPaid");
            } catch (ClassCastException ignored) {
                amountPaid = Double.valueOf((Integer) body.get("amountPaid"));
            }

            if (!canUserChange(header)) {
                /* 403 */
                return new ResponseEntity<>("User is not an employee", HttpStatus.FORBIDDEN);
            }

            var order = manager.findById(orderId).orElseThrow();
            order.confirm(amountPaid);

            manager.save(order);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("cancel")
    @ResponseBody
    public ResponseEntity<Object> cancel(
            @RequestHeader HashMap<String, String> header,
            @RequestBody Integer orderId
    ) {
        try {
            if (!canUserChange(header)) {
                /* 403 */
                return new ResponseEntity<>("User is not an employee", HttpStatus.FORBIDDEN);
            }

            var order = manager.findById(orderId).orElseThrow();
            order.cancel();

            manager.save(order);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("update")
    @ResponseBody
    public ResponseEntity<Object> update(
            @RequestHeader HashMap<String, String> header,
            @RequestBody HashMap<String, Integer> props
    ) {
        try {
            var employee = extractUser(header);
            var order = manager.findById(props.get("orderId")).orElseThrow();

            if (!order.getEmployee().equals(employee) && !employee.isManager()) {
                /* 403 */
                return new ResponseEntity<>("User is not an employee or does not manage order", HttpStatus.FORBIDDEN);
            }

            props.remove("orderId");

            order.setItems(props);

            return new ResponseEntity<>(manager.save(order), HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings("unchecked")
    @PostMapping("create")
    @ResponseBody
    public ResponseEntity<Object> create(
            @RequestHeader HashMap<String, String> header,
            @RequestBody HashMap<String, Object> body
    ) {
        Order order = null;

        try {
            var customerId = (Integer) body.get("customerId");
            var customer = usersManager.findById(customerId).orElseThrow();
            var items = (HashMap<String, Integer>) body.get("items");
            var employee = extractUser(header);
            var note = (String) body.getOrDefault("note", "");

            if (!employee.isEmployee()) {
                /* 403 */
                return new ResponseEntity<>("User is not an employee", HttpStatus.FORBIDDEN);
            }

            var primitiveOrder = new Order(customer, employee);
            primitiveOrder.setNote(note);

            if (!primitiveOrder.areValidItems(items)) {
                /* 422 */
                return new ResponseEntity<>("Quantities too large", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            order = manager.save(primitiveOrder);
            order.addItems(items);

            /* 200 */
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(order != null ? order.getOrderId() : e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getOrderItems")
    @ResponseBody
    public ResponseEntity<Object> getOrderItems(
            @RequestHeader HashMap<String, String> header,
            @RequestBody Integer orderId
    ) {
        try {
            if (canUserChange(header)) {
                /* 200 */
                return new ResponseEntity<>(Order
                                .orderItemsManager
                                .findAllInItemIds(manager.findById(orderId).orElseThrow()),
                        HttpStatus.OK);
            }

            /* 403 */
            return new ResponseEntity<>("Sender is not an employee",
                    HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
