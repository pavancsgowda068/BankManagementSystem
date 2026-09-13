import java.time.LocalDateTime;

public class Transactions {
    private int accountId;
    private String type;          // DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    private double amount;
    private double balanceAfter;
    private LocalDateTime date;
    // Used when creating a new transaction to insert
    public Transactions(int accountId, String type, double amount, double balanceAfter) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }
    // Used when reading a transaction back from the database
    public Transactions(int accountId, String type, double amount,
                       double balanceAfter, LocalDateTime date) {
        this(accountId, type, amount, balanceAfter);
        this.date = date;
    }
    public int getAccountId() { return accountId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getDate() { return date; }
    @Override
    public String toString() {
        return String.format("[%s] %-12s Rs.%-10.2f Balance after: Rs.%-10.2f",
                date, type, amount, balanceAfter);
    }
}
