import annotations.processors.MappingProcessor;
import dao.ConnectionProvider;
import dao.QueryExecutor;

public class MainApplication {

    public static void main(String[] args) {
        new MappingProcessor(
            new QueryExecutor(
                new ConnectionProvider()
            )
        ).createTable(Employee.class);
    }
}
