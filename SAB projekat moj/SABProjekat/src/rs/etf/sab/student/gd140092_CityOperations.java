/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.*;
import rs.etf.sab.operations.CityOperations;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Dusan
 */
public class gd140092_CityOperations implements CityOperations{

    @Override
    public int insertCity(String string, String string1) {
        Connection conn = DB.getInstance().getConnection(); 
        int idG = -1;
        try( PreparedStatement ps = conn.prepareStatement("insert into Grad values (?,?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement ps2 = conn.prepareStatement("select IDGrad from Grad where Naziv = ?");
             PreparedStatement ps3 = conn.prepareStatement("select IDGrad from Grad where PostanskiBroj = ?");
            ){

            ps3.setString(1, string1);
            ResultSet rs = ps3.executeQuery();
            if(rs.next()){
                rs.close();
                return -1;
            }
            
            
            ps.setString(1, string);
            ps.setString(2, string1);
            ps.executeUpdate();
            
            if(ps.getGeneratedKeys().next()){
                idG = ps.getGeneratedKeys().getInt(1);
            }
            ps.getGeneratedKeys().close();
            
            rs.close();
        } catch(SQLException ex){}
        
        return idG;
    }

    @Override
    public int deleteCity(String... strings) {
        int num = 0;
        Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement ps = conn.prepareStatement("select IDGrad from Grad where Naziv = ?"); ){
            ResultSet rs = null;
            for(String naziv : strings){
                ps.setString(1, naziv);
                rs = ps.executeQuery();
                if(rs.next()){
                    if (deleteCity( rs.getInt(1) ))
                        num++;
                }

            }
            rs.close();
        } catch(SQLException ex){}
        return num;
    }

    @Override
    public boolean deleteCity(int i) {
        Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement ps = conn.prepareStatement("select * from Adresa where IDGrad = ?"); 
             PreparedStatement ps2 = conn.prepareStatement("select * from Grad where IDGrad = ?");
             PreparedStatement ps3 = conn.prepareStatement("delete from Grad where IDGrad = ?");){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                rs.close();
                return false;
            }
            
            ps2.setInt(1, i);
            rs = ps2.executeQuery();
            if(rs.next()){

            } else {
                rs.close();
                return false;
            }
                        
            ps3.setInt(1, i);
            ps3.executeUpdate();
            rs.close();
        } catch(SQLException ex){}
        
        return true;
    }

    @Override
    public List<Integer> getAllCities() {
       List<Integer> rezultat = new ArrayList<>();
       Connection conn = DB.getInstance().getConnection(); 
       try( Statement ps = conn.createStatement(); ){
           ResultSet rs = ps.executeQuery("select IDGrad from Grad");
           while(rs.next()){
               rezultat.add( rs.getInt(1) );
           }
           rs.close();
       } catch(SQLException ex){}
       
       return rezultat;
    }
    
}
