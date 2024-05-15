import model.Student;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.*;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class StudentServiceImplement implements StudentService {
    private static final String TRANSACTION_FILE = "students_transaction.csv";
    private static final String MAIN_FILE = "students.csv";
    private static final int RECORDS_PER_PAGE = 5;

    @Override
    public void addNewStudent() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(">".repeat(30));

        System.out.print("[+] Insert student's name: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) {
            System.out.println("[!] Name cannot be empty.");
            return;
        }

        System.out.println("[+] STUDENT DATE OF BIRTH");
        System.out.print("> Year (number): ");
        int year = scanner.nextInt();
        System.out.print("> Month (number): ");
        int month = scanner.nextInt();
        System.out.print("> Day (number): ");
        int day = scanner.nextInt();
        scanner.nextLine();

        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            System.out.println("[!] Invalid date entered. Please ensure the date is correct.");
            return;
        }

        System.out.println("[!] You can insert multiple classes by splitting [,] symbol (C1,C2).");
        System.out.print("[+] Student's class: ");
        String classesInput = scanner.nextLine();
        String[] classes = classesInput.split(",");

        System.out.println("[!] You can insert multiple subjects by splitting [,] symbol (S1,S2).");
        System.out.print("[+] Subject studied: ");
        String subjectsInput = scanner.nextLine();
        String[] subjects = subjectsInput.split(",");

        int studentId = Student.generateStudentId();
        Student student = new Student(studentId, name, year, month, day, dateOfBirth, classes, subjects, null);
        student.updateCreatedAt();

        String data = student.getStudentId() + "," +
                student.getStudentName() + "," +
                student.getStudentBirthYear() + "," +
                student.getStudentBirthMonth() + "," +
                student.getStudentBirthDay() + "," +
                String.join(";", student.getClasses()) + "," +
                String.join(";", student.getSubjects()) + "," +
                student.getCreatedAt();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(TRANSACTION_FILE, true))) {
            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("[!] Problem during adding new student: " + e.getMessage());
            return;
        }

        System.out.println("[+] Added new student successfully to transaction file.");
    }

    @Override
    public void displayAllStudents() {
        List<Student> students = loadStudents();
        int totalRecords = students.size();
        int totalPages = (int) Math.ceil((double) totalRecords / RECORDS_PER_PAGE);

        Scanner scanner = new Scanner(System.in);
        int currentPage = 1;

        while (true) {
            int start = (currentPage - 1) * RECORDS_PER_PAGE;
            int end = Math.min(start + RECORDS_PER_PAGE, totalRecords);

            Table table = new Table(6, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.ALL);
            table.addCell("ID");
            table.addCell("STUDENT'S NAME");
            table.addCell("STUDENT'S DATE OF BIRTH");
            table.addCell("STUDENT CLASS");
            table.addCell("STUDENT'S SUBJECT");
            table.addCell("CREATED/UPDATED AT");

            for (int i = start; i < end; i++) {
                Student student = students.get(i);
                String id = String.valueOf(student.getStudentId());
                String name = student.getStudentName();
                String dob = student.getStudentBirthYear() + "-" + student.getStudentBirthMonth() + "-" + student.getStudentBirthDay();
                String classes = formatClassesOrSubjects(String.join(";", student.getClasses()));
                String subjects = formatClassesOrSubjects(String.join(";", student.getSubjects()));
                String createdAt = student.getCreatedAt();

                table.addCell(id);
                table.addCell(name);
                table.addCell(dob);
                table.addCell(classes);
                table.addCell(subjects);
                table.addCell(createdAt);
            }

            System.out.println(table.render());
            System.out.printf("[*] Page Number: %d [*] Actual record: %d [*] All Record: %d%n", currentPage, end - start, totalRecords);

            System.out.print("[+] Insert to Navigate [p/N/b]: ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("p") && currentPage > 1) {
                currentPage--;
            } else if (input.equals("n") && currentPage < totalPages) {
                currentPage++;
            } else if (input.equals("b")) {
                break;
            } else {
                System.out.println("Invalid input, please try again.");
            }
        }
    }

    @Override
    public void commitData() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(TRANSACTION_FILE));
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(MAIN_FILE, true))) {

            String data;
            while ((data = bufferedReader.readLine()) != null) {
                bufferedWriter.write(data);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            clearTransactionFile();

            System.out.println("Data committed successfully.");
        } catch (IOException e) {
            System.out.println("[!] Problem during committing data: " + e.getMessage());
        }
    }

    @Override
    public void updateStudent() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("[+] Enter student ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        List<Student> students = loadStudents();
        boolean found = false;

        for (Student student : students) {
            if (student.getStudentId() == id) {
                System.out.print("[+] Enter new name: ");
                student.setStudentName(scanner.nextLine());

                System.out.println("[+] STUDENT DATE OF BIRTH");
                System.out.print("> Year (number): ");
                int year = scanner.nextInt();
                System.out.print("> Month (number): ");
                int month = scanner.nextInt();
                System.out.print("> Day (number): ");
                int day = scanner.nextInt();
                scanner.nextLine();

                LocalDate dateOfBirth;
                try {
                    dateOfBirth = LocalDate.of(year, month, day);
                    student.setDateFromString(dateOfBirth.toString());
                } catch (DateTimeException e) {
                    System.out.println("[!] Invalid date entered. Please ensure the date is correct.");
                    return;
                }

                System.out.println("[!] You can insert multiple classes by splitting [,] symbol (C1,C2).");
                System.out.print("[+] Student's class: ");
                String classesInput = scanner.nextLine();
                student.setClasses(classesInput.split(","));

                System.out.println("[!] You can insert multiple subjects by splitting [,] symbol (S1,S2).");
                System.out.print("[+] Subject studied: ");
                String subjectsInput = scanner.nextLine();
                student.setSubjects(subjectsInput.split(","));

                student.updateCreatedAt();

                found = true;
                break;
            }
        }

        if (found) {
            saveStudentsToTransaction(students);
            System.out.println("[+] Student updated successfully.");
            displayAllStudents();
        } else {
            System.out.println("[!] Student not found.");
        }
    }

    @Override
    public void removeStudentById() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("[+] Enter student ID to remove: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        List<Student> students = loadStudents();
        boolean removed = students.removeIf(student -> student.getStudentId() == id);

        if (removed) {
            saveStudentsToTransaction(students);
            System.out.println("[+] Student removed successfully.");
            displayAllStudents();
        } else {
            System.out.println("[!] Student not found.");
        }
    }

    @Override
    public void searchStudent() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("[+] Enter student name to search: ");
        String name = scanner.nextLine().trim().toLowerCase();

        List<Student> students = loadStudents();
        boolean found = false;

        Table table = new Table(6, BorderStyle.UNICODE_BOX_DOUBLE_BORDER_WIDE, ShownBorders.ALL);
        table.addCell("ID");
        table.addCell("STUDENT'S NAME");
        table.addCell("STUDENT'S DATE OF BIRTH");
        table.addCell("STUDENT CLASS");
        table.addCell("STUDENT'S SUBJECT");
        table.addCell("CREATED/UPDATED AT");

        for (Student student : students) {
            if (student.getStudentName().toLowerCase().contains(name)) {
                String id = String.valueOf(student.getStudentId());
                String studentName = student.getStudentName();
                String dob = student.getStudentBirthYear() + "-" + student.getStudentBirthMonth() + "-" + student.getStudentBirthDay();
                String classes = formatClassesOrSubjects(String.join(";", student.getClasses()));
                String subjects = formatClassesOrSubjects(String.join(";", student.getSubjects()));
                String createdAt = student.getCreatedAt();

                table.addCell(id);
                table.addCell(studentName);
                table.addCell(dob);
                table.addCell(classes);
                table.addCell(subjects);
                table.addCell(createdAt);

                found = true;
            }
        }

        if (found) {
            System.out.println(table.render());
        } else {
            System.out.println("[!] No student found with the name: " + name);
        }
    }

    @Override
    public void clearAllData() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(MAIN_FILE))) {
            bufferedWriter.write("");
            System.out.println("[+] All data cleared successfully.");
        } catch (IOException e) {
            System.out.println("[!] Problem during clearing all data: " + e.getMessage());
        }
    }

    @Override
    public void generateDataToFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("[+] Number of objects you want to generate: ");
        long numberOfObjects;
        try {
            numberOfObjects = scanner.nextLong();
            if (numberOfObjects < 1) {
                System.out.println("[!] Invalid number of objects. Please enter a value greater than 0.");
                return;
            }
        } catch (Exception e) {
            System.out.println("[!] Invalid input. Please enter a numeric value.");
            return;
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(TRANSACTION_FILE, true))) {
            for (long i = 1; i <= numberOfObjects; i++) {
                int studentId = Student.generateStudentId();
                String name = "Student" + i;
                int year = 2000 + (new Random().nextInt(20));
                int month = 1 + (new Random().nextInt(12));
                int day = 1 + (new Random().nextInt(28));
                LocalDate dateOfBirth = LocalDate.of(year, month, day);
                String[] classes = {"Class1", "Class2"};
                String[] subjects = {"Subject1", "Subject2"};
                String createdAt = LocalDate.now().toString();

                Student student = new Student(studentId, name, year, month, day, dateOfBirth, classes, subjects, createdAt);
                String data = student.getStudentId() + "," +
                        student.getStudentName() + "," +
                        student.getStudentBirthYear() + "," +
                        student.getStudentBirthMonth() + "," +
                        student.getStudentBirthDay() + "," +
                        String.join(";", student.getClasses()) + "," +
                        String.join(";", student.getSubjects()) + "," +
                        student.getCreatedAt();

                bufferedWriter.write(data);
                bufferedWriter.newLine();

                if (i % 10_000_000 == 0) {
                    System.out.println("Generated " + i + " records...");
                }
            }
            bufferedWriter.flush();
            System.out.println("[+] Generated " + numberOfObjects + " student records successfully.");
        } catch (IOException e) {
            System.out.println("[!] Problem during generating data to transaction file: " + e.getMessage());
        }

        // Commit the generated data from the transaction file to the main file
        commitData();
    }

    @Override
    public List<Student> loadStudents() {
        long startTime = System.currentTimeMillis();
        List<Student> students = new ArrayList<>();
        int recordCount = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(MAIN_FILE))) {
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                String[] values = data.split(",");
                if (values.length < 8) {
                    System.out.println("[!] Skipping malformed line: " + data);
                    continue;
                }

                try {
                    int id = Integer.parseInt(values[0]);
                    String name = values[1];
                    int year = Integer.parseInt(values[2]);
                    int month = Integer.parseInt(values[3]);
                    int day = Integer.parseInt(values[4]);
                    LocalDate dateOfBirth = LocalDate.of(year, month, day);
                    String[] classes = values[5].split(";");
                    String[] subjects = values[6].split(";");
                    String createdAt = values[7];

                    Student student = new Student(id, name, year, month, day, dateOfBirth, classes, subjects, createdAt);
                    students.add(student);
                    recordCount++;
                } catch (Exception e) {
                    System.out.println("[!] Problem parsing line: " + data);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("[!] Problem during loading students: " + e.getMessage());
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        double timeSpent = (endTime - startTime) / 1000.0;
        System.out.printf("[*] SPENT TIME FOR READING DATA: %.3fS%n", timeSpent);
        System.out.printf("[*] NUMBER OF RECORD IN DATA SOURCE FILE: %d%n", recordCount);

        return students;
    }

    private void saveStudentsToTransaction(List<Student> students) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(TRANSACTION_FILE))) {
            for (Student student : students) {
                String data = student.getStudentId() + "," +
                        student.getStudentName() + "," +
                        student.getStudentBirthYear() + "," +
                        student.getStudentBirthMonth() + "," +
                        student.getStudentBirthDay() + "," +
                        String.join(";", student.getClasses()) + "," +
                        String.join(";", student.getSubjects()) + "," +
                        student.getCreatedAt();
                bufferedWriter.write(data);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("[!] Problem during saving students to transaction file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearTransactionFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(TRANSACTION_FILE))) {
            bufferedWriter.write("");
        } catch (IOException e) {
            System.out.println("[!] Problem during clearing transaction file: " + e.getMessage());
        }
    }

    private String formatClassesOrSubjects(String csvData) {
        String[] items = csvData.split(";");
        return "[" + String.join(", ", items) + "]";
    }
}
