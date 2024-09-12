package com.carbontracker.config;

        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.SQLException;

public class DbConnection {

    private static  String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static  String USER = "GreenPulse";
    private static  String PASSWORD = "";
    private static DbConnection instance;
    private static Connection connection;
    private   DbConnection(){
        try {
            connection = DriverManager.getConnection(URL , USER , PASSWORD);
            System.out.println( "\u001B[92m" + "Connected to the database." + "\u001B[0m" );
        }catch (SQLException e){
            System.err.println("Failed to make connection" + e.getMessage());
        }
    }
    public  static DbConnection getInstance(){
        if(instance == null){
            instance = new DbConnection();
        }
        return instance;

    }
    public static Connection getConnection(){
        return connection;
    }

}
