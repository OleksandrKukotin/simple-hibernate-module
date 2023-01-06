package annotations.processors;

import annotations.ColumnMapping;
import annotations.EntityMapping;
import annotations.IdMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
            } else {
                ColumnMapping columnMapping = field.getAnnotation(ColumnMapping.class);
                if (columnMapping == null) {
                    continue;
                }
                queryString.append(columnMapping.columnName() + " " + columnMapping.columnType() + ", ");
            }
        }
        queryString.append("PRIMARY KEY (" + primaryKeyName + ")");
        queryString.append(")");

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password")) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryString.toString())) {
                preparedStatement.execute();
            } catch (SQLException e) {
                LOGGER.error("Processing of the PreparedStatement went wrong. Try to check a generated query in the debugger.");
            }
        } catch (SQLException e) {
            LOGGER.error("Error with connecting to Database. Try to check the connection data that is given for DriverManager.getConnection().");
        }
    }

    private static class EntityMappingNotFoundException extends RuntimeException {
        public EntityMappingNotFoundException() {
            super("Entity class must be annotated with @EntityMapping");
        }
    }
}

