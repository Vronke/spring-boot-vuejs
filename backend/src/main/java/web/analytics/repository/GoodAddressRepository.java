package web.analytics.repository;

import org.springframework.data.repository.CrudRepository;
import web.analytics.domain.GoodAddress;

public interface GoodAddressRepository extends CrudRepository<GoodAddress, Long> {
}
