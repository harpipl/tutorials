package pl.harpi.tutorials.bitemporal;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
  Person.Identity identityFromCreatePersonDto(String logicalId, CreatePersonDto createPersonDto);
  Person.Instance instanceFromCreatePersonDto(CreatePersonDto createPersonDto);
  Person.Instance instanceFromUpdatePersonDto(UpdatePersonDto updatePersonDto);
}
