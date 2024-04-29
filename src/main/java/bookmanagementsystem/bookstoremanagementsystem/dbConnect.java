package bookmanagementsystem.bookstoremanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class dbConnect {

    private static String HOST = "127.0.0.1";
    private static int PORT = 3306;
    private static String DB_NAME = "bookstore";
    private static String USERNAME = "root";
    private static String PASSWORD = "root";
    private static Connection con;


    public static Connection getConnect() {
        try {
            try {
                Class.forName( "com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            con = DriverManager.getConnection("jdbc:mysql://localhost/bookstore", "root", "");
        } catch (SQLException ex) {
            Logger.getLogger(dbConnect.class.getName()).log(Level.SEVERE, null, ex);
        }

        return con;
    }
}




