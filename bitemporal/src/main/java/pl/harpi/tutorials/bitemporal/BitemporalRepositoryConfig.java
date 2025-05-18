package pl.harpi.tutorials.bitemporal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.harpi.tutorials.common.jpa.BitemporalEntityRepository;
import pl.harpi.tutorials.common.jpa.JpaBitemporalEntityRepository;

@Configuration
public class BitemporalRepositoryConfig {
  @Bean
  @Qualifier("personRepository")
  public BitemporalEntityRepository<Person.Identity, Person.Instance> bitemporalEntityRepository() {
    return new JpaBitemporalEntityRepository<>(Person.Identity.class, Person.Version.class, Person.Instance.class);
  }
}
