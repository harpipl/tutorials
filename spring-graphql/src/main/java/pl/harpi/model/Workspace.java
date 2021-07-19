package pl.harpi.model;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import static pl.harpi.model.JPAConstants.*;

@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = TABLE_WORKSPACE)
@DiscriminatorValue(value = OBJECT_WORKSPACE_DISCRIMINATOR)
public final class Workspace extends AbstractObject {
    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_VISIBILITY, nullable = false, length = TYPE_LENGTH)
    private VisibilityType visibility;
}
