package pl.harpi.model;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

import static pl.harpi.model.JPAConstants.*;

@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = TABLE_OBJECT, uniqueConstraints = {
        @UniqueConstraint(name = TABLE_OBJECT_CONSTRAINT_TYPE_NAME, columnNames = {COLUMN_OBJECT_DISCRIMINATOR, COLUMN_NAME})})
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = COLUMN_OBJECT_DISCRIMINATOR, length = DISCRIMINATOR_LENGTH)
public abstract class AbstractObject extends AbstractPersistable<Long> {
    @Column(name = COLUMN_NAME, nullable = false, length = NAME_LENGTH)
    private String name;

    @Lob
    @Column(name = COLUMN_DESCRIPTION)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = COLUMN_JOIN_OBJECT_ID)
    private List<ObjectML> mls;

    public List<ObjectML> getMls() {
        return mls;
    }
}
