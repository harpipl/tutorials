package pl.harpi.model;

import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Persistable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

@SuperBuilder
@MappedSuperclass
public abstract class AbstractPersistable<D extends Serializable> implements Persistable<D> {
    @Id
    @Nullable
    @GeneratedValue
    private D id;

    public AbstractPersistable() {
    }

    @Nullable
    public D getId() {
        return this.id;
    }

    @Transient
    public boolean isNew() {
        return null == this.getId();
    }
}