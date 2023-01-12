import annotations.processors.ConnectionProvider;
import annotations.processors.MappingProcessor;
import annotations.processors.QueryExecutor;

public class MainApplication {

    public static void main(String[] args) {
        new MappingProcessor(
            new QueryExecutor(
                new ConnectionProvider()
            )
        ).createTable(Employee.class);
    }
}
