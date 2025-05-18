package pl.harpi.tutorials.bitemporal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.harpi.tutorials.common.jpa.BitemporalEntityRepository;
import pl.harpi.tutorials.common.jpa.JpaInterval;

@RestController
@RequestMapping("/api")
public class PersonController {
  private final PersonMapper personMapper;
  private final BitemporalEntityRepository<Person.Identity, Person.Instance> personRepository;

  public PersonController(
      PersonMapper personMapper,
      @Qualifier("personRepository")
          BitemporalEntityRepository<Person.Identity, Person.Instance> personRepository) {
    this.personMapper = personMapper;
    this.personRepository = personRepository;
  }

  @PostMapping("/person/{validDate}")
  public ResponseEntity<String> createPerson(
      @RequestBody CreatePersonDto createPersonDto, @PathVariable String validDate) {
    val personIdentity =
        personMapper.identityFromCreatePersonDto(UUID.randomUUID().toString(), createPersonDto);
    val personInstance = personMapper.instanceFromCreatePersonDto(createPersonDto);

    val validDateDT = LocalDateTime.parse(validDate, JpaInterval.PATH_DATE_TIME);

    val logicalId = personRepository.insert(personIdentity, personInstance, validDateDT);

    return ResponseEntity.ok(logicalId);
  }

  @PutMapping("/person/{logicalId}/{validDate}")
  public ResponseEntity<String> updatePerson(
      @RequestBody UpdatePersonDto updatePersonDto,
      @PathVariable String logicalId,
      @PathVariable String validDate) {
    val personInstance = personMapper.instanceFromUpdatePersonDto(updatePersonDto);

    val validDateDT = LocalDateTime.parse(validDate, JpaInterval.PATH_DATE_TIME);

    val personId = personRepository.findIdByLogicalId(logicalId);

    personRepository.update(personId, personInstance, validDateDT, JpaInterval.MAX_DATE_TIME);

    return ResponseEntity.ok(logicalId);
  }

  @PutMapping("/person/{logicalId}/{validDateFrom}/{validDateTo}")
  public ResponseEntity<String> updatePerson(
      @RequestBody UpdatePersonDto updatePersonDto,
      @PathVariable String logicalId,
      @PathVariable String validDateFrom,
      @PathVariable String validDateTo) {
    val personInstance = personMapper.instanceFromUpdatePersonDto(updatePersonDto);

    val validDateFromDT = LocalDateTime.parse(validDateFrom, JpaInterval.PATH_DATE_TIME);
    val validDateToDT = LocalDateTime.parse(validDateTo, JpaInterval.PATH_DATE_TIME);

    val personId = personRepository.findIdByLogicalId(logicalId);

    personRepository.update(personId, personInstance, validDateFromDT, validDateToDT);

    return ResponseEntity.ok(logicalId);
  }

  private ResponseEntity<List<FindAllPersonDto>> getPerson(
      String logicalId, LocalDateTime recordDate, LocalDateTime validDate) {

    val id = personRepository.findIdByLogicalId(logicalId);
    val allPersons = personRepository.find(id, recordDate, validDate);

    val result = new ArrayList<FindAllPersonDto>();

    for (val person : allPersons) {
      result.add(
          new FindAllPersonDto(
              person.getIdentity().getPesel(),
              person.getInstance().getFirstName(),
              person.getInstance().getLastName()));
    }

    return ResponseEntity.ok(result);
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
}
