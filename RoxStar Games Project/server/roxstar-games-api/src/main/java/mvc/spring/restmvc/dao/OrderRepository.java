package mvc.spring.restmvc.dao;

import mvc.spring.restmvc.model.Order;
import mvc.spring.restmvc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Transactional(readOnly = true)
    Set<Order> findByUser(User user);

    @Transactional(readOnly = true)
    Set<Order> findByStatus(String status);
}
