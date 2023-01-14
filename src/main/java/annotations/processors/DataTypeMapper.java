package annotations.processors;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum DataTypeMapper {

    STRING(String.class, "VARCHAR(255)"),
    INTEGER(Integer.class, "INTEGER"),
    LONG(Long.class, "BIGINT"),
    DOUBLE(Double.class, "DOUBLE PRECISION"),
    BOOLEAN(Boolean.class, "BOOLEAN"),
    ;

    private static final Map<Class<?>, String> types = initializeMap();

    private final Class<?> javaType;
    private final String pgType;

    DataTypeMapper(Class<?> javaType, String pgType) {
        this.javaType = javaType;
        this.pgType = pgType;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public String getPgType() {
        return pgType;
    }

    public static String getType(Class<?> javaType) {
        return types.get(javaType);
    }

    private static Map<Class<?>, String> initializeMap() {
        return Arrays.stream(DataTypeMapper.values())
            .collect(Collectors.toMap(DataTypeMapper::getJavaType, DataTypeMapper::getPgType));
    }
}
