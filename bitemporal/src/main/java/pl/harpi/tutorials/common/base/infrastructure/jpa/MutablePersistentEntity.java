package pl.harpi.tutorials.common.base.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class MutablePersistentEntity<I extends Serializable, V extends Serializable>
    extends PersistentEntity<I> {
  @Version
  @Column(name = "VER", nullable = false)
  private V version;
}
