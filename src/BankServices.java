import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankServices {
    // ------- Create a new account ------------------------
    public int openAccount(String holder_name, String account_type, double balance, double overdraft_limit) throws Exception {
        try (Connection conn = DBconnection.get()) {
            String sql = "insert into Accounts (holder_name, account_type, balance, overdraft_limit)" + "values(?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, holder_name);
            pst.setString(2, account_type);
            pst.setDouble(3, balance);
            pst.setDouble(4, overdraft_limit);
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new Exception("Account Creation Failed");
    }
    // -------- Look up one account -------------------------
    public BankAccount getAccount(int account_no) throws Exception {
        try (Connection conn = DBconnection.get()) {
            String sql = "select * from Accounts where account_id =?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, account_no);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }return null;
    }
    public void deposit(int account_id,double amount) throws Exception {
        if (amount<=100){
            throw new IllegalArgumentException("Amount must greater than 100 Rs");
        }
        try (Connection conn=DBconnection.get()){
            conn.setAutoCommit(false);
            try{
            BankAccount account=lockAndGetAccount(conn,account_id);
            if (account==null){
                throw new SQLException(" Account with Account ID"+account_id+ "Not Found");
            }
            double newBalance=account.getBalance()+amount;
            updateBalance(conn,account_id,newBalance);
            insertTransaction(conn,new Transactions(account_id,"deposit".toUpperCase(),amount,newBalance));
            conn.commit();
        }catch (SQLException e){
                conn.rollback();
                throw e;
        }finally {
                conn.setAutoCommit(true);
            }
        }
    }
    public void withdraw(int account_id, double amount) throws Exception{
        if (amount<=100){
            throw new IllegalArgumentException("Withdraw Amount Must be Greater than 100 Rs");
        }
        try(Connection conn =DBconnection.get()){
            conn.setAutoCommit(false);
            try{
                BankAccount account=lockAndGetAccount(conn,account_id);
                if (account==null){
                    throw new SQLException(" Account with Account ID"+account_id+ "Not Found");
                }
                if (!account.can_withdraw(amount)){
                    throw new SQLException("Insufficients Funds in Your Account");
            }
                double newBalance=account.getBalance()-amount;
                updateBalance(conn,account_id,newBalance);
                insertTransaction(conn, new Transactions(account_id, "withdrawal".toUpperCase(), amount,
                        newBalance));
                conn.commit();
        }catch (SQLException e){
                conn.rollback();
                throw e;
        }finally {
                conn.setAutoCommit(true);
            }
    }}
    public void transfer(int fromId, int toId, double amount) throws Exception{
        if (amount<=100){
            throw new IllegalArgumentException("Transfer Amount Must Be Greater Than 100 Rs");
        }if (toId==fromId){
            throw new IllegalArgumentException("Transfer of Amount Between Same Account is not Possible");
        }try(Connection conn=DBconnection.get()) {
            conn.setAutoCommit(false);
            try {
                BankAccount from_account = lockAndGetAccount(conn, fromId);
                BankAccount to_account = lockAndGetAccount(conn, toId);
                if (from_account == null) {
                    throw new SQLException("Sender Account with Account ID " + fromId + " Not Found");
                }
                if (to_account == null) {
                    throw new SQLException("Reciever Account with Account ID" + toId + " Not Found");
                }
                if (!from_account.can_withdraw(amount)) {
                    throw new SQLException("Insufficients Funds in Sender Account");
                }
                double from_newBalance = from_account.getBalance() - amount;
                double to_newBalance = to_account.getBalance() + amount;
                updateBalance(conn, fromId, from_newBalance);
                updateBalance(conn, toId, to_newBalance);
                insertTransaction(conn, new Transactions(fromId, "TRANSFER_OUT", amount,
                        from_newBalance));
                insertTransaction(conn, new Transactions(toId, "TRANSFER_IN", amount,
                        to_newBalance));
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    public List<Transactions> getStatement(int account_id) throws Exception{
        String sql="select * from Transactions where account_id=? order by transaction_date desc";
        List<Transactions> history = new ArrayList<>();
        try(Connection conn =DBconnection.get();
            PreparedStatement pst= conn.prepareStatement(sql)){
            pst.setInt(1,account_id);
            try(ResultSet rs= pst.executeQuery()){
                while (rs.next()){
                    history.add(new Transactions( rs.getInt("account_id"),
                            rs.getString("transaction_type"),
                            rs.getDouble("amount"),
                            rs.getDouble("balance_after"),
                            rs.getTimestamp("transaction_date").toLocalDateTime()));
                }
            }

        }
        return history;
    }

    private BankAccount lockAndGetAccount(Connection conn, int accountId)throws SQLException{
        String sql="select * from Accounts with(UPDLOCK, ROWLOCK) where account_id=?";
        try(PreparedStatement pst=conn.prepareStatement(sql)){
            pst.setInt(1, accountId);
            try(ResultSet rs=pst.executeQuery()){
                return rs.next()?mapRow(rs):null;
            }
        }
    }
    private void updateBalance(Connection conn, int accountId, double newBalance) throws SQLException{
        String sql="update Accounts set balance=? where account_id=?";
        try(PreparedStatement pst= conn.prepareStatement(sql)){
            pst.setDouble(1,newBalance);
            pst.setInt(2,accountId);
            pst.executeUpdate();
        }
    }
    private void insertTransaction(Connection conn ,Transactions t)throws SQLException{
        String sql="insert into Transactions (account_id,transaction_type, amount, balance_after) VALUES (?, ?, ?, ?)";
        try(PreparedStatement pst=conn.prepareStatement(sql)){
            pst.setInt(1,t.getAccountId());
            pst.setString(2,t.getType());
            pst.setDouble(3,t.getAmount());
            pst.setDouble(4,t.getBalanceAfter());
            pst.executeUpdate();
        }
    }
    private BankAccount mapRow(ResultSet rs) throws SQLException {
        return new BankAccount(
                rs.getInt("account_id"),
                rs.getString("holder_name"),
                rs.getString("account_type"),
                rs.getDouble("balance"),
                rs.getDouble("overdraft_limit")
        );
    }
}