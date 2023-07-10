package eformer.back.eformer_backend;

import eformer.back.eformer_backend.api.v1.request.RegisterRequest;
import eformer.back.eformer_backend.model.Order;
import eformer.back.eformer_backend.repository.ItemRepository;
import eformer.back.eformer_backend.repository.OrderItemsRepository;
import eformer.back.eformer_backend.repository.OrderRepository;
import eformer.back.eformer_backend.repository.UserRepository;
import eformer.back.eformer_backend.utility.auth.AuthenticationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    final OrderItemsRepository orderItemsRepo;

    final OrderRepository orderRepo;

    final UserRepository userRepo;

    final ItemRepository itemRepo;

    final AuthenticationService authService;

    public Application(OrderItemsRepository orderItemsRepo, OrderRepository orderRepo,
                       UserRepository userRepo, ItemRepository itemRepo, AuthenticationService authService) {
        this.orderItemsRepo = orderItemsRepo;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
        this.authService = authService;

        try {
            authService.register(new RegisterRequest("Admin", "admin@eformer.com",
                    "Admin", "123456789", 2)
            );
        } catch (Exception ignored) {}

        Order.setOrderItemsManager(orderItemsRepo);
        Order.setItemsManager(itemRepo);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
