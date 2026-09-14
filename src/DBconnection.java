import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnection {
    public  static Connection get()throws Exception {
        Connection connection = null;
        String url = "jdbc:sqlserver://pavangowda\\SQLEXPRESS;databaseName=BankManagementDB;encrypt=true;trustServerCertificate=true;";
        String username = "javauser";
        String password = "Java@123";
        connection = DriverManager.getConnection(url, username, password);
        return connection;
    }
}
