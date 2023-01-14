import annotations.processors.EntityMappingProcessor;
import dao.ConnectionProvider;
import dao.QueryExecutor;

public class MainApplication {

    public static void main(String[] args) {
        new EntityMappingProcessor(
            new QueryExecutor(
                new ConnectionProvider()
            )
        ).createTable(Employee.class);
    }
}
