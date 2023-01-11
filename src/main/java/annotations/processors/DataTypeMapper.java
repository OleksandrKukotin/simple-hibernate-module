package annotations.processors;

import java.util.HashMap;
import java.util.Map;

public class DataTypeMapper {

    private final Map<Class<?>, String> typeMap;

    public DataTypeMapper() {
        this.typeMap = new HashMap<>();
        this.typeMap.put(String.class, "VARCHAR");
        this.typeMap.put(Integer.class, "INTEGER");
        this.typeMap.put(Long.class, "BIGINT");
        this.typeMap.put(Double.class, "DOUBLE PRECISION");
        this.typeMap.put(Boolean.class, "BOOLEAN");
    }

    public String getDbType(Class<?> clazz) {
        return typeMap.getOrDefault(clazz, "VARCHAR");
    }
}
