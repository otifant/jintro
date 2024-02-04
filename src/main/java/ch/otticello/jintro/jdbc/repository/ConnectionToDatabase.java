package ch.otticello.jintro.jdbc.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import com.mysql.cj.jdbc.Driver;

/**
 * This class is used as a helper to establish a connection to the database.
 * It also provides some methods that are used multiple times.
 */
public class ConnectionToDatabase implements java.lang.AutoCloseable {
    final static String URL = "jdbc:mysql://localhost:3306/magicdb";
    final static String USER_NAME = "magician";
    final static String PASSWORD = "!tsGonn@B€M@g!c";

    private Connection connection;

    public void connect() throws SQLException {
        Driver driver;

        driver = new com.mysql.cj.jdbc.Driver();
        DriverManager.registerDriver(driver);

        connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
    }

    @Override
    public void close() throws SQLException {
        var statement = getStatement();
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * @return a Statement (not a PreparedStatement) from the {java.sql.Connection}.
     */
    public Statement getStatement() throws SQLException {
        return connection.createStatement();
    }

    /**
     * @return a PreparedStatement from the {java.sql.Connection}.
     */
    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    /**
     * After this, it executes the {selectQuery} in order to query the desired id.
     * If this doesn't exist yet, it then runs a {createQuery} including a {value} as a parameter for the
     * PreparedStatement in order to create the database entry.
     */
    public <E> int createOrQueryTableRow(final String selectQuery,
                                         final String createQuery, E value)
            throws SQLException {
        Optional<Integer> id = queryId(selectQuery, value);

        if (id.isEmpty()) {
            var createStatement = getPreparedStatement(createQuery);
            if (value instanceof Float) {
                createStatement.setFloat(1, (float) value);
            } else if (value instanceof String) {
                createStatement.setString(1, (String) value);
            } else {
                throw new UnsupportedOperationException("This function is only implemented for float and String.");
            }
            createStatement.executeUpdate();
            id = queryId(selectQuery, value);
        }

        if (id.isEmpty()) {
            throw new SQLException("For some reason, we didn't receive a grade after we've created it");
        }
        return id.get();
    }

    /**
     * This method is used to query an Id with some additional {values} for the PreparedStatement.
     * @param values used for the placeholders within the PreparedStatement.
     * @return Optional<Integer> The id from the resultSet.
     */
    public Optional<Integer> queryId(final String query, Object... values)
            throws SQLException {
        var statement = getPreparedStatement(query);
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            switch (value) {
                case Integer integer -> statement.setInt(i + 1, integer);
                case Float floatValue -> statement.setFloat(i + 1, floatValue);
                case String s -> statement.setString(i + 1, s);
                case Date date -> statement.setDate(i + 1, date);
                case null, default ->
                        throw new UnsupportedOperationException("This function is only implemented for int, float, String, and Date.");
            }
        }
        var result = statement.executeQuery();
        Optional<Integer> id = result.next() ? Optional.of(result.getInt("id")) : Optional.empty();
        return id;
    }

    /**
     * This method is used to delete rows in the database with some additional {values} for the PreparedStatement.
     * @param values used for the placeholders within the PreparedStatement.
     * @return int The id from the resultSet.
     */
    public int deleteWith(final String query, Object... values)
            throws SQLException {
        var statement = getPreparedStatement(query);
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if (value instanceof Integer) {
                statement.setInt(i + 1, (int) value);
            } else if (value instanceof Date) {
                statement.setDate(i + 1, (Date) value);
            } else {
                throw new UnsupportedOperationException("This function is only implemented for int.");
            }
        }
        return statement.executeUpdate();

    }
}
