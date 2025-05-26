package pl.harpi.tutorials.common.base.infrastructure.entity;

import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class ImmutablePersistentEntity<I extends Serializable> extends PersistentEntity<I> {}
