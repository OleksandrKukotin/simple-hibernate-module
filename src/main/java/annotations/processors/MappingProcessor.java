package annotations.processors;

import annotations.ColumnMapping;
import annotations.EntityMapping;
import annotations.IdMapping;
import dao.QueryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MappingProcessor {

    private static final String SPACE = " ";
    private static final String COMA = ",";
    private static final String SEMICOLON = ";";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET = ")";
    private static final String CREATE_TABLE = "CREATE TABLE";
    private static final String PRIMARY_KEY_CONSTRAINT = "PRIMARY KEY";
    private static final int SINGLE_ID_MARKER = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingProcessor.class);

    private final QueryExecutor queryExecutor;

    public MappingProcessor(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public void createTable(Class<?> entityClass) {
        final StringBuilder query = buildInitialQueryPart(entityClass);

        final Field[] fields = entityClass.getDeclaredFields();

        query.append(processEntityId(fields, query));
        query.append(processColumns(fields, query));
        query.append(buildFinalQueryPart(query));

        queryExecutor.execute(query.toString());
    }

    // looks too useless, but I want to emphasize the idea
    private StringBuilder buildFinalQueryPart(StringBuilder query) {
        return query.append(RIGHT_BRACKET).append(SEMICOLON);
    }

    private StringBuilder buildInitialQueryPart(Class<?> entityClass) {
        EntityMapping entityMapping = entityClass.getAnnotation(EntityMapping.class);
        if (isNull(entityMapping)) {
            LOGGER.error("EntityMapping annotation was not found");
            throw new EntityMappingNotFoundException("Entity class must be annotated with @EntityMapping");
        }

        return new StringBuilder()
            .append(CREATE_TABLE).append(SPACE)
            .append(entityMapping.name()).append(SPACE)
            .append(LEFT_BRACKET);
    }

    private StringBuilder processEntityId(Field[] fields, StringBuilder query) {
        final Field entityId = findSingleId(fields);
        IdMapping idMapping = entityId.getAnnotation(IdMapping.class);
        String idName = idMapping.name().isEmpty()
            ? entityId.getName()
            : idMapping.name();

        return query
            .append(idName).append(SPACE)
            .append(idMapping.type()).append(SPACE)
            .append(PRIMARY_KEY_CONSTRAINT).append(COMA).append(SPACE);
    }

    private Field findSingleId(Field[] declaredFields) {
        List<Field> ids = Arrays.stream(declaredFields)
            .filter(field -> nonNull(field.getAnnotation(IdMapping.class)))
            .toList();

        if (ids.size() == SINGLE_ID_MARKER) {
            return ids.get(0);
        } else {
            throw new MoreThanOneIdException("Entity class should have one single Id");
        }
    }

    private StringBuilder processColumns(Field[] fields, StringBuilder query) {
        for (Field field : fields) {
            ColumnMapping columnMapping = field.getAnnotation(ColumnMapping.class);

            if (isNull(columnMapping)) {
                query.append(field.getName()).append(SPACE)
                    .append(DataTypeMapper.getType(field.getType()));
            } else {
                query.append(columnMapping.name()).append(SPACE)
                    .append(DataTypeMapper.getType(columnMapping.type().getClass()));
            }
            query.append(COMA).append(SPACE);
        }
        return query;
    }

    private static final class EntityMappingNotFoundException extends RuntimeException {

        EntityMappingNotFoundException(String message) {
            super(message);
        }
    }

    private static final class MoreThanOneIdException extends RuntimeException {

        public MoreThanOneIdException(String message) {
            super(message);
        }
    }
}
