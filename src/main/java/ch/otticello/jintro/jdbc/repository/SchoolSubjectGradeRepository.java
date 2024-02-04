package ch.otticello.jintro.jdbc.repository;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import ch.otticello.jintro.jdbc.model.Grade;
import ch.otticello.jintro.jdbc.model.SchoolSubject;
import ch.otticello.jintro.jdbc.model.SchoolSubjectGrade;

public class SchoolSubjectGradeRepository extends AbstractRepository {
    private static final String SELECT_ALL_QUERY = """
            SELECT
                subject.name as subject,
                subject.id as subject_id,
                grade.grade as grade,
                grade.id as grade_id,
                ssg.date as date
            FROM
                School_Subject_Grade as ssg
            LEFT JOIN
                School_Subject as subject ON subject.id = ssg.school_subject_id
            LEFT JOIN
                Grade as grade ON grade.id = ssg.grade_id
                    """;

    private static final String SELECT_BY_SUBJECT_AND_DATE_QUERY = """
            SELECT
                grade_id as id
            FROM
                School_Subject_Grade
            WHERE
                school_subject_id = ?
                    AND
                date = ?
            """;

    private static final String CREATE_ENTRY_QUERY = """
            INSERT INTO
                School_Subject_Grade
                (grade_id, school_subject_id, date)
            values
                (?, ?, ?)
            """;

    private static final String UPDATE_QUERY = """
            UPDATE
               School_Subject_Grade
            SET
                grade_id = ?
            WHERE
                school_subject_id = ?
                    AND
                date = ?;
                """;

    private final static String DELETE_GRADE_MAPPING_QUERY = """
            DELETE
            FROM
                School_Subject_Grade
            WHERE
                school_subject_id = ?
                    AND
                date = ?;
            """;

    public SchoolSubjectGradeRepository() throws SQLException {
    }

    public SchoolSubjectGradeRepository(ConnectionToDatabase connectionToDatabase) {
        super(connectionToDatabase);
    }

    public List<SchoolSubjectGrade> getAll() throws SQLException {
        var statement = this.connectionToDatabase.getPreparedStatement(SELECT_ALL_QUERY);

        var result = statement.executeQuery();

        List<SchoolSubjectGrade> allGrades = new ArrayList<>();

        while (result.next()) {
            allGrades.add(new SchoolSubjectGrade(
                    new Grade(result.getInt("grade_id"), result.getFloat("grade")),
                    new SchoolSubject(result.getInt("subject_id"), result.getString("subject")),
                    result.getDate("date")));
        }

        return allGrades;
    }

    public SchoolSubjectGrade getBySubjectAndDate(SchoolSubject subject,
            Date date) throws SQLException, NoSuchElementException, Exception {
        Optional<Integer> gradeId = this.connectionToDatabase.queryId(SELECT_BY_SUBJECT_AND_DATE_QUERY, subject.id(),
                date);
        if (gradeId.isPresent()) {
            Grade grade;
            try (GradeRepository gradeRepository = new GradeRepository()) {
                grade = gradeRepository.getById(gradeId.get());
            }
            return new SchoolSubjectGrade(grade, subject, date);
        }
        throw new NoSuchElementException("Can't find a SchoolSubjectGrade with this values.");
    }

    public void save(SchoolSubjectGrade subjectGrade) throws SQLException {
        var statement = this.connectionToDatabase.getPreparedStatement(CREATE_ENTRY_QUERY);
        statement.setInt(1, subjectGrade.getGrade().id());
        statement.setInt(2, subjectGrade.getSchoolSubject().id());
        statement.setDate(3, subjectGrade.getDate());
        statement.executeUpdate();
    }

    public void update(SchoolSubjectGrade subjectGrade) throws SQLException {
        var statement = connectionToDatabase.getPreparedStatement(UPDATE_QUERY);
        statement.setInt(1, subjectGrade.getGrade().id());
        statement.setInt(2, subjectGrade.getSchoolSubject().id());
        statement.setDate(3, subjectGrade.getDate());
        statement.executeUpdate();
    }

    public void deleteWithSubjectAndDate(
            SchoolSubject subject,
            Date date) throws SQLException {

        int success = this.connectionToDatabase.deleteWith(
                DELETE_GRADE_MAPPING_QUERY,
                subject.id(),
                date);

        System.out.println(success);
    }

}
