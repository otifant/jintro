package ch.otticello.jintro.jdbc.commands;

import java.sql.SQLException;
import java.util.List;

import ch.otticello.jintro.jdbc.model.SchoolSubjectGrade;
import ch.otticello.jintro.jdbc.repository.ConnectionToDatabase;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectGradeRepository;

public class ShowGrades implements Command {

    @Override
    public void execute() throws Exception {
        try (
                var connection = new ConnectionToDatabase();
                var subjectGradeRepository = new SchoolSubjectGradeRepository(connection);) {
            connection.connect();

            List<SchoolSubjectGrade> allGrades = subjectGradeRepository.getAll();

            System.out.println("All Grades:");
            System.out.println("subject\tgrade\tdate");

            for (var schoolSubjectGrade : allGrades) {
                String line = new StringBuilder()
                        .append(schoolSubjectGrade.getSchoolSubject().name()).append("\t")
                        .append(schoolSubjectGrade.getGrade().grade()).append("\t")
                        .append(schoolSubjectGrade.getDate())
                        .toString();

                System.out.println(line);
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
