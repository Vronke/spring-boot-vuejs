package web.analytics.repository;


import web.analytics.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByUsername(@Param("username") String username);
    List<User> findById(@Param("id") int id);
}
