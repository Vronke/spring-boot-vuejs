package web.analytics.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import web.analytics.domain.ActionLog;

import java.util.List;

public interface ActionLogRepository extends CrudRepository<ActionLog, Long> {
    List<ActionLog> findByTypeId(@Param("typeId") int typeId);
}
