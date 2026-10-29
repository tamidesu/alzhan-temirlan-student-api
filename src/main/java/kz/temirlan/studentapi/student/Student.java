package kz.temirlan.studentapi.student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private String studentNumber;

    protected Student() {
    }

    public Student(String fullName, String email, String groupName, String studentNumber) {
        updateDetails(fullName, email, groupName, studentNumber);
    }

    public void updateDetails(String fullName, String email, String groupName, String studentNumber) {
        this.fullName = fullName;
        this.email = email;
        this.groupName = groupName;
        this.studentNumber = studentNumber;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }
}
