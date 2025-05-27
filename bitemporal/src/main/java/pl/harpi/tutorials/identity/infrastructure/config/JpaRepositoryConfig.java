package pl.harpi.tutorials.identity.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.harpi.tutorials.common.temporal.domain.port.repository.BitemporalRepository;
import pl.harpi.tutorials.common.temporal.infrastructure.jpa.JpaBitemporalRepository;
import pl.harpi.tutorials.identity.infrastructure.adapters.output.persistence.jpa.Person;

@Configuration
public class JpaRepositoryConfig {
  @Bean
  @Qualifier("personBitemporalRepository")
  public BitemporalRepository<Long, String, Person.Identity, Person.Instance> personBitemporalRepository() {
    return new JpaBitemporalRepository<>(Person.Identity.class, Person.Version.class, Person.Instance.class);
  }
}
