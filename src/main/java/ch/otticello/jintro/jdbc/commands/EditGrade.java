package ch.otticello.jintro.jdbc.commands;

import java.sql.Date;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import ch.otticello.jintro.jdbc.model.Grade;
import ch.otticello.jintro.jdbc.model.SchoolSubjectGrade;
import ch.otticello.jintro.jdbc.repository.ConnectionToDatabase;
import ch.otticello.jintro.jdbc.repository.GradeRepository;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectGradeRepository;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectRepository;
import ch.otticello.jintro.jdbc.model.SchoolSubject;

public class EditGrade implements Command {
    private final String subject;
    private final Date date;
    private final float newGrade;

    final ConnectionToDatabase connection;
    final SchoolSubjectRepository subjectRepository;
    final SchoolSubjectGradeRepository subjectGradeRepository;
    final GradeRepository gradeRepository;

    public EditGrade(String subject,
                     Date date,
                     float newGrade) {
        this.subject = subject;
        this.date = date;
        this.newGrade = newGrade;

        this.connection = new ConnectionToDatabase();
        this.subjectRepository = new SchoolSubjectRepository(connection);
        this.subjectGradeRepository = new SchoolSubjectGradeRepository(connection);
        this.gradeRepository = new GradeRepository(connection);
    }

    public EditGrade(String subject,
                     Date date,
                     float newGrade,

                     final ConnectionToDatabase connection,
                     final SchoolSubjectRepository subjectRepository,
                     final SchoolSubjectGradeRepository subjectGradeRepository,
                     final GradeRepository gradeRepository
    ) {
        this.subject = subject;
        this.date = date;
        this.newGrade = newGrade;

        this.connection = connection;
        this.subjectRepository = subjectRepository;
        this.subjectGradeRepository = subjectGradeRepository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    public void execute() throws Exception {
        try (
                this.connection;
                this.subjectRepository;
                this.subjectGradeRepository;
                this.gradeRepository) {
            connection.connect();
            Optional<SchoolSubject> schoolSubject = subjectRepository.getByName(subject);
            if (schoolSubject.isEmpty()) {
                throw new NoSuchElementException("Can't find the corresponding SchoolSubject");
            }
            SchoolSubjectGrade entry = subjectGradeRepository.getBySubjectAndDate(schoolSubject.get(),
                    date);
            Grade newGrade = gradeRepository.getByGradeOrCreate(this.newGrade);
            entry.setGrade(newGrade);
            subjectGradeRepository.update(entry);

        } catch (SQLException ex) {
            System.out.println("Can't add grade, see:");
            throw ex;
        }
    }

    public static void main(String[] args) throws Exception {
        String subject = "Sport";
        Date date = Date.valueOf("2022-06-09");
        float newGrade = 1.25f;

        var instance = new EditGrade(subject, date, newGrade);
        instance.execute();
    }
}
