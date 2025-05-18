package pl.harpi.tutorials.common.jpa;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractIdentity extends ImmutablePersistentEntity<Long> {
    @Column(name = "logicalId", nullable = false, updatable = false, unique = true, length = 40)
    private String logicalId;
}
