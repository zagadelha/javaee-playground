package javaee8.repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import javaee8.entity.AbstractEntity;

public abstract class AbstractRepository<T extends AbstractEntity> {

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	// @Getter
	@PersistenceContext(unitName = "javaee-pu")
	private EntityManager entityManager;

	@SuppressWarnings({ "all" })
	public Class<T> getPersistenceClazz() {
		return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public T findById(Long id) {
		return this.entityManager.find(getPersistenceClazz(), id);
	}

	public T find(Class<T> clazz, Object id) {
		return this.entityManager.find(clazz, id);
	}

	public List<T> findAll() {
		try {
			return findAll(getPersistenceClazz());
		} catch (NoResultException ne) {
			return new ArrayList<T>();
		} catch (Exception e) {
			throw e;
		}
	}

	public List<T> findAll(Class<T> clazz) {
		return findAll(clazz, 0, 0);
	}

	public List<T> findAll(int resultLimit, int firstResult) {
		return findAll(getPersistenceClazz(), resultLimit, firstResult);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll(Class<T> clazz, int resultLimit, int firstResult) {

		StringBuilder sql = new StringBuilder().append("select o from ").append(clazz.getName()).append(" o WHERE 1=1 ");

		Query query = this.entityManager.createQuery(sql.toString());

		if (resultLimit > 0) {
			query.setMaxResults(resultLimit);
		}

		query.setFirstResult(firstResult);

		return query.getResultList();
	}

	/**
	 * Returns the number of records that meet the criteria
	 *
	 * @param <O>
	 * @param namedQueryName
	 * @param parameters
	 * @return List
	 */
	public <O extends Object> List<O> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
		return findWithNamedQuery(namedQueryName, parameters, 0, 0);
	}

	/**
	 * Returns the number of records that meet the criteria
	 *
	 * @param namedQueryName
	 * @param parameters
	 * @return T
	 */
	public T findWithNamedQuerySingle(String namedQueryName, Map<String, Object> parameters) {
		List<T> result = findWithNamedQuery(namedQueryName, parameters, 0, 0);
		return (result != null && !result.isEmpty()) ? result.get(0) : null;
	}

	/**
	 * Returns the number of records that meet the criteria with parameter map and
	 * result limit
	 *
	 * @param <O>
	 * @param namedQueryName
	 * @param parameters
	 * @param resultLimit
	 * @param firstResult
	 * @return List
	 */
	@SuppressWarnings("all")
	public <O extends Object> List<O> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit, int firstResult) {
		Query query = this.entityManager.createNamedQuery(namedQueryName);
		if (resultLimit > 0) {
			query.setMaxResults(resultLimit);
		}

		query.setFirstResult(firstResult);

		if (null != parameters) {
			Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
			for (Map.Entry<String, Object> entry : rawParameters) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.getResultList();
	}

	/**
	 * Buscar Lista por namedQuery com parametros definidos.
	 * 
	 * @param <O>
	 * @param namedQueryName
	 * @param parameters
	 * @param resultLimit
	 * @param firstResult
	 * @param parametersByTemporalType
	 * @return
	 */
	@SuppressWarnings("all")
	public <O extends Object> List<O> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit, int firstResult,
			Map<String, TemporalType> parametersByTemporalType) {

		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.entityManager.createNamedQuery(namedQueryName);

		if (resultLimit > 0) {
			query.setMaxResults(resultLimit);
		}

		query.setFirstResult(firstResult);

		for (Map.Entry<String, Object> entry : rawParameters) {

			TemporalType type = null;

			if (parametersByTemporalType != null) {
				type = parametersByTemporalType.get(entry.getKey());
			}

			if (type != null) {
				Date date = (Date) entry.getValue();
				query.setParameter(entry.getKey(), date, type);
			} else {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.getResultList();
	}

	public T save(T entity) {
		try {
			entityManager.persist(entity);
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}

	public T saveComFlush(T entity) {
		try {
			entityManager.persist(entity);
			entityManager.flush();
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}

	public void save(List<T> entity) {
		try {
			for (T t : entity) {
				entityManager.persist(t);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	protected Query createQuery(String query) {
		return entityManager.createQuery(query);
	}

	protected CriteriaBuilder getCriteriaBuilder() {
		return entityManager.getCriteriaBuilder();
	}

	protected TypedQuery<T> createNamedQuery(String q, Class<T> clazz) {
		return entityManager.createNamedQuery(q, clazz);
	}

	protected TypedQuery<T> createTypedQuery(CriteriaQuery<T> criteriaQuery) {
		return entityManager.createQuery(criteriaQuery);
	}

	protected TypedQuery<T> createTypedQuery(String query) {
		return createTypedQuery(query, getPersistenceClazz());
	}

	protected TypedQuery<T> createTypedQuery(String query, Class<T> clazz) {
		return entityManager.createQuery(query, clazz);
	}

	public T saveOrUpdate(T entity) {
		try {
			entity = entityManager.merge(entity);
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}

	public void saveOrUpdate(List<T> entity) {
		try {
			for (T t : entity) {
				entityManager.merge(t);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void saveOrUpdate(Set<T> entity) {
		try {
			for (T t : entity) {
				entityManager.merge(t);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public Integer executeNamedQuery(String namedQueryName, Map<String, Object> parameters) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.entityManager.createNamedQuery(namedQueryName);
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return query.executeUpdate();
	}

//	@SuppressWarnings("unchecked")
//	public boolean remove(T entity) throws Exception {
//		try {
//			Object id = null;
//			Class<T> clazz = (Class<T>) entity.getClass();
//			for (Field field : clazz.getDeclaredFields()) {
//				if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class)) {
//					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), entity.getClass());
//					id = pd.getReadMethod().invoke(entity);
//					break;
//				}
//			}
//
//			entity = this.find(clazz, id);
//
//			if (entity != null) {
//				entityManager.remove(entity);
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			throw new Exception(e);
//		}
//	}

	public <O> O saveOther(O entity) {
		try {
			entityManager.persist(entity);
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}

	public <O> O findOther(Class<O> clazz, Number id) {

		try {
			Field[] fields = clazz.getDeclaredFields();
			Class<?> type = null;

			for (Field field : fields) {
				Id annotation = field.getAnnotation(Id.class);

				if (annotation != null) {
					type = field.getType();
					break;
				}
			}

			if (type == null) {
				return null;
			}

			return entityManager.find(clazz, type.cast(id.shortValue()));
		} catch (Exception e) {
			throw e;
		}
	}

	public <O> O saveOrUpdateOther(O entityParam) {
		O entity = entityParam;
		try {
			entity = entityManager.merge(entity);
		} catch (Exception e) {
			throw e;
		}
		return entity;
	}

	protected boolean contains(T entity) {
		return entityManager.contains(entity);
	}

	/**
	 * Buscar se string já existe no campo informado, ignorando maiúsculas e
	 * minúsculas.
	 * 
	 * @param field      -> campo no banco de dados
	 * @param table      -> tabela do banco de dados
	 * @param comparison -> string a ser verificada
	 * @return
	 */
	public boolean containsValue(String field, String table, String comparison) {
		Query sql = entityManager.createNativeQuery("SELECT " + field + " FROM " + table + " WHERE LOWER(" + field + ") = :comparison");

		sql.setParameter("comparison", comparison.toLowerCase());

		@SuppressWarnings("unchecked")
		List<Object[]> rs = sql.getResultList();
		if (rs != null && rs.size() > 0)
			return true;

		return false;
	}

}
