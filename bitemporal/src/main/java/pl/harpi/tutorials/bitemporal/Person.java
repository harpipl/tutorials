package pl.harpi.tutorials.bitemporal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.harpi.tutorials.common.jpa.AbstractIdentity;
import pl.harpi.tutorials.common.jpa.AbstractInstance;
import pl.harpi.tutorials.common.jpa.AbstractVersion;

public class Person {
  @Getter
  @Setter
  @Entity
  @NoArgsConstructor
  @Table(schema = "DEMO", name = "PERSON")
  public static class Identity extends AbstractIdentity {
    @Column(name = "PESEL")
    private String pesel;
  }

  @Getter
  @Setter
  @Entity
  @NoArgsConstructor
  @Table(schema = "DEMO", name = "PERSON_V")
  public static class Version extends AbstractVersion<Instance> {}

  @Getter
  @Setter
  @Entity
  @NoArgsConstructor
  @Table(schema = "DEMO", name = "PERSON_I")
  public static class Instance extends AbstractInstance {
    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;
  }
}
