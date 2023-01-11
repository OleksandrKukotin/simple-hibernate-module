package annotations.processors;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    private static final String APPLICATION_PROPERTIES_FILENAME = "app.properties";
    private static final String PG_DEFAULT_VALUE = "postgres";
    private static final int DATASOURCE_DEFAULT_PORT = 5434;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    private final Properties properties;
    private final PGSimpleDataSource dataSource;

    ConnectionManager() {
        this.properties = new Properties();
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES_FILENAME)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new PropertiesFileNotExists("Properties file is not exists", e);
        }
        this.dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{properties.getProperty("dataSource.host")});
        dataSource.setPortNumbers(new int[]{Integer.parseInt(properties.getProperty("dataSource.port", String.valueOf(DATASOURCE_DEFAULT_PORT)))});
        dataSource.setUser(properties.getProperty("dataSource.user", PG_DEFAULT_VALUE));
        dataSource.setPassword(properties.getProperty("dataSource.password", PG_DEFAULT_VALUE));
        dataSource.setDatabaseName(properties.getProperty("dataSource.databaseName", PG_DEFAULT_VALUE));
    }

    public void exectuteQuery(StringBuilder queryString) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryString.toString())) {
                preparedStatement.execute();
            } catch (SQLException e) {
                LOGGER.error("Processing of the PreparedStatement went wrong. Try to check a generated query in the debugger.");
            }
        } catch (SQLException e) {
            LOGGER.error("Error with connecting to Database. Try to check the connection data that is given for DriverManager.getConnection().");
        }
    }

    private static final class PropertiesFileNotExists extends RuntimeException {

        PropertiesFileNotExists(String message, Exception exception) {
            super(message, exception);
        }
    }
}
