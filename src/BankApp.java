import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BankApp {
    private static final BankServices bankService = new BankServices();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("===== Bank Management System  =====");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> openAccount();
                    case 2 -> deposit();
                    case 3 -> withdraw();
                    case 4 -> transfer();
                    case 5 -> viewAccount();
                    case 6 -> viewStatement();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option, try again.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("=======BANK SERVICES=======");
        System.out.println("\n1. Open Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer Funds");
        System.out.println("5. View Account");
        System.out.println("6. View Mini Statement");
        System.out.println("7. List All Accounts");
        System.out.println("0. Exit");
    }

    private static void openAccount() throws Exception {
        System.out.print("Holder name: ");
        String name = scanner.nextLine();
        System.out.print("Account type (SAVINGS/CURRENT): ");
        String type = scanner.nextLine().trim().toUpperCase();
        double initialBalance = readDouble("Initial balance: ");
        double overdraft = 0;
        if ("CURRENT".equals(type)) {
            overdraft = readDouble("Overdraft limit: ");
        }
        int id = bankService.openAccount(name, type, initialBalance, overdraft);
        System.out.println("Account opened with id: " + id);
    }

    private static void deposit() throws Exception {
        int accountId = readInt("Account id: ");
        double amount = readDouble("Amount to deposit: ");
        bankService.deposit(accountId, amount);
        System.out.println("Deposit successful.");
    }

        private static void withdraw() throws Exception {
        int accountId = readInt("Account id: ");
        double amount = readDouble("Amount to withdraw: ");
        bankService.withdraw(accountId, amount);
        System.out.println("Withdrawal successful.");
    }
    private static void transfer() throws Exception {
        int from = readInt("From account id: ");
        int to = readInt("To account id: ");
        double amount = readDouble("Amount to transfer: ");
        bankService.transfer(from, to, amount);
        System.out.println("Transfer successful.");
    }
    private static void viewAccount() throws Exception {
        int accountId = readInt("Account id: ");
        BankAccount account = bankService.getAccount(accountId);
        if (account == null) {
            System.out.println("No such account.");
            return;
        }
        System.out.println(account);
    }
    private static void viewStatement() throws Exception{
        int accountId=readInt("Account id");
        List<Transactions> history=bankService.getStatement(accountId);
        if (history.isEmpty()){
            System.out.println("No Transaction History Found");
        }
        for (Transactions t:history){
            System.out.println(t);
        }

    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print(prompt +"should be in number"+"Please enter a number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("Please enter a number: ");
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }
}
