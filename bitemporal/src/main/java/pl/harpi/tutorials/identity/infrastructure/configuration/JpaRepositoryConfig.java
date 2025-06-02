package pl.harpi.tutorials.identity.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.harpi.tutorials.identity.application.service.PersonService;
import pl.harpi.tutorials.identity.domain.port.input.PersonUseCase;
import pl.harpi.tutorials.identity.domain.port.output.PersonBitemporalRepository;
import pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence.JpaPersonBitemporalRepository;
import pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence.PersonEntity;
import pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence.PersonPersistenceMapper;

@Configuration
public class JpaRepositoryConfig {
  @Bean
  @Qualifier("personBitemporalUseCase")
  public PersonUseCase personUseCase(
          PersonBitemporalRepository personBitemporalRepository, PersonPersistenceMapper personPersistenceMapper) {
    return new PersonService(personBitemporalRepository, personPersistenceMapper);
  }

  @Bean
  public PersonBitemporalRepository personBitemporalRepository() {
    return new JpaPersonBitemporalRepository(
        PersonEntity.Identity.class, PersonEntity.Version.class, PersonEntity.Instance.class);
  }
}
