package eformer.back.eformer_backend.model.keys;

import java.io.Serializable;
import java.util.Objects;


/**
 * Class used as a representation of the OrderItem Entity's composite key.
 */
public class OrderItemId implements Serializable {
    private Integer item;

    private Integer order;

    private Integer quantity;

    public OrderItemId(Integer itemId, Integer orderId, Integer quantity) {
        setItem(itemId);
        setOrder(orderId);
        setQuantity(quantity);
    }

    public OrderItemId(Integer itemId, Integer orderId) {
        this(itemId, orderId, 0);
    }

    public OrderItemId() {
        this(-1, -1);
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = Math.max(quantity, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItemId that)) return false;
        return Objects.equals(getItem(), that.getItem()) && Objects.equals(getOrder(), that.getOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItem(), getOrder());
    }
}
