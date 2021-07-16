/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Dusan
 */
public class DB {
    private static final String url = "jdbc:sqlserver://localhost:1433;databaseName=KurirskaSluzba";
    private static DB db = null;
    private Connection connection;
    public Connection getConnection(){
        return connection;
    }
    private DB(){
        try {
            connection = DriverManager.getConnection(url, "sa", "1234567");
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static DB getInstance(){
        if (db==null)
            db = new DB();
        return db;
    }
}
