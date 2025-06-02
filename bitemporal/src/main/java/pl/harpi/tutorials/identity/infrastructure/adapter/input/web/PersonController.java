package pl.harpi.tutorials.identity.infrastructure.adapter.input.web;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.harpi.tutorials.common.base.infrastructure.jpa.JpaInterval;
import pl.harpi.tutorials.common.util.DateHelper;
import pl.harpi.tutorials.identity.domain.model.Person;
import pl.harpi.tutorials.identity.domain.model.PersonUpdate;
import pl.harpi.tutorials.identity.domain.port.input.PersonUseCase;
import pl.harpi.tutorials.identity.infrastructure.adapter.input.web.dto.CreatePersonDto;
import pl.harpi.tutorials.identity.infrastructure.adapter.input.web.dto.FindAllPersonDto;
import pl.harpi.tutorials.identity.infrastructure.adapter.input.web.dto.UpdatePersonDto;

@RestController
@RequestMapping("/api")
public class PersonController {
  private final PersonMapper personMapper;
  private final PersonUseCase personUseCase;

  public PersonController(
      PersonMapper personMapper,
      @Qualifier("personBitemporalUseCase") PersonUseCase personUseCase) {
    this.personMapper = personMapper;
    this.personUseCase = personUseCase;
  }

  @PostMapping("/person/{validDate}")
  public ResponseEntity<String> createPerson(
      @RequestBody CreatePersonDto createPersonDto, @PathVariable String validDate) {

    val validDateDT = LocalDateTime.parse(validDate, JpaInterval.PATH_DATE_TIME);
    val personCreate = personMapper.toDomain(UUID.randomUUID().toString(), createPersonDto);
    val logicalId = personUseCase.insert(personCreate, validDateDT);

    return ResponseEntity.ok(logicalId);
  }

  @PutMapping("/person/{logicalId}/{validDate}")
  public ResponseEntity<String> updatePerson(
      @RequestBody UpdatePersonDto updatePersonDto,
      @PathVariable String logicalId,
      @PathVariable String validDate) {

    val validDateDT = LocalDateTime.parse(validDate, JpaInterval.PATH_DATE_TIME);
    val personUpdate = personMapper.toDomain(updatePersonDto);

    personUseCase.update(logicalId, personUpdate, validDateDT, JpaInterval.MAX_DATE_TIME);

    return ResponseEntity.ok(logicalId);
  }

  @PutMapping("/person/{logicalId}/{validDateFrom}/{validDateTo}")
  public ResponseEntity<String> updatePerson(
      @RequestBody UpdatePersonDto updatePersonDto,
      @PathVariable String logicalId,
      @PathVariable String validDateFrom,
      @PathVariable String validDateTo) {

    val validDateFromDT = LocalDateTime.parse(validDateFrom, JpaInterval.PATH_DATE_TIME);
    val validDateToDT = LocalDateTime.parse(validDateTo, JpaInterval.PATH_DATE_TIME);
    val personUpdate = new PersonUpdate(updatePersonDto.firstName(), updatePersonDto.lastName());

    personUseCase.update(logicalId, personUpdate, validDateFromDT, validDateToDT);

    return ResponseEntity.ok(logicalId);
  }

  private List<FindAllPersonDto> convertPersonData(List<Person> allPersons) {
    return personMapper.toDto(allPersons);
  }

  private ResponseEntity<List<FindAllPersonDto>> getPerson(
      String logicalId, LocalDateTime recordDate, LocalDateTime validDate) {

    val allPersons = personUseCase.find(logicalId, recordDate, validDate);

    return ResponseEntity.ok(convertPersonData(allPersons.stream().toList()));
  }

  private ResponseEntity<List<FindAllPersonDto>> getPersons(
      LocalDateTime recordDate, LocalDateTime validDate) {

    val allPersons = personUseCase.findAll(recordDate, validDate, PageRequest.of(0, 3));

    return ResponseEntity.ok(convertPersonData(allPersons));
  }

  @GetMapping("/person/{logicalId}/{validDate}")
  public ResponseEntity<List<FindAllPersonDto>> getPerson(
      @PathVariable String logicalId, @PathVariable String validDate) {
    val validDateDT = LocalDateTime.parse(validDate, JpaInterval.PATH_DATE_TIME);

    return getPerson(logicalId, JpaInterval.MAX_DATE_TIME, validDateDT);
  }

  @GetMapping("/person/{logicalId}/{recordDate}/{validDate}")
  public ResponseEntity<List<FindAllPersonDto>> getPerson(
      @PathVariable String logicalId,
      @PathVariable String recordDate,
      @PathVariable String validDate) {
    val recordDateDT = LocalDateTime.parse(recordDate, JpaInterval.PATH_DATE_TIME);
    val validDateDT = LocalDateTime.parse(validDate, JpaInterval.PATH_DATE_TIME);

    return getPerson(logicalId, recordDateDT, validDateDT);
  }

  @GetMapping("/persons/{recordDate}/{validDate}")
  public ResponseEntity<List<FindAllPersonDto>> getPersons(
      @PathVariable String recordDate, @PathVariable String validDate) {
    val recordDateDT = LocalDateTime.parse(recordDate, JpaInterval.PATH_DATE_TIME);
    val validDateDT = LocalDateTime.parse(validDate, JpaInterval.PATH_DATE_TIME);

    return getPersons(recordDateDT, validDateDT);
  }

  @GetMapping("/persons/search/{lastName}")
  public ResponseEntity<List<FindAllPersonDto>> getPersonsByLastName(
      @PathVariable String lastName) {
    return ResponseEntity.ok(
        convertPersonData(
            personUseCase.findAllByLastName(
                lastName,
                DateHelper.getCurrentDateTime(),
                DateHelper.getCurrentDateTime(),
                PageRequest.of(0, 3))));
  }
}
