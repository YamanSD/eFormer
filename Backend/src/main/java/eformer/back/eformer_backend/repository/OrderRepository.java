package eformer.back.eformer_backend.repository;

import eformer.back.eformer_backend.model.Order;
import eformer.back.eformer_backend.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Integer> {
    List<Order> findAllByCustomer(User customer);

    List<Order> findAllByEmployee(User customer);

    List<Order> findAllByCustomerAndEmployee(User customer, User employee);

    List<Order> findAllByStatus(String status);

    List<Order> findAllByCreationDateAfter(Date date);

    List<Order> findAllByCreationDateBefore(Date date);

    List<Order> findAllByCreationDateBetween(Date start, Date end);

    @Query("SELECT SUM(o.total) FROM Order o")
    Double getTotalSales();

    @Query("SELECT SUM(o.numberOfItems) FROM Order o")
    Integer getTotalSoldQuantity();

    @Query("SELECT SUM(o.amountPaid) FROM Order o")
    Double getTotalActualSales();

    @Query("SELECT o FROM Order o WHERE o.amountPaid IS NOT NULL AND o.amountPaid > 0")
    List<Order> getAllPaid();

    @Query("SELECT SUM(o.profit) FROM Order o")
    Double getTotalProfit();
}
