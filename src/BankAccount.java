public class BankAccount {
    private int account_no;
    private String holder_name;
    private String account_type;
    private double balance;
    private double overdraft_limit;

    public BankAccount(int account_no,String holder_name,String account_type,double balance,
                       double overdraft_limit){
        this.account_no=account_no;
        this.holder_name=holder_name;
        this.account_type=account_type;
        this.balance=balance;
        this.overdraft_limit=overdraft_limit;
    }

    public boolean can_withdraw(double amount){
        if ("CURRENT".equals(account_type)){
            return amount<=(balance +overdraft_limit);
        }
        return amount<=balance;
    }

    public int getAccount_no() {
        return account_no;
    }

    public String getHolder_name() {
        return holder_name;
    }

    public String getAccount_type() {
        return account_type;
    }

    public double getBalance() {
        return balance;
    }

    public double getOverdraft_limit() {
        return overdraft_limit;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "account_no=" + account_no +
                ", holder_name='" + holder_name + '\'' +
                ", account_type='" + account_type + '\'' +
                ", balance=" + balance +
                ", overdraft_limit=" + overdraft_limit +
                '}';
    }
}
