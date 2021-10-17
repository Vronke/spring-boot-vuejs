package web.analytics.repository;

import org.springframework.data.repository.CrudRepository;
import web.analytics.domain.BuyPoint;

public interface BuyPointRepository extends CrudRepository<BuyPoint, Long> {
}
