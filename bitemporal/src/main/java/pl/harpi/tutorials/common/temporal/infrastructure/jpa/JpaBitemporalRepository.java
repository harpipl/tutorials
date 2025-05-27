package pl.harpi.tutorials.common.temporal.infrastructure.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import pl.harpi.tutorials.common.temporal.domain.exception.TemporalEntityException;
import pl.harpi.tutorials.common.temporal.domain.model.TemporalIdentity;
import pl.harpi.tutorials.common.temporal.domain.port.repository.BitemporalRepository;
import pl.harpi.tutorials.common.base.infrastructure.jpa.JpaInterval;
import pl.harpi.tutorials.common.temporal.domain.model.RecordData;
import pl.harpi.tutorials.common.util.DateHelper;

@Getter
@RequiredArgsConstructor
public class JpaBitemporalRepository<
        TD extends TemporalIdentity<Long, String>,
        TI extends AbstractTemporalInstance,
        TV extends AbstractBitemporalVersion<TI>>
    implements BitemporalRepository<Long, String, TD, TI> {
  @PersistenceContext private EntityManager entityManager;

  private final Class<TD> clazzIdentity;
  private final Class<TV> clazzVersion;
  private final Class<TI> clazzInstance;

  @AllArgsConstructor
  private class QuerySet {
    private final CriteriaBuilder cb;
    private final CriteriaQuery<Object> criteriaQuery;
    private final Root<TD> objectIdentity;
    private final Root<TV> objectVersion;
    private final Root<TI> objectInstance;
  }

  private TV createNewVersionInstance() {
    try {
      return clazzVersion.getConstructor().newInstance();
    } catch (Exception e) {
      throw new TemporalEntityException("Could not create version instance", e);
    }
  }

  @Override
  @Transactional
  public String insert(TD identity, TI instance, LocalDateTime validDate) {
    val currentDateTime = DateHelper.getCurrentDateTime();

    getEntityManager().persist(identity);

    TV version = createNewVersionInstance();

    version.setRecordInterval(JpaInterval.of(currentDateTime, JpaInterval.MAX_DATE_TIME));
    version.setValidInterval(JpaInterval.of(validDate, JpaInterval.MAX_DATE_TIME));
    version.setIdentityId(identity.getId());
    version.setObjectInstance(instance);

    getEntityManager().persist(version);

    return identity.getLogicalId();
  }

  protected TV cloneVersion(TV version) {
    TV clone = createNewVersionInstance();

    clone.setIdentityId(version.getIdentityId());
    clone.setObjectInstance(version.getObjectInstance());
    clone.setRecordInterval(
        (version.getRecordInterval() == null) ? null : version.getRecordInterval().copy());
    clone.setValidInterval(
        (version.getValidInterval() == null) ? null : version.getValidInterval().copy());

    return clone;
  }

  private void updateLeft(
      TV version, JpaInterval validInterval, LocalDateTime newRecordDateFrom, List<TV> changeList) {
    if (version.getValidInterval().getStart().isBefore(validInterval.getStart())) {
      TV newVersion = cloneVersion(version);

      newVersion.setRecordInterval(JpaInterval.of(newRecordDateFrom, JpaInterval.MAX_DATE_TIME));

      newVersion.setValidInterval(
          JpaInterval.of(
              newVersion.getValidInterval().getStart(), validInterval.getStart().plusSeconds(-1)));

      changeList.add(newVersion);
    }
  }

  private void updateRight(
      TV version, JpaInterval validInterval, LocalDateTime newRecordDateFrom, List<TV> changeList) {
    if (version.getValidInterval().getEnd().isAfter(validInterval.getEnd())) {
      TV newVersion = cloneVersion(version);

      newVersion.setRecordInterval(JpaInterval.of(newRecordDateFrom, JpaInterval.MAX_DATE_TIME));

      newVersion.setValidInterval(
          JpaInterval.of(
              validInterval.getEnd().plusSeconds(1), newVersion.getValidInterval().getEnd()));

      changeList.add(newVersion);
    }
  }

  @Override
  @Transactional
  public void update(Long id, TI instance, LocalDateTime validDateFrom, LocalDateTime validDateTo) {
    val currentDateTime = DateHelper.getCurrentDateTime();
    val validInterval = JpaInterval.of(validDateFrom, validDateTo);

    val versions = fetchAllActualObjectVersions(id);

    if (versions.isEmpty()) {
      throw new EntityNotFoundException("Object with id " + id + " not found");
    }

    val changeList = new ArrayList<TV>();
    for (val version : versions) {
      if (version.getValidInterval().hasCommonPart(validInterval)) {
        updateLeft(version, validInterval, currentDateTime, changeList);
        updateRight(version, validInterval, currentDateTime, changeList);

        if (version.isActive()) {
          version.setRecordInterval(
              JpaInterval.of(
                  version.getRecordInterval().getStart(), currentDateTime.plusSeconds(-1)));
          changeList.add(version);
        }
      }
    }

    val version = createNewVersionInstance();

    version.setIdentityId(id);
    version.setObjectInstance(instance);
    version.setRecordInterval(JpaInterval.of(currentDateTime, JpaInterval.MAX_DATE_TIME));
    version.setValidInterval(validInterval);

    changeList.add(version);

    for (val v : changeList) {
      if (v.getId() == null) {
        getEntityManager().persist(v);
      } else {
        getEntityManager().merge(v);
      }
    }
  }

  @Override
  public Long findIdByLogicalId(String logicalId) {
    val cb = getEntityManager().getCriteriaBuilder();

    val paramUUID = cb.parameter(String.class, "logicalId");

    val criteriaQuery = cb.createQuery(getClazzIdentity());
    val o = criteriaQuery.from(getClazzIdentity());
    criteriaQuery.select(o).where(cb.equal(o.get("logicalId"), paramUUID));

    val query =
        getEntityManager().createQuery(criteriaQuery).setParameter(paramUUID.getName(), logicalId);

    val objectList = query.getResultList();

    if (objectList != null && !objectList.isEmpty()) {
      return objectList.get(0).getId();
    } else {
      return null;
    }
  }

  private List<TV> fetchAllActualObjectVersions(Long id) {
    val cb = getEntityManager().getCriteriaBuilder();

    val paramId = cb.parameter(Long.class, "id");
    val paramEnd = cb.parameter(LocalDateTime.class, "end");

    val criteriaQuery = cb.createQuery(getClazzVersion());
    val v = criteriaQuery.from(getClazzVersion());

    criteriaQuery
        .select(v)
        .where(
            cb.equal(v.get("identityId"), paramId),
            cb.equal(v.get("recordInterval").get("end"), paramEnd));

    val query =
        getEntityManager()
            .createQuery(criteriaQuery)
            .setParameter(paramId.getName(), id)
            .setParameter(paramEnd.getName(), JpaInterval.MAX_DATE_TIME);

    val objectList = query.getResultList();

    return (objectList == null) ? new ArrayList<>() : objectList;
  }

  private QuerySet prepareQuery() {
    val cb = getEntityManager().getCriteriaBuilder();

    val criteriaQuery = cb.createQuery();

    val objectIdentity = criteriaQuery.from(getClazzIdentity());
    val objectVersion = criteriaQuery.from(getClazzVersion());
    val objectInstance = criteriaQuery.from(getClazzInstance());

    criteriaQuery.multiselect(objectIdentity, objectVersion, objectInstance);

    return new QuerySet(cb, criteriaQuery, objectIdentity, objectVersion, objectInstance);
  }

  @SuppressWarnings("unchecked")
  protected List<RecordData<Long, String, TD, TI>> fetchObjects(Query query) {
    val resultList = new ArrayList<RecordData<Long, String, TD, TI>>();

    val objectList = (List<Object[]>) query.getResultList();

    if (objectList == null || objectList.isEmpty()) {
      return resultList;
    }

    for (Object[] obj : objectList) {
      val identity = ((TD) obj[0]);
      val version = ((TV) obj[1]);

      resultList.add(new RecordData<>(identity, version.getObjectInstance()));
    }

    return resultList;
  }

  @Override
  public List<RecordData<Long, String, TD, TI>> find(Long id, LocalDateTime recordDate, LocalDateTime validDate) {
    val q = prepareQuery();

    val paramId = q.cb.parameter(Long.class, "id");
    val paramRecordDate = q.cb.parameter(LocalDateTime.class, "rd");
    val paramValidDate = q.cb.parameter(LocalDateTime.class, "vd");

    q.criteriaQuery.where(
        q.cb.equal(paramId, q.objectIdentity.get("id")),
        q.cb.equal(q.objectIdentity.get("id"), q.objectVersion.get("identityId")),
        q.cb.equal(q.objectInstance.get("id"), q.objectVersion.get("objectInstance").get("id")),
        q.cb.between(
            paramRecordDate,
            q.objectVersion.get("recordInterval").get(JpaInterval.TXT_START),
            q.objectVersion.get("recordInterval").get(JpaInterval.TXT_END)),
        q.cb.between(
            paramValidDate,
            q.objectVersion.get("validInterval").get(JpaInterval.TXT_START),
            q.objectVersion.get("validInterval").get(JpaInterval.TXT_END)));

    val query =
        getEntityManager()
            .createQuery(q.criteriaQuery)
            .setParameter(paramId.getName(), id)
            .setParameter(paramRecordDate.getName(), recordDate)
            .setParameter(paramValidDate.getName(), validDate);

    return fetchObjects(query);
  }

  @Override
  public List<RecordData<Long, String, TD, TI>> findAll(
      LocalDateTime recordDate, LocalDateTime validDate, Pageable pageable) {
    val q = prepareQuery();

    val paramRecordDate = q.cb.parameter(LocalDateTime.class, "rd");
    val paramValidDate = q.cb.parameter(LocalDateTime.class, "vd");

    q.criteriaQuery.where(
        q.cb.equal(q.objectIdentity.get("id"), q.objectVersion.get("identityId")),
        q.cb.equal(q.objectInstance.get("id"), q.objectVersion.get("objectInstance").get("id")),
        q.cb.between(
            paramRecordDate,
            q.objectVersion.get("recordInterval").get(JpaInterval.TXT_START),
            q.objectVersion.get("recordInterval").get(JpaInterval.TXT_END)),
        q.cb.between(
            paramValidDate,
            q.objectVersion.get("validInterval").get(JpaInterval.TXT_START),
            q.objectVersion.get("validInterval").get(JpaInterval.TXT_END)));

    val query =
        getEntityManager()
            .createQuery(q.criteriaQuery)
            .setParameter(paramRecordDate.getName(), recordDate)
            .setParameter(paramValidDate.getName(), validDate)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());

    return fetchObjects(query);
  }
}
