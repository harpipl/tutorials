package pl.harpi.tutorials.common.base.infrastructure.jpa;

import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class ImmutablePersistentEntity<I extends Serializable> extends PersistentEntity<I> {}
