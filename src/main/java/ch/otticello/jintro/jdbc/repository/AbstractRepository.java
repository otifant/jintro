package ch.otticello.jintro.jdbc.repository;

import java.sql.SQLException;

/**
 * This abstract class is used as base class for all other Repositories.
 * It allows initialization with and without {ConnectionToDatabase} instance.
 */
public abstract class AbstractRepository implements AutoCloseable {
    private boolean closeConnectionAfterClose = false;
    protected ConnectionToDatabase connectionToDatabase;

    public AbstractRepository() throws SQLException {
        this.connectionToDatabase = new ConnectionToDatabase();
        this.connectionToDatabase.connect();
        this.closeConnectionAfterClose = true;
    }

    public AbstractRepository(ConnectionToDatabase connectionToDatabase) {
        this.connectionToDatabase = connectionToDatabase;
    }

    @Override
    public void close() throws Exception {
        if (this.closeConnectionAfterClose) {
            this.connectionToDatabase.close();
        }
    }
}
