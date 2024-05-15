package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {
    private int studentId;
    private String studentName;
    private Integer studentBirthYear;
    private Integer studentBirthMonth;
    private Integer studentBirthDay;
    private LocalDate date;
    private String[] classes;
    private String[] subjects;
    private String createdAt;

    public void setDateFromString(String dateString) {
        String[] parts = dateString.split("-");
        if (parts.length == 3) {
            this.studentBirthYear = Integer.parseInt(parts[0]);
            this.studentBirthMonth = Integer.parseInt(parts[1]);
            this.studentBirthDay = Integer.parseInt(parts[2]);
            updateDate();
        } else {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    private void updateDate() {
        if (studentBirthYear != null && studentBirthMonth != null && studentBirthDay != null) {
            this.date = LocalDate.of(studentBirthYear, studentBirthMonth, studentBirthDay);
        }
    }

    public static int generateStudentId() {
        Random random = new Random();
        return  1000 + random.nextInt(9000); // Generates a random 6-digit number
    }

    public void updateCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.createdAt = LocalDate.now().format(formatter);
    }
}
