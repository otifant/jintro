package ch.otticello.jintro.jdbc.model;

import java.sql.Date;

public class SchoolSubjectGrade {

    private Grade grade;
    private final SchoolSubject subject;
    private final Date date;

    public SchoolSubjectGrade(Grade grade, SchoolSubject subject, Date date) {
        this.grade = grade;
        this.subject = subject;
        this.date = date;
    }

    /* GETTER */
    public Grade getGrade() {
        return grade;
    }

    /** TODO: Remove this method so we can transform this class to a record. */
    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public SchoolSubject getSchoolSubject() {
        return subject;
    }

    public Date getDate() {
        return date;
    }

}
