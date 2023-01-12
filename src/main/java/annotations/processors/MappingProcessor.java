package annotations.processors;

import annotations.ColumnMapping;
import annotations.EntityMapping;
import annotations.IdMapping;
import dao.QueryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class MappingProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(MappingProcessor.class);

    private final QueryExecutor queryExecutor;

    public MappingProcessor(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public void createTable(Class<?> entityClass) {
        EntityMapping entityMapping = entityClass.getAnnotation(EntityMapping.class);
        if (entityMapping == null) {
            LOGGER.error("EntityMapping annotation was not found");
            throw new EntityMappingNotFoundException("Entity class must be annotated with @EntityMapping");
        }
        Field[] fields = entityClass.getDeclaredFields();
        StringBuilder queryString = new StringBuilder("CREATE TABLE " + entityMapping.name() + " (");
        String primaryKeyName = "";
        for (Field field : fields) {
            IdMapping idMapping = field.getAnnotation(IdMapping.class);
            if (idMapping != null) {
                primaryKeyName = field.getName();
                queryString.append(primaryKeyName + " " + idMapping.type() + ", ");
            } else {
                ColumnMapping columnMapping = field.getAnnotation(ColumnMapping.class);
                if (columnMapping == null) {
                    continue;
                }
                queryString.append(field.getName() + " " + DataTypeMapper.getType(field.getType()) + ", ");
            }
        }
        queryString.append("PRIMARY KEY (" + primaryKeyName + ")");
        queryString.append(")");
        queryExecutor.execute(queryString.toString());
    }

    private static final class EntityMappingNotFoundException extends RuntimeException {

        public EntityMappingNotFoundException(String message) {
            super(message);
        }
    }
}
