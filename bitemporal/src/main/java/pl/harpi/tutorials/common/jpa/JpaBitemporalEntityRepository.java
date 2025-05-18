package pl.harpi.tutorials.common.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.springframework.transaction.annotation.Transactional;
import pl.harpi.tutorials.common.DateHelper;

@Getter
public class JpaBitemporalEntityRepository<
        D extends AbstractIdentity, I extends AbstractInstance, V extends AbstractVersion<I>>
    implements BitemporalEntityRepository<D, I> {
  @PersistenceContext private EntityManager entityManager;

  private final Class<D> clazzIdentity;
  private final Class<V> clazzVersion;
  private final Class<I> clazzInstance;

  @AllArgsConstructor
  private class QuerySet {
    private final CriteriaBuilder cb;
    private final CriteriaQuery<Object> criteriaQuery;
    private final Root<D> objectIdentity;
    private final Root<V> objectVersion;
    private final Root<I> objectInstance;
  }

  public JpaBitemporalEntityRepository(
      Class<D> clazzIdentity, Class<V> clazzVersion, Class<I> clazzInstance) {
    this.clazzIdentity = clazzIdentity;
    this.clazzVersion = clazzVersion;
    this.clazzInstance = clazzInstance;
  }

  @Override
  @Transactional
  public String insert(D identity, I instance, LocalDateTime validDate) {
    val currentDateTime = DateHelper.getCurrentDateTime();

    getEntityManager().persist(identity);

    V version;
    try {
      version = clazzVersion.getConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e); // TODO: dedicated exception
    }

    version.setRecordInterval(JpaInterval.of(currentDateTime, JpaInterval.MAX_DATE_TIME));
    version.setValidInterval(JpaInterval.of(validDate, JpaInterval.MAX_DATE_TIME));
    version.setIdentityId(identity.getId());
    version.setObjectInstance(instance);

    getEntityManager().persist(version);

    return identity.getLogicalId();
  }

  protected V cloneVersion(V version) {
    try {
      V clone = getClazzVersion().getConstructor().newInstance();

      clone.setIdentityId(version.getIdentityId());
      clone.setObjectInstance(version.getObjectInstance());
      clone.setRecordInterval(
          (version.getRecordInterval() == null) ? null : version.getRecordInterval().copy());
      clone.setValidInterval(
          (version.getValidInterval() == null) ? null : version.getValidInterval().copy());

      return clone;
    } catch (InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      throw new RuntimeException(e); // TODO: dedicated exception
    }
  }

  private void updateLeft(
      V version, JpaInterval validInterval, LocalDateTime newRecordDateFrom, List<V> changeList) {
    if (version.getValidInterval().getStart().isBefore(validInterval.getStart())) {
      V newVersion = cloneVersion(version);

      newVersion.setRecordInterval(JpaInterval.of(newRecordDateFrom, JpaInterval.MAX_DATE_TIME));

      newVersion.setValidInterval(
          JpaInterval.of(
              newVersion.getValidInterval().getStart(), validInterval.getStart().plusSeconds(-1)));

      changeList.add(newVersion);
    }
  }

  private void updateRight(
      V version, JpaInterval validInterval, LocalDateTime newRecordDateFrom, List<V> changeList) {
    if (version.getValidInterval().getEnd().isAfter(validInterval.getEnd())) {
      V newVersion = cloneVersion(version);

      newVersion.setRecordInterval(JpaInterval.of(newRecordDateFrom, JpaInterval.MAX_DATE_TIME));

      newVersion.setValidInterval(
          JpaInterval.of(
              validInterval.getEnd().plusSeconds(1), newVersion.getValidInterval().getEnd()));

      changeList.add(newVersion);
    }
  }

  @Override
  @Transactional
  public void update(Long id, I instance, LocalDateTime validDateFrom, LocalDateTime validDateTo) {
    val currentDateTime = DateHelper.getCurrentDateTime();
    val validInterval = JpaInterval.of(validDateFrom, validDateTo);

    val versions = fetchAllActualObjectVersions(id);

    if (versions.isEmpty()) {
      throw new EntityNotFoundException("Object with id " + id + " not found");
    }

    val changeList = new ArrayList<V>();
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

    try {
      val version = getClazzVersion().getConstructor().newInstance();

      version.setIdentityId(id);
      version.setObjectInstance(instance);
      version.setRecordInterval(JpaInterval.of(currentDateTime, JpaInterval.MAX_DATE_TIME));
      version.setValidInterval(validInterval);

      changeList.add(version);
    } catch (Exception e) {
      throw new RuntimeException(e); // TODO: dedicated exception
    }

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

  private List<V> fetchAllActualObjectVersions(Long id) {
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
  protected List<AbstractObject<D, I>> fetchObjects(Query query) {
    val resultList = new ArrayList<AbstractObject<D, I>>();

    val objectList = (List<Object[]>) query.getResultList();

    if (objectList == null || objectList.isEmpty()) {
      return resultList;
    }

    for (Object[] obj : objectList) {
      val identity = ((D) obj[0]);
      val version = ((V) obj[1]);

      resultList.add(new AbstractObject<>(identity, version.getObjectInstance()));
    }

    return resultList;
  }

  @Override
  public List<AbstractObject<D, I>> find(
          Long id, LocalDateTime recordDate, LocalDateTime validDate) {
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
  public List<AbstractObject<D, I>> findAll(LocalDateTime recordDate, LocalDateTime validDate) {
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
            .setParameter(paramValidDate.getName(), validDate);

    return fetchObjects(query);
  }
}
