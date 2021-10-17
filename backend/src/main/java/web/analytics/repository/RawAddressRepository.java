package web.analytics.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import web.analytics.domain.RawAddress;

import java.util.List;

public interface RawAddressRepository extends CrudRepository<RawAddress, Long> {
    List<RawAddress> findByRawAddressAndInn(@Param("rawAddress") String rawAddress, @Param("inn") String inn);
}
