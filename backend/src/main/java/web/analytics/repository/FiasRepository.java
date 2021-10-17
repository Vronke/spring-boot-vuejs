package web.analytics.repository;

import org.springframework.data.repository.CrudRepository;
import web.analytics.domain.Fias;

public interface FiasRepository extends CrudRepository<Fias, Long> {
}
