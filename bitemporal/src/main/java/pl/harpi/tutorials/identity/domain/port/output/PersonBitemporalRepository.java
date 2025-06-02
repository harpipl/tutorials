package pl.harpi.tutorials.identity.domain.port.output;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import pl.harpi.tutorials.common.temporal.domain.port.repository.BitemporalRepository;
import pl.harpi.tutorials.identity.domain.model.Person;
import pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence.PersonEntity;

public interface PersonBitemporalRepository
    extends BitemporalRepository<Long, String, PersonEntity.Identity, PersonEntity.Instance> {

  List<Person> findAllByLastName(
      String lastName, LocalDateTime recordDate, LocalDateTime validDate, Pageable pageable);
}
