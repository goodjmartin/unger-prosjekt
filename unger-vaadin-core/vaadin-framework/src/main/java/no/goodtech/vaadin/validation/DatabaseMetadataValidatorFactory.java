package no.goodtech.vaadin.validation;

import com.vaadin.v7.data.validator.BeanValidator;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;
import org.hibernate.validator.cfg.defs.SizeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.util.Map;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Creates validation rules for all entities based on info from database schema.
 * The following rules is created:
 * - max length of string fields
 *
 * Got help from these links:
 * http://stackoverflow.com/questions/21617556/adding-constraints-dynamically-to-hibernate-validator-in-a-spring-mvc-applicatio
 * https://docs.jboss.org/hibernate/validator/4.1/reference/en-US/html/programmaticapi.html#programmaticapi
 * http://in.relation.to/2010/06/02/hibernate-validator-410-beta-2-with-programmatic-constraint-configuration-api/
 * https://vaadin.com/api/7.6.8/javax/validation/Configuration.html
 * TODO: Handle entities with table names different than enity name and column names different than field name
 * TODO: Plugin validation rules into vaadin in a more nice way, see {@link #hackVaadinBeanValidator()}
 * TODO: Make it possible to build the rules again if we change the database schema while we are running
 */
public class DatabaseMetadataValidatorFactory implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMetadataValidatorFactory.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private DataSource dataSource;

	private ValidatorFactory createFactory() {
		long start = System.currentTimeMillis();

		final HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class).configure();
		final ConstraintMapping constraintMapping = configuration.createConstraintMapping();

		for (EntityType<?> entityType : entityManager.getMetamodel().getEntities()) {
			final String tableName = getTableName(entityType);
			DatabaseMetadataUtils metadata = new DatabaseMetadataUtils(dataSource);
			final Map<String, Integer> columnLengths = metadata.getColumnLengths(tableName);
			LOGGER.debug("Table {} string column lengths: {}", tableName, columnLengths);

			if (columnLengths.size() > 0) {
				final Class<?> javaType = entityType.getJavaType();
				final TypeConstraintMappingContext<?> type = constraintMapping.type(javaType);
				for (Map.Entry<String, Integer> entry : columnLengths.entrySet()) {
					final String columnName = entry.getKey();
					try {
						final Field field = javaType.getDeclaredField(columnName);
						if (String.class.equals(field.getType())) {
							type.property(columnName, FIELD).constraint(new SizeDef().max(entry.getValue().intValue()));
						}
					} catch (NoSuchFieldException e) {
						//TODO: Find field if column name is diferent from field name
						LOGGER.warn(e.getMessage()); //ignore unmapped table columns or columns with different name than colum
					}
				}
			}
		}
		configuration.addMapping(constraintMapping);
		ValidatorFactory factory = configuration.buildValidatorFactory();
		LOGGER.info("Database metadata scan and validation rule creation completed in {} millis", System.currentTimeMillis() - start);
		return factory;
	}

	private String getTableName(EntityType<?> entityType) {
		final Class<? extends EntityType> type = entityType.getClass();
		if (type.isAnnotationPresent(Table.class)) {
			return type.getAnnotation(Table.class).name();
		}
		return entityType.getName();
	}

	private void hackVaadinBeanValidator() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = BeanValidator.class.getDeclaredField("factory");
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		field.set(null, createFactory());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		hackVaadinBeanValidator();
	}
}