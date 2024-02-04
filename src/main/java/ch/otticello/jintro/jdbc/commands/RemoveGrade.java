package ch.otticello.jintro.jdbc.commands;

import java.sql.Date;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import ch.otticello.jintro.jdbc.repository.ConnectionToDatabase;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectGradeRepository;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectRepository;
import ch.otticello.jintro.jdbc.model.SchoolSubject;

public class RemoveGrade implements Command {
    private final String subject;
    private final Date date;
    

    public RemoveGrade(String subject, Date date) {
        this.subject = subject;
        this.date = date;
    }

    @Override
    public void execute() throws Exception {
        try (
                var connection = new ConnectionToDatabase();
                var subjectRepository = new SchoolSubjectRepository(connection);
                var subjectGradeRepository = new SchoolSubjectGradeRepository(connection);) {
            connection.connect();

            Optional<SchoolSubject> subject = subjectRepository.getByName(this.subject);
            if (subject.isEmpty()) {
                throw new NoSuchElementException("Can't find a subject with the name " + this.subject);
            }

            subjectGradeRepository.deleteWithSubjectAndDate(subject.get(), this.date);

        } catch (SQLException ex) {
            System.out.println("Can't add grade, see:");
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println("Unhandled Exception:");
            System.out.println(ex);
            throw ex;
        }
    }

    public static void main(String[] args) throws Exception {
        String subject = "Math";
        Date date = Date.valueOf("2022-06-09");

        var instance = new RemoveGrade(subject, date);
        instance.execute();
    }
}
