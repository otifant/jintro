package ch.otticello.jintro.jdbc.commands;

import ch.otticello.jintro.jdbc.model.SchoolSubjectGrade;
import ch.otticello.jintro.jdbc.repository.ConnectionToDatabase;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectGradeRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class GetAverageOfGrades implements Command {

    private final String schoolSubjectName;
    private final ConnectionToDatabase connection;
    private final SchoolSubjectGradeRepository subjectGradeRepository;

    public GetAverageOfGrades(final String schoolSubjectName) {
        this.schoolSubjectName = schoolSubjectName;

        this.connection = new ConnectionToDatabase();
        this.subjectGradeRepository = new SchoolSubjectGradeRepository(connection);
    }

    public GetAverageOfGrades(final String schoolSubjectName,
                              final ConnectionToDatabase connection,
                              final SchoolSubjectGradeRepository subjectGradeRepository
    ) {
        this.schoolSubjectName = schoolSubjectName;

        this.connection = connection;
        this.subjectGradeRepository = subjectGradeRepository;
    }

    @Override
    public void execute() throws Exception {
        try (
                this.connection;
                this.subjectGradeRepository) {

            connection.connect();

            List<SchoolSubjectGrade> allGrades = subjectGradeRepository.getAll();

            // Of course, to calculate the avg, it would be better to do that in the SQL query.
            // But for simplicity, we do it in the Java Code:
            OptionalDouble avg = allGrades.stream()
                    .filter(x -> x.getSchoolSubject().name().equalsIgnoreCase(this.schoolSubjectName))
                    .mapToDouble(x -> x.getGrade().grade())
                    .average();

            if (avg.isPresent()) {
                System.out.println("Average of " + this.schoolSubjectName + ": " + avg.getAsDouble());
            } else {
                System.out.println("This School Subject has no entries yet: " + this.schoolSubjectName);
            }

        } catch (SQLException ex) {
            System.out.println("Can't fetch all grades, see:");
            System.out.println(ex);
            throw ex;
        } catch (Exception ex) {
            System.out.println("Another unknown exception happend.");
            throw ex;
        }

    }

}
