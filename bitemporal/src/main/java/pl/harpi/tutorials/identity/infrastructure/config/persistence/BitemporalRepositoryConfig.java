package pl.harpi.tutorials.identity.infrastructure.config.persistence;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.harpi.tutorials.common.temporal.domain.port.repository.BitemporalEntityRepository;
import pl.harpi.tutorials.common.temporal.infrastructure.repository.JpaBitemporalEntityRepository;
import pl.harpi.tutorials.identity.infrastructure.adapters.output.persistence.entity.Person;

@Configuration
public class BitemporalRepositoryConfig {
  @Bean
  @Qualifier("personRepository")
  public BitemporalEntityRepository<Long, String, Person.Identity, Person.Instance> bitemporalEntityRepository() {
    return new JpaBitemporalEntityRepository<>(Person.Identity.class, Person.Version.class, Person.Instance.class);
  }
}
