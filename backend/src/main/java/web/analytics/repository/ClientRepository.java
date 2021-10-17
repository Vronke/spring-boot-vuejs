package web.analytics.repository;

import org.springframework.data.repository.CrudRepository;
import web.analytics.domain.Clients;

public interface ClientRepository extends CrudRepository<Clients, Long> {
}
