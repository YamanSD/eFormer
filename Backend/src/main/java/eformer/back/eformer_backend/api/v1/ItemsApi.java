package eformer.back.eformer_backend.api.v1;

import eformer.back.eformer_backend.model.Item;
import eformer.back.eformer_backend.repository.ItemRepository;
import eformer.back.eformer_backend.repository.UserRepository;
import eformer.back.eformer_backend.utility.auth.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/api/v1/items/")
public class ItemsApi extends BaseApi {
    final ItemRepository manager;

    public ItemsApi(ItemRepository manager, JwtService jService, UserRepository userRepo) {
        super(jService, userRepo);
        this.manager = manager;
    }

    public StringBuilder checkItem(Item item) {
        var error = new StringBuilder();

        if (item.getQuantity() <= 0) {
            error.append(String.format("Invalid quantity %d must be a positive integer\n",
                    item.getQuantity()));
        }

        if (manager.existsByNameIgnoreCase(item.getName())) {
            error.append(String.format("Invalid item name '%s' already taken\n",
                    item.getName()));
        }

        if (item.getUnitPrice() <= 0) {
            error.append(String.format("Invalid unit price %f must be a positive number\n",
                    item.getUnitPrice()));
        }

        if (item.getCost() <= 0) {
            error.append(String.format("Invalid cost price %f must be a positive number\n",
                    item.getCost()));
        }

        if (item.getItemId() > 0) {
            error.append("Cannot supply own ID\n");
        }

        return error;
    }

    public StringBuilder checkItemForUpdate(HashMap<String, Object> item) {
        var error = new StringBuilder();

        var quantity = (Integer) item.get("quantity");
        var name = (String) item.get("name");
        Double unitPrice;
        Double cost;

        try {
            unitPrice = (Double) item.get("unitPrice");
        } catch (ClassCastException ignored) {
            unitPrice = Double.valueOf((Integer) item.get("unitPrice"));
        }

        try {
            cost = (Double) item.get("cost");
        } catch (ClassCastException ignored) {
            cost = Double.valueOf((Integer) item.get("cost"));
        }

        var itemId = (Integer) item.get("itemId");

        if (quantity != null && quantity <= 0) {
            error.append(String.format("Invalid quantity %d must be a positive integer\n",
                    quantity));
        }

        if (name != null && !manager.existsByNameIgnoreCase(name)) {
            error.append(String.format("Invalid item '%s' does not exist\n",
                    name));
        }

        if (unitPrice != null && unitPrice <= 0) {
            error.append(String.format("Invalid unit price %f must be a positive number\n",
                    unitPrice));
        }

        if (cost != null && cost <= 0) {
            error.append(String.format("Invalid cost price %f must be a positive number\n",
                    cost));
        }

        if (!manager.existsById(itemId)) {
            error.append(String.format("Item (ID: %d) does not exist\n", itemId));
        }

        return error;
    }

    @GetMapping("getById")
    @ResponseBody
    public ResponseEntity<Object> getItemById(@RequestParam(name = "id") Integer itemId) {
        try {
            return manager.findById(itemId)
                    .<ResponseEntity<Object>>map(item -> new ResponseEntity<>(item, HttpStatus.OK)) /* 200 */
                    .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY)); /* 422 */
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("getByName")
    @ResponseBody
    public ResponseEntity<Object> getItemByName(@RequestParam(name = "name") String name) {
        try {
            return manager.findByName(name)
                    .<ResponseEntity<Object>>map(item -> new ResponseEntity<>(item, HttpStatus.OK)) /* 200 */
                    .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY)); /* 422 */
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("getAll")
    @ResponseBody
    public ResponseEntity<Object> getItems() {
        try {
            /* 200 */
            return new ResponseEntity<>(manager.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * An item must contain:
     *  name: String
     *  description: String
     *  quantity: Integer
     *  unitPrice: Double
     * */
    @PostMapping("create")
    @ResponseBody
    public ResponseEntity<Object> create(@RequestHeader HashMap<String, String> header,
                                         @RequestBody Item item) {
        try {
            if (!canUserChange(header)) {
                return new ResponseEntity<>("User is not an employe",
                        HttpStatus.FORBIDDEN);
            }

            var error = checkItem(item);

            if (error.length() > 0) {
                return new ResponseEntity<>(error.toString(), HttpStatus.UNPROCESSABLE_ENTITY); /* 422 */
            }

            /* 200 */
            return new ResponseEntity<>(manager.save(item), HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getAllAfter")
    @ResponseBody
    public ResponseEntity<Object> getItemsAfter(@RequestBody String date) {
        try {
            /* 200 */
            return new ResponseEntity<>(manager.findAllByIntroductionDateAfter(processToDate(date)), HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("getAllBefore")
    @ResponseBody
    public ResponseEntity<Object> getItemsBefore(@RequestBody String date) {
        try {
            /* 200 */
            return new ResponseEntity<>(manager.findAllByIntroductionDateBefore(processToDate(date)), HttpStatus.OK);
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * An Item Object must be given.
     * */
    @PostMapping("update")
    @ResponseBody
    public ResponseEntity<Object> updateItem(
            @RequestHeader HashMap<String, String> header,
            @RequestBody HashMap<String, Object> props) {
        try {
            if (!canUserChange(header)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            var error = checkItemForUpdate(props);

            if (error.length() > 0) {
                return new ResponseEntity<>(error.toString(), HttpStatus.UNPROCESSABLE_ENTITY); /* 422 */
            }

            var item = manager.findById((Integer) props.get("itemId")).orElseThrow();

            props.remove("itemId");
            props.remove("introductionDate");
            props.remove("IntroductionDate");

            for (var prop: props.keySet()) {
                try {
                    var value = props.get(prop);

                    /* Use reflection to call setters */
                    Item.class.getDeclaredMethod(
                            "set"
                                    + prop.substring(0, 1).toUpperCase()
                                    + prop.substring(1),
                            value.getClass()
                    ).invoke(item, value);
                } catch (Exception ignored) {
                }
            }

            return new ResponseEntity<>(manager.save(item), HttpStatus.OK); /* 200 */
        } catch (Exception e) {
            /* 400 */
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
