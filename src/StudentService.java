import model.Student;

import java.util.List;

interface StudentService {
    void addNewStudent();
    void displayAllStudents();
    void commitData();
    void updateStudent();
    void removeStudentById();
    void searchStudent();
    void clearAllData();
    void generateDataToFile();
    List<Student> loadStudents();  // Add this method
}
