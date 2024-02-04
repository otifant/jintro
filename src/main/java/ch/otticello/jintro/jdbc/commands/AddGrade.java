package ch.otticello.jintro.jdbc.commands;

import java.sql.Date;
import java.sql.SQLException;

import ch.otticello.jintro.jdbc.model.SchoolSubjectGrade;
import ch.otticello.jintro.jdbc.repository.ConnectionToDatabase;
import ch.otticello.jintro.jdbc.repository.GradeRepository;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectGradeRepository;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectRepository;
import ch.otticello.jintro.jdbc.model.Grade;
import ch.otticello.jintro.jdbc.model.SchoolSubject;

public class AddGrade implements Command {
    private final float grade;
    private final String subject;
    private final Date date;

    public AddGrade(float grade, String subject, Date date) {
        this.grade = grade;
        this.subject = subject;
        this.date = date;
    }

    @Override
    public void execute() throws Exception{
        try (
                var connection = new ConnectionToDatabase();
                var gradeRepository = new GradeRepository(connection);
                var subjectRepository = new SchoolSubjectRepository(connection);
                var subjectGradeRepository = new SchoolSubjectGradeRepository(connection);) {
            connection.connect();

            Grade grade = gradeRepository.getByGradeOrCreate(this.grade);
            SchoolSubject subject = subjectRepository.getByNameOrCreate(this.subject);

            SchoolSubjectGrade newEntry = new SchoolSubjectGrade(grade, subject, date);
            subjectGradeRepository.save(newEntry);

        } catch (SQLException ex) {
            System.out.println("Can't add grade, see:");
            System.out.println(ex);
            throw ex;
        } catch (Exception ex) {
            System.out.println("Another unknown exception happend during closing the connection to the database.");
            throw ex;
        }

    }

    public static void main(String[] args) throws Exception {
        float grade = 2.5f;
        String subject = "Sport";

        Date date = Date.valueOf("2022-06-09");

        var instance = new AddGrade(grade, subject, date);
        instance.execute();
    }

}
