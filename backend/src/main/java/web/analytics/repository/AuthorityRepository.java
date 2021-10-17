package web.analytics.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import web.analytics.domain.Authority;

import java.util.List;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {
    List<Authority> findByUsername(@Param("username") String username);
}
