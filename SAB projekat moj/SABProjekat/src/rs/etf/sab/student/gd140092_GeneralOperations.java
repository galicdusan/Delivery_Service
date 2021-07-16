/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.*;
import rs.etf.sab.operations.GeneralOperations;
/**
 *
 * @author Dusan
 */
public class gd140092_GeneralOperations implements GeneralOperations{

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConnection(); 
        try( CallableStatement cs = conn.prepareCall("{call spDeleteAllData}"); ){
            cs.executeUpdate();
        } catch(SQLException ex){}
    }
    
}
