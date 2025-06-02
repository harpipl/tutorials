package pl.harpi.tutorials.identity.infrastructure.adapter.input.web;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.harpi.tutorials.identity.domain.model.Person;
import pl.harpi.tutorials.identity.domain.model.PersonUpdate;
import pl.harpi.tutorials.identity.infrastructure.adapter.input.web.dto.CreatePersonDto;
import pl.harpi.tutorials.identity.infrastructure.adapter.input.web.dto.FindAllPersonDto;
import pl.harpi.tutorials.identity.infrastructure.adapter.input.web.dto.UpdatePersonDto;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonMapper {
  Person toDomain(String logicalId, CreatePersonDto createPersonDto);

  PersonUpdate toDomain(UpdatePersonDto updatePersonDto);

  List<FindAllPersonDto> toDto(List<Person> personList);
}
