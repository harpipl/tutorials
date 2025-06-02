package pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import pl.harpi.tutorials.common.temporal.infrastructure.jpa.JpaBitemporalRepository;
import pl.harpi.tutorials.identity.domain.model.Person;
import pl.harpi.tutorials.identity.domain.port.output.PersonBitemporalRepository;

public class JpaPersonBitemporalRepository
    extends JpaBitemporalRepository<PersonEntity.Identity, PersonEntity.Instance, PersonEntity.Version>
    implements PersonBitemporalRepository {

  public JpaPersonBitemporalRepository(
      Class<PersonEntity.Identity> clazzIdentity,
      Class<PersonEntity.Version> clazzVersion,
      Class<PersonEntity.Instance> clazzInstance) {
    super(clazzIdentity, clazzVersion, clazzInstance);
  }

  @Override
  public List<Person> findAllByLastName(
      String lastName, LocalDateTime recordDate, LocalDateTime validDate, Pageable pageable) {
    return getEntityManager()
        .createQuery(
            """
                    SELECT
                        new pl.harpi.tutorials.identity.domain.model.Person(d.logicalId, d.pesel, i.firstName, i.lastName)
                    FROM
                        pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence.PersonEntity$Identity d
                        JOIN pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence.PersonEntity$Version v ON d.id = v.identityId
                        JOIN pl.harpi.tutorials.identity.infrastructure.adapter.output.persistence.PersonEntity$Instance i ON v.objectInstance = i
                    WHERE
                        :recordDate between v.recordInterval.start and v.recordInterval.end
                        and :validDate between v.recordInterval.start and v.recordInterval.end
                        and i.lastName = :lastName
                    """,
            Person.class)
        .setParameter("lastName", lastName)
        .setParameter("recordDate", recordDate)
        .setParameter("validDate", validDate)
        .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
        .setMaxResults(pageable.getPageSize())
        .getResultList();
  }
}
