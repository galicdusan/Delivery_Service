/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.*;
import rs.etf.sab.operations.StockroomOperations;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Dusan
 */
public class gd140092_StockroomOperations implements StockroomOperations{

    @Override
    public int insertStockroom(int i) {
        Connection conn = DB.getInstance().getConnection(); 
        int idM = -1;
        try( PreparedStatement ps = conn.prepareStatement(
                "insert into Magacin values (?)", 
                Statement.RETURN_GENERATED_KEYS);
             PreparedStatement ps2 = conn.prepareStatement(
            "select M.IDMagacin from Magacin M, Adresa A where M.IDAdresa = A.IDAdresa and A.IDGrad = (select IDGrad from Adresa where IDAdresa = ?)");){
            ps2.setInt(1, i);
            ResultSet rs = ps2.executeQuery();
            if(rs.next()){
                rs.close();
                return -1;
            }
            rs.close();
            
            
            ps.setInt(1, i);
            ps.executeUpdate();
            
            if (ps.getGeneratedKeys().next())
                idM = ps.getGeneratedKeys().getInt(1);            
            ps.getGeneratedKeys().close();
            
        } catch(SQLException ex){}
        
        return idM;
    }

    @Override
    public boolean deleteStockroom(int i) {
        Connection conn = DB.getInstance().getConnection(); 
        try( PreparedStatement ps = conn.prepareStatement("select * from Parkiran where IDMagacin = ?"); 
             PreparedStatement ps2 = conn.prepareStatement("delete from Magacin where IDMagacin = ?");
             PreparedStatement ps3 = conn.prepareStatement("select * from Magacin where IDMagacin = ?");
                ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                rs.close();
                return false;
            }
            
            ps3.setInt(1, i);
            rs = ps3.executeQuery();
            if(rs.next()){
                
            } else {
                rs.close();
                return false;
            }
            
            ps2.setInt(1, i);
            ps2.executeUpdate();
            
            rs.close();
        } catch (SQLException ex) {}
        return true;
    }

    @Override
    public int deleteStockroomFromCity(int idCity) {
        Connection conn = DB.getInstance().getConnection();
        int i = -1;
        try( PreparedStatement ps = conn.prepareStatement(
                "select IDMagacin from Magacin M, Adresa A where M.IDAdresa = A.IDAdresa and A.IDGrad = ?"); ){
            ps.setInt(1, idCity);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                i = rs.getInt(1);
            }
            else {
                rs.close();
                return -1;
            }
            rs.close();
        } catch(SQLException ex){}
        
        try( PreparedStatement ps = conn.prepareStatement("select * from Parkiran where IDMagacin = ?"); 
             PreparedStatement ps2 = conn.prepareStatement("delete from Magacin where IDMagacin = ?");){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                rs.close();
                return -1;
            }
            ps2.setInt(1, i);
            ps2.executeUpdate();
            
            rs.close();
        } catch (SQLException ex) {}
        return i;
    }

    @Override
    public List<Integer> getAllStockrooms() {
        List<Integer> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection(); 
        try( Statement ps = conn.createStatement(); ){
            ResultSet rs = ps.executeQuery("select IDMagacin from Magacin");
            while(rs.next()){
                rezultat.add( rs.getInt(1) );
            }
            rs.close();
        } catch(SQLException ex){}
        return rezultat;
    }
    
}
