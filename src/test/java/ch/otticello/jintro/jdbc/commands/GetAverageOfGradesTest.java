package ch.otticello.jintro.jdbc.commands;

import ch.otticello.jintro.jdbc.model.Grade;
import ch.otticello.jintro.jdbc.model.SchoolSubject;
import ch.otticello.jintro.jdbc.model.SchoolSubjectGrade;
import ch.otticello.jintro.jdbc.repository.ConnectionToDatabase;
import ch.otticello.jintro.jdbc.repository.SchoolSubjectGradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAverageOfGradesTest {

    private GetAverageOfGrades uut;

    @Mock
    ConnectionToDatabase connectionToDatabase;
    @Mock
    SchoolSubjectGradeRepository schoolSubjectGradeRepository;

    @BeforeEach
    void setUp() {
        uut = new GetAverageOfGrades("Math",
                connectionToDatabase, schoolSubjectGradeRepository);
    }

    @Test
    void gradesExist_execute_calculateAvg() throws Exception {
        final Date date = new Date(0);
        // Arrange
        List<SchoolSubjectGrade> grades = Arrays.asList(
                new SchoolSubjectGrade(new Grade(0, 90), new SchoolSubject(0, "Math"), date),
                new SchoolSubjectGrade(new Grade(0, 80), new SchoolSubject(0, "Math"), date)
        );
        when(schoolSubjectGradeRepository.getAll()).thenReturn(grades);

        // Capture system output
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        uut.execute();

        // Assert
        String expectedOutput = "Average of Math: 85.0\n";
        assertEquals(expectedOutput, outContent.toString().replace("\r\n", "\n"));
        verify(schoolSubjectGradeRepository).getAll();
    }

    @Test
    void noGradesYet_execute_noEntriesYetMessage() throws Exception {
        when(schoolSubjectGradeRepository.getAll()).thenReturn(List.of());

        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        uut.execute();

        String expectedOutput = "This School Subject has no entries yet: Math\n";
        assertEquals(expectedOutput, outContent.toString().replace("\r\n", "\n"));
        verify(schoolSubjectGradeRepository).getAll();
    }

}