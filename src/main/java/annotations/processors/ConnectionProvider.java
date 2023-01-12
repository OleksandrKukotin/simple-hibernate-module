package annotations.processors;

import annotations.exception.DatasourceException;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionProvider {

    private static final String APP_PROPERTIES_FILENAME = "app.properties";
    private static final String PG_DEFAULT_VALUE = "postgres";
    private static final int PG_DEFAULT_PORT = 5434;

    private final Connection connection;

    public ConnectionProvider() {
        try {
            this.connection = setupDatasource().getConnection();
        } catch (SQLException e) {
            throw new DatasourceException("Error with connecting to Database", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private PGSimpleDataSource setupDatasource() {
        final Properties properties = new Properties();
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(APP_PROPERTIES_FILENAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new PropertiesFileNotExistsException("Properties file is not exists", e);
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{properties.getProperty("dataSource.host")});
        dataSource.setPortNumbers(new int[]{Integer.parseInt(
            properties.getProperty("dataSource.port", String.valueOf(PG_DEFAULT_PORT)))}
        );
        dataSource.setUser(properties.getProperty("dataSource.user", PG_DEFAULT_VALUE));
        dataSource.setPassword(properties.getProperty("dataSource.password", PG_DEFAULT_VALUE));
        dataSource.setDatabaseName(properties.getProperty("dataSource.databaseName", PG_DEFAULT_VALUE));

        return dataSource;
    }

    private static final class PropertiesFileNotExistsException extends RuntimeException {

        PropertiesFileNotExistsException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
