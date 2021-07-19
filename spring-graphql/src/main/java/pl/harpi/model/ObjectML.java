package pl.harpi.model;

import lombok.Getter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import static pl.harpi.model.JPAConstants.*;

@Getter
@Entity
@Table(name = TABLE_OBJECT_ML)
public final class ObjectML extends AbstractPersistable<Long> {
    @Column(name = COLUMN_NAME, nullable = false, length = NAME_LENGTH)
    private String name;

    @Lob
    @Column(name = COLUMN_DESCRIPTION)
    private String description;

    @Column(name = COLUMN_COUNTRY_CODE, length = COUNTRY_CODE_LENGTH, nullable = false)
    private String countryCode;

}
