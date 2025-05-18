package pl.harpi.tutorials.common.jpa;

import java.time.LocalDateTime;
import java.util.List;

public interface BitemporalEntityRepository<
    D extends AbstractIdentity, I extends AbstractInstance> {
  String insert(D identity, I instance, LocalDateTime validDate);

  void update(Long id, I instance, LocalDateTime validDateFrom, LocalDateTime validDateTo);

  Long findIdByLogicalId(String logicalId);

    List<AbstractObject<D, I>> find(
            Long id, LocalDateTime recordDate, LocalDateTime validDate);

    List<AbstractObject<D, I>> findAll(LocalDateTime recordDate, LocalDateTime validDate);
}
