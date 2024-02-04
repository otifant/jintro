package ch.otticello.jintro.jdbc.repository;

import java.sql.SQLException;
import java.util.Optional;

import ch.otticello.jintro.jdbc.model.SchoolSubject;

public class SchoolSubjectRepository extends AbstractRepository {
    private static final String FILTER_SUBJECT_QUERY = """
            SELECT
                id
            FROM
                School_Subject
            WHERE
                name = ?
            """;
    private static final String CREATE_SUBJECT_QUERY = """
            INSERT INTO
                School_Subject (name)
            values(?)
            """;

    public SchoolSubjectRepository() throws SQLException {
    }

    public SchoolSubjectRepository(ConnectionToDatabase connectionToDatabase) {
        super(connectionToDatabase);
    }

    public Optional<SchoolSubject> getByName(String name)
            throws SQLException {
        Optional<Integer> id = this.connectionToDatabase.queryId(FILTER_SUBJECT_QUERY, name);

        return id.isPresent() ? Optional.of(new SchoolSubject(id.get(), name)) : Optional.empty();
    }

    public SchoolSubject getByNameOrCreate(String name)
            throws SQLException {
        int id = this.connectionToDatabase.createOrQueryTableRow(FILTER_SUBJECT_QUERY, CREATE_SUBJECT_QUERY, name);

        return new SchoolSubject(id, name);
    }
}
