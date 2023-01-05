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

        // Connect to the database
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password")) {
            StringBuilder queryString = new StringBuilder("CREATE TABLE " + entityMapping.tableName() + " (");

            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                ColumnMapping columnMapping = field.getAnnotation(ColumnMapping.class);
                if (columnMapping == null) {
                    continue;
                }
                queryString.append(columnMapping.columnName() + " " + columnMapping.columnType() + ",");
            }

            for (Field field : fields) {
                // Check if the field is annotated with @IdMapping
                IdMapping idMapping = field.getAnnotation(IdMapping.class);
                if (idMapping == null) {
                    continue;
                }

                queryString.append("PRIMARY KEY (" + idMapping + "),");
            }

            // Remove the last comma and add the closing parenthesis
            queryString.deleteCharAt(queryString.length() - 1);
            queryString.append(")");

            PreparedStatement preparedStatement = connection.prepareStatement(queryString.toString());
            preparedStatement.execute();
        } catch (SQLException e) {
            LOGGER.error("Error with connecting to Database");
        }
    }

    private static class EntityMappingNotFoundException extends RuntimeException {
        public EntityMappingNotFoundException() {
            super("Entity class must be annotated with @EntityMapping");
        }
    }
}

