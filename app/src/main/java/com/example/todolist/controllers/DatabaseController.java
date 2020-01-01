package com.example.todolist.controllers;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseController {

    private Connection con;

    /**
     * @param DBName Database's name that you want to connect
     * @param DBUser Database's username
     * @param DBPwd  Database's password, If there isn't any password put null
     */
    public void connectToMySqlDB(String DBName, String DBUser, String DBPwd) {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Setup the connection with the DB
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DBName, DBUser, DBPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Connection getConnection(){
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Setup the connection with the DB
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + "photo", "root", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
    public void closeConnection(){
        try {
            this.con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}