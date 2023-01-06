import annotations.processors.MappingProcessor;

public class MainApplication {

    public static void main(String[] args) {
        new MappingProcessor().createTable(Employee.class);
    }
}
