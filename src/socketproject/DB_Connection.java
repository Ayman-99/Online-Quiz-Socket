package socketproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class User {

    public String username;
    public String password;

    public User(String username, String password, String score) {
        this.username = username;
        this.password = password;
    }
}

public class DB_Connection {

    static Connection conn = getConnection();

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaquiz", "root", "");
            return conn;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DB_Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        try {
            String sql = "SELECT `USERNAME`, `PASSWORD` FROM `users` WHERE 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB_Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    public static void insertUser(String username, String password) throws SQLException {
        String sql = "insert into users values ('" + username + "','" + password + "','0');";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.execute();
    }

    public static void updateScore(String username, int score) throws SQLException {
        String sql = "update users set score=" + score + " where username='" + username + "';";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.execute();
    }

    public static int getScore(String username) throws SQLException {
        String sql = "SELECT score FROM `users` WHERE username='" + username + "'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("score");
        }
        return -1;
    }
}




