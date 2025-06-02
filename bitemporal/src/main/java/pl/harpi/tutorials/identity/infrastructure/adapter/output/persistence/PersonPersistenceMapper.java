package pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence;

import java.util.List;
import java.util.Optional;

import lombok.val;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.harpi.tutorials.common.temporal.domain.model.RecordData;
import pl.harpi.tutorials.identity.domain.model.Person;
import pl.harpi.tutorials.identity.domain.model.PersonUpdate;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonPersistenceMapper {
  PersonEntity.Identity toIdentity(Person person);

  PersonEntity.Instance toInstance(Person person);

  PersonEntity.Instance toInstance(PersonUpdate personUpdate);

  Person toDomain(PersonEntity.Identity identity, PersonEntity.Instance instance);

  default Optional<Person> toDomain(
      RecordData<Long, String, PersonEntity.Identity, PersonEntity.Instance> recordData) {
    return Optional.ofNullable(recordData).map(rd -> toDomain(rd.identity(), rd.instance()));
  }

  default List<Person> toDomain(
      List<RecordData<Long, String, PersonEntity.Identity, PersonEntity.Instance>> recordDataList) {
    val result = new java.util.ArrayList<Person>();
    recordDataList.stream().map(this::toDomain).forEach(person -> person.ifPresent(result::add));

    return result;
  }
}
