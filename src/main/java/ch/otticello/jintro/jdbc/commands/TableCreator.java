package ch.otticello.jintro.jdbc.commands;

import ch.otticello.jintro.jdbc.repository.ConnectionToDatabase;

import java.sql.SQLException;

public class TableCreator implements Command {
    private static final String CREATE_SCHOOL_SUBJECT_QUERY = """
            CREATE TABLE IF NOT EXISTS School_Subject (
                id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(50),
                CONSTRAINT UC_School_Subject_name UNIQUE (name)
            )
            """;
    private static final String CREATE_GRADE_QUERY = """
            CREATE TABLE IF NOT EXISTS Grade (
                id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                grade FLOAT(2)
            )
            """;
    private static final String CREATE_SCHOOL_SUBJECT_GRADE_QUERY = """
            CREATE TABLE IF NOT EXISTS School_Subject_Grade (
                grade_id INT NOT NULL,
                school_subject_id INT NOT NULL,
                date DATE NOT NULL,
                PRIMARY KEY (school_subject_id, date),
                FOREIGN KEY (grade_id) REFERENCES Grade(id),
                FOREIGN KEY (school_subject_id) REFERENCES School_Subject(id)
            )
            """;

    @Override
    public void execute() {
        try (var connection = new ConnectionToDatabase()) {
            connection.connect();

            var statement = connection.getStatement();

            statement.executeUpdate(CREATE_GRADE_QUERY);
            statement.executeUpdate(CREATE_SCHOOL_SUBJECT_QUERY);
            statement.executeUpdate(CREATE_SCHOOL_SUBJECT_GRADE_QUERY);
        } catch (SQLException ex) {
            System.out.println("Can't create the tables, see:");
            System.out.println(ex);
        }

    }
    public static void main(String[] args) {
        var instance = new TableCreator();
        instance.execute();
    }

}
