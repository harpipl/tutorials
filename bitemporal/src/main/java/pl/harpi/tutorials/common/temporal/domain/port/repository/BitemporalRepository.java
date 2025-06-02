package pl.harpi.tutorials.common.temporal.domain.port.repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import pl.harpi.tutorials.common.temporal.domain.model.TemporalIdentity;
import pl.harpi.tutorials.common.temporal.domain.model.TemporalInstance;
import pl.harpi.tutorials.common.temporal.domain.model.RecordData;

public interface BitemporalRepository<
    I extends Serializable,
    L extends Serializable,
    TD extends TemporalIdentity<I, L>,
    TI extends TemporalInstance<I>> {
  String insert(TD identity, TI instance, LocalDateTime validDate);

  void update(I id, TI instance, LocalDateTime validDateFrom, LocalDateTime validDateTo);

  Long findIdByLogicalId(L logicalId);

  Optional<RecordData<I, L, TD, TI>> find(I id, LocalDateTime recordDate, LocalDateTime validDate);

  List<RecordData<I, L, TD, TI>> findAll(
      LocalDateTime recordDate, LocalDateTime validDate, Pageable pageable);
}
