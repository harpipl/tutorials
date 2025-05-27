package pl.harpi.tutorials.identity.infrastructure.adapters.input.rest.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.harpi.tutorials.common.temporal.domain.model.RecordData;
import pl.harpi.tutorials.identity.infrastructure.adapters.input.rest.dto.CreatePersonDto;
import pl.harpi.tutorials.identity.infrastructure.adapters.input.rest.dto.FindAllPersonDto;
import pl.harpi.tutorials.identity.infrastructure.adapters.input.rest.dto.UpdatePersonDto;
import pl.harpi.tutorials.identity.infrastructure.adapters.output.persistence.jpa.Person;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
  Person.Identity identityFromCreatePersonDto(String logicalId, CreatePersonDto createPersonDto);

  Person.Instance instanceFromCreatePersonDto(CreatePersonDto createPersonDto);

  Person.Instance instanceFromUpdatePersonDto(UpdatePersonDto updatePersonDto);

  List<FindAllPersonDto> findAllPersonDto(List<RecordData<Long, String, Person.Identity, Person.Instance>> allPersons);

  @Mapping(target = "logicalId", source = "person.identity.logicalId")
  @Mapping(target = "pesel", source = "person.identity.pesel")
  @Mapping(target = "firstName", source = "person.instance.firstName")
  @Mapping(target = "lastName", source = "person.instance.lastName")
  FindAllPersonDto findAllPersonDto(RecordData<Long, String, Person.Identity, Person.Instance> person);
}
