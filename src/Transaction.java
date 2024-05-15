import java.io.File;
import java.util.Scanner;

public class Transaction {
    private final static StudentService studentService = new StudentServiceImplement();

    public static void checkAndCommitData() {
        File transactionFile = new File("students_transaction.csv");
        if (transactionFile.length() > 0) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("[!] There are pending transactions. Do you want to commit the data before exiting? (Y/N): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("y")) {
                studentService.commitData();
                System.out.println("[+] Data committed successfully.");
            } else {
                System.out.println("[!] Data not committed.");
            }
        }
    }

    public static void checkTransactionFile() {
        File transactionFile = new File("students_transaction.csv");
        if (transactionFile.length() > 0) {
            System.out.println("[!] Pending transactions found. Please commit the data.");
        }
    }
}
