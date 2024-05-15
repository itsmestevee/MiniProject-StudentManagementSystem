import design.Header;
import java.io.File;

public class Application {
    private final static StudentService studentService = new StudentServiceImplement();

    public static void main(String[] args) {
        Header.header();
        checkTransactionFile();
        displayInitialLoadMessage();
        while (true) {
            switch (Menu.menu()) {
                case 1 -> studentService.addNewStudent();
                case 2 -> studentService.displayAllStudents();
                case 3 -> studentService.commitData();
                case 4 -> studentService.searchStudent();
                case 5 -> studentService.updateStudent();
                case 6 -> studentService.removeStudentById();
                case 7 -> studentService.generateDataToFile();
                case 8 -> studentService.clearAllData();
                case 99 -> {
                    System.out.println("Exiting...");
                    Transaction.checkAndCommitData();
                    System.exit(0);
                }
                default -> System.out.println("No Option!!!");
            }
        }
    }

    private static void checkTransactionFile() {
        File transactionFile = new File("students_transaction.csv");
        if (transactionFile.length() > 0) {
            System.out.println("[!] Pending transactions found. Please commit the data.");
        }
    }

    private static void displayInitialLoadMessage() {
        System.out.println("[*] Initializing student data loading...");
        studentService.loadStudents();
    }
}
