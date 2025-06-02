package pl.harpi.tutorials.identity.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.harpi.tutorials.identity.domain.model.Person;
import pl.harpi.tutorials.identity.domain.model.PersonUpdate;
import pl.harpi.tutorials.identity.domain.port.input.PersonUseCase;
import pl.harpi.tutorials.identity.domain.port.output.PersonBitemporalRepository;
import pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence.PersonPersistenceMapper;

@Service
@RequiredArgsConstructor
public class PersonService implements PersonUseCase {
  private final PersonBitemporalRepository personBitemporalRepository;
  private final PersonPersistenceMapper personPersistenceMapper;

  @Override
  public Optional<Person> find(
      String logicalId, LocalDateTime recordDate, LocalDateTime validDate) {
    val id = personBitemporalRepository.findIdByLogicalId(logicalId);
    val recordData = personBitemporalRepository.find(id, recordDate, validDate);

    return recordData.flatMap(personPersistenceMapper::toDomain);
  }

  @Override
  public List<Person> findAll(
      LocalDateTime recordDate, LocalDateTime validDate, Pageable pageable) {
    val recordDataList = personBitemporalRepository.findAll(recordDate, validDate, pageable);
    return personPersistenceMapper.toDomain(recordDataList);
  }

  @Override
  public List<Person> findAllByLastName(
      String lastName, LocalDateTime recordDate, LocalDateTime validDate, Pageable pageable) {
    return personBitemporalRepository.findAllByLastName(lastName, recordDate, validDate, pageable);
  }

  @Override
  public String insert(Person person, LocalDateTime validDate) {
    val identity = personPersistenceMapper.toIdentity(person);
    val instance = personPersistenceMapper.toInstance(person);

    return personBitemporalRepository.insert(identity, instance, validDate);
  }

  @Override
  public void update(
      String logicalId,
      PersonUpdate personUpdate,
      LocalDateTime validDateFrom,
      LocalDateTime validDateTo) {
    val id = personBitemporalRepository.findIdByLogicalId(logicalId);
    val instance = personPersistenceMapper.toInstance(personUpdate);

    personBitemporalRepository.update(id, instance, validDateFrom, validDateTo);
  }
}
