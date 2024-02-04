package ch.otticello.jintro.jdbc.commands;

import ch.otticello.jintro.jdbc.model.Grade;
import ch.otticello.jintro.jdbc.model.SchoolSubject;
import ch.otticello.jintro.jdbc.model.SchoolSubjectGrade;
import ch.otticello.jintro.jdbc.repository.ConnectionToDatabase;
import ch.otticello.jintro.jdbc.repository.GradeRepository;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectGradeRepository;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditGradeTest {

    @Mock
    ConnectionToDatabase connectionToDatabase;
    @Mock
    SchoolSubjectRepository schoolSubjectRepository;
    @Mock
    SchoolSubjectGradeRepository schoolSubjectGradeRepository;
    @Mock
    GradeRepository gradeRepository;

    EditGrade uut;

    @Test
    void noSuchSubject_execute_throwNoSuchElementException() throws SQLException {
        uut = new EditGrade("subject", new Date(0), 2,
                connectionToDatabase, schoolSubjectRepository, schoolSubjectGradeRepository, gradeRepository
        );

        doReturn(Optional.empty()).when(schoolSubjectRepository).getByName(any());

        assertThrows(NoSuchElementException.class, () ->
                uut.execute()
        );
    }

    @Test
    void gradeExists_execute_subjectUpdated() throws Exception {
        final int newGrade = 2;
        final String subjectName = "subject";
        final Date date = new Date(0);

        uut = new EditGrade("subject", date, newGrade,
                connectionToDatabase, schoolSubjectRepository, schoolSubjectGradeRepository, gradeRepository
        );

        doReturn(Optional.of(new SchoolSubject(0, subjectName))).when(schoolSubjectRepository).getByName(any());


        doReturn(new SchoolSubjectGrade(new Grade(0, 1), new SchoolSubject(0, subjectName), date))
                .when(schoolSubjectGradeRepository).getBySubjectAndDate(any(), any());

        doReturn(new Grade(0, newGrade))
                .when(gradeRepository).getByGradeOrCreate(newGrade);

        uut.execute();

        verify(schoolSubjectGradeRepository, atLeastOnce()).update(any());
    }

}