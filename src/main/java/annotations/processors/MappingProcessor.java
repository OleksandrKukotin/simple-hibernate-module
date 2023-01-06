package annotations.processors;

import annotations.ColumnMapping;
import annotations.EntityMapping;
import annotations.IdMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MappingProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(MappingProcessor.class);

    public void createTable(Class<?> entityClass) {
        EntityMapping entityMapping = entityClass.getAnnotation(EntityMapping.class);
        if (entityMapping == null) {
            LOGGER.error("EntityMapping annotation was not found");
            throw new EntityMappingNotFoundException();
        }
        Field[] fields = entityClass.getDeclaredFields();
        StringBuilder queryString = new StringBuilder("CREATE TABLE " + entityMapping.tableName() + " (");
        String primaryKeyName = "";
        for (Field field : fields) {
            IdMapping idMapping = field.getAnnotation(IdMapping.class);
            if (idMapping != null) {
                primaryKeyName = idMapping.idName();
                queryString.append(primaryKeyName + " " + idMapping.idType() + ", ");
                continue;
            }
            ColumnMapping columnMapping = field.getAnnotation(ColumnMapping.class);
            if (columnMapping == null) {
                continue;
            }
            queryString.append(columnMapping.columnName() + " " + columnMapping.columnType() + ", ");
        }
        queryString.append("PRIMARY KEY (" + primaryKeyName + "),");
        queryString.deleteCharAt(queryString.length() - 1);
        queryString.append(")");
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password")) {
            connection.prepareStatement(queryString.toString()).execute();
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("Error with connecting to Database");
        }
    }

    private static class EntityMappingNotFoundException extends RuntimeException {
        public EntityMappingNotFoundException() {
            super("Entity class must be annotated with @EntityMapping");
        }
    }
}

