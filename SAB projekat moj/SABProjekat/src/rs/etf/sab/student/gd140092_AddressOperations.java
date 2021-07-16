/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.*;
import rs.etf.sab.operations.AddressOperations;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Dusan
 */
public class gd140092_AddressOperations implements AddressOperations{

    @Override
    public int insertAddress(String string, int i, int i1, int i2, int i3) {
        Connection conn = DB.getInstance().getConnection(); 
        int idA = -1;
        try( PreparedStatement ps = conn.prepareStatement("insert into Adresa values (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS); ){
            ps.setString(1, string);
            ps.setInt(2, i);
            ps.setInt(3, i1);
            ps.setInt(4, i2);
            ps.setInt(5, i3);
            ps.executeUpdate();
            if (ps.getGeneratedKeys().next())
                idA = ps.getGeneratedKeys().getInt(1);
            ps.getGeneratedKeys().close();
            
        } catch(SQLException ex){}
        
        return idA;
    }

    @Override
    public int deleteAddresses(String string, int i) {
       int num = 0;
       Connection conn = DB.getInstance().getConnection(); 
       try( PreparedStatement ps = conn.prepareStatement(
               "select IDAdresa from Adresa where Ulica = ? and Broj = ?"); ){
           ps.setString(1, string);
           ps.setInt(2, i);
           ResultSet rs = ps.executeQuery();
           while(rs.next()){
              if( deleteAdress( rs.getInt(1) ) )
                  num++;
           }
           
           rs.close();
       } catch(SQLException ex){}
       return num;
    }

    @Override
    public boolean deleteAdress(int i) {
        Connection conn = DB.getInstance().getConnection();
        int num = 0;
        try( PreparedStatement ps = conn.prepareStatement("select count(*) from Magacin where IDAdresa = ?");
             PreparedStatement ps2 = conn.prepareStatement("select count(*) from Korisnik where IDAdresa = ?");
             PreparedStatement ps3 = conn.prepareStatement(
                     "select count(*) from Paket where IDAdresaOd = ? or IDAdresaDo = ? or IDLokacija = ?");
             PreparedStatement ps4 = conn.prepareStatement("delete from Adresa where IDAdresa = ?");
             PreparedStatement ps5 = conn.prepareStatement("select * from Adresa where IDAdresa = ?");
                ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                num = rs.getInt(1);
                if (num > 0){
                    rs.close();
                    return false;
                }
            }
            ps2.setInt(1, i);
            rs = ps2.executeQuery();
            if (rs.next()){
                num = rs.getInt(1);
                if (num > 0){
                    rs.close();
                    return false;
                }
            }
            ps3.setInt(1, i);
            ps3.setInt(2, i);
            ps3.setInt(3, i);
            rs = ps3.executeQuery();
            if (rs.next()){
                num = rs.getInt(1);
                if (num > 0){
                    rs.close();
                    return false;
                }
            }
            
            ps5.setInt(1, i);
            rs = ps5.executeQuery();
            if(rs.next()){
                
            } else {
                rs.close();
                return false;
            }
            
            ps4.setInt(1, i);
            ps4.executeUpdate();
            
            rs.close();
        } catch( SQLException ex ){}
        return true;
    }

    @Override
    public int deleteAllAddressesFromCity(int i) {
       int num = 0;
       Connection conn = DB.getInstance().getConnection(); 
       try( PreparedStatement ps = conn.prepareStatement("select IDAdresa from Adresa where IDGrad = ?"); ){
           ps.setInt(1, i);
           ResultSet rs = ps.executeQuery();
           while(rs.next()){
              if( deleteAdress( rs.getInt(1) ) )
                  num++;
           }
           
           rs.close();
       } catch(SQLException ex){}
       return num;
    }

    @Override
    public List<Integer> getAllAddresses() {
        List<Integer> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection(); 
        try( Statement ps = conn.createStatement(); ){
            ResultSet rs = ps.executeQuery("select IDAdresa from Adresa");
            while(rs.next()){
                rezultat.add( rs.getInt(1) );
            }
            rs.close();
        } catch(SQLException ex){}
        return rezultat;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int i) {
        List<Integer> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection(); 
        try( PreparedStatement ps = conn.prepareStatement("select IDAdresa from Adresa where IDGrad = ?") ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                rezultat.add( rs.getInt(1) );
            }
            rs.close();
        } catch(SQLException ex){}
        
        if(rezultat.isEmpty()) return null;
        return rezultat;
    }
    
}
