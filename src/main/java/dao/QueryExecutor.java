package dao;

import dao.exception.DatasourceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryExecutor {

    private final Connection connection;

    public QueryExecutor(ConnectionProvider connectionProvider) {
        this.connection = connectionProvider.getConnection();
    }

    public void execute(String query) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DatasourceException("Processing of the PreparedStatement went wrong", e);
        }
    }
}
