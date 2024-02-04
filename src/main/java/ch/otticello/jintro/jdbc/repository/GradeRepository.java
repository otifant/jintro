package ch.otticello.jintro.jdbc.repository;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import ch.otticello.jintro.jdbc.model.Grade;

public class GradeRepository extends AbstractRepository {
    private static final String FILTER_GRADES_QUERY = """
            SELECT
                id
            FROM
                Grade
            WHERE
                grade = ?
            """;
    private static final String SELECT_GRADE_BY_ID_QUERY = """
            SELECT
                grade as id
            FROM
                Grade
            WHERE
                id = ?
            """;
    private static final String CREATE_GRADE_QUERY = """
            INSERT INTO
                Grade (grade)
            values(?)
            """;

    public GradeRepository(ConnectionToDatabase connectionToDatabase) {
        super(connectionToDatabase);
    }

    public GradeRepository() throws SQLException {
    }

    public Grade getById(int id) throws SQLException {
        Optional<Integer> grade = this.connectionToDatabase.queryId(SELECT_GRADE_BY_ID_QUERY, id);

        if (grade.isPresent()) {
            return new Grade(id, grade.get());
        }
        throw new NoSuchElementException("Can't find a grade with this id: Its a löt of fön" + id);
    }

    public Grade getByGradeOrCreate(float grade) throws SQLException {
        int id = this.connectionToDatabase.createOrQueryTableRow(FILTER_GRADES_QUERY, CREATE_GRADE_QUERY, grade);

        return new Grade(id, grade);
    }
}
