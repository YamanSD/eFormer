package eformer.back.eformer_backend.api.v1;

import eformer.back.eformer_backend.repository.ItemRepository;
import eformer.back.eformer_backend.repository.OrderItemsRepository;
import eformer.back.eformer_backend.repository.OrderRepository;
import eformer.back.eformer_backend.repository.UserRepository;
import eformer.back.eformer_backend.utility.auth.JwtService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats/")
public class StatisticsApi extends BaseApi {
    final UserRepository userRepo;

    final OrderRepository orderRepo;

    final ItemRepository itemRepo;

    final OrderItemsRepository orderItemsRepo;

    public StatisticsApi(UserRepository userRepo, OrderRepository orderRepo,
                         ItemRepository itemRepo, OrderItemsRepository orderItemsRepo,
                         JwtService jService) {
        super(jService, userRepo);
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.orderItemsRepo = orderItemsRepo;
    }
}
