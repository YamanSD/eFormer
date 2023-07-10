package eformer.back.eformer_backend.repository;

import eformer.back.eformer_backend.model.Item;
import eformer.back.eformer_backend.model.Order;
import eformer.back.eformer_backend.model.OrderItem;
import eformer.back.eformer_backend.model.keys.OrderItemId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface OrderItemsRepository extends CrudRepository<OrderItem, OrderItemId> {
    Optional<OrderItem> findById(OrderItemId id);

    List<OrderItem> findAllByItem(Item item);

    List<OrderItem> findAllByOrder(Order order);

    @Query("SELECT i.item, i.quantity as soldQuantity FROM OrderItem i WHERE i.order = ?1")
    List<Object> findAllInItemIds(Order order);

    @Query("SELECT SUM(o.quantity) FROM OrderItem o WHERE o.item = :item")
    Integer getSoldItemQuantity(@Param("item") Item item);

    void deleteAllByOrder(Order order);

    void deleteById(OrderItemId id);
}
