import java.util.Scanner;

public class Menu {
    public static int menu(){
        System.out.println("=".repeat(150));
        System.out.println("1. ADD NEW STUDENT\t2. LIST ALL STUDENTS\t3. COMMIT DATA TO FILE");
        System.out.println("4. SEARCH FOR STUDENT\t5. UPDATE STUDENT'S INFO BY ID\t6. DELETE STUDENT'S DATA");
        System.out.println("7. GENERATE DATA TO FILE\t\t8. DELETE/CLEAR ALL DATA FROM DATA STORE");
        System.out.println("0. 99. Exit");
        System.out.println("=".repeat(150));
        System.out.print("Enter your choice: ");
        return new Scanner(System.in).nextInt();
    }
}
