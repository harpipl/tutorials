package pl.harpi.tutorials.identity.domain.port.input;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import pl.harpi.tutorials.identity.domain.model.Person;
import pl.harpi.tutorials.identity.domain.model.PersonUpdate;

public interface PersonUseCase {
  Optional<Person> find(String logicalId, LocalDateTime recordDate, LocalDateTime validDate);

  List<Person> findAll(LocalDateTime recordDate, LocalDateTime validDate, Pageable pageable);

  List<Person> findAllByLastName(
      String lastName, LocalDateTime recordDate, LocalDateTime validDate, Pageable pageable);

  String insert(Person person, LocalDateTime validDate);

  void update(String logicalId, PersonUpdate person, LocalDateTime validDateFrom, LocalDateTime validDateTo);


}
