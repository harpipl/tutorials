package pl.harpi.tutorials.identity.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.harpi.tutorials.common.temporal.infrastructure.entity.AbstractBitemporalVersion;
import pl.harpi.tutorials.common.temporal.infrastructure.entity.AbstractTemporalIdentity;
import pl.harpi.tutorials.common.temporal.infrastructure.entity.AbstractTemporalInstance;

public class Person {
  @Getter
  @Setter
  @Entity
  @NoArgsConstructor
  @Table(schema = "DEMO", name = "PERSON")
  public static class Identity extends AbstractTemporalIdentity {
    @Column(name = "PESEL")
    private String pesel;
  }

  @Getter
  @Setter
  @Entity
  @NoArgsConstructor
  @Table(schema = "DEMO", name = "PERSON_V")
  public static class Version extends AbstractBitemporalVersion<Instance> {}

  @Getter
  @Setter
  @Entity
  @NoArgsConstructor
  @Table(schema = "DEMO", name = "PERSON_I")
  public static class Instance extends AbstractTemporalInstance {
    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;
  }
}
