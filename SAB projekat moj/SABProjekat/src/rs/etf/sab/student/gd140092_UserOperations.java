/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.*;
import rs.etf.sab.operations.UserOperations;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Dusan
 */
public class gd140092_UserOperations implements UserOperations{

    @Override
    public boolean insertUser(String string, String string1, String string2, String string3, int i) {
        if (!Character.isUpperCase( string1.charAt(0) ))
            return false;
        if (!Character.isUpperCase( string2.charAt(0) ))
            return false;
        if (!( string3.matches(".*\\d+.*") && string3.matches(".*[A-Z]+.*") 
                && string3.matches(".*[a-z]+.*") && string3.length() >= 8 
                && string3.matches(".*[!@#$%&*()_+=|<>?{}\\\\[\\\\]~-]+.*") ))
            return false;
        
        Connection conn = DB.getInstance().getConnection(); 
        try( PreparedStatement ps = conn.prepareStatement("select * from Korisnik where KorisnickoIme = ?");
             PreparedStatement ps2 = conn.prepareStatement("insert into Korisnik values(?,?,?,?,?)");
             PreparedStatement ps3 = conn.prepareStatement("select * from Adresa where IDAdresa = ?");
                ){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
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
            
            ps2.setString(1, string1);
            ps2.setString(2, string2);
            ps2.setString(3, string3);
            ps2.setInt(4, i);
            ps2.setString(5, string);
            ps2.executeUpdate();
            
            rs.close();
        } catch(SQLException ex){}
        
        return true;
    }

    @Override
    public boolean declareAdmin(String string) {
        Connection conn = DB.getInstance().getConnection(); 
        int idK = 1;
        try( PreparedStatement ps = conn.prepareStatement(
                "select A.IDKorisnik from Administrator A, Korisnik K where K.IDKorisnik = A.IDKorisnik and K.KorisnickoIme = ?");
             PreparedStatement ps2 = conn.prepareStatement("select IDKorisnik from Korisnik where KorisnickoIme = ?");
             PreparedStatement ps3 = conn.prepareStatement("insert into Administrator values (?)");){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                rs.close();
                return false;
            }
            
            ps2.setString(1, string);
            rs = ps2.executeQuery();
            if(rs.next()){
                idK = rs.getInt(1);
            }
            else {
                rs.close();
                return false;
            }
            
            ps3.setInt(1, idK);
            ps3.executeUpdate();
            
            rs.close();
        }catch(SQLException ex){}
        
        return true;
    }

    @Override
    public int getSentPackages(String... strings) {
       int num = 0, idK = -1;
       Connection conn = DB.getInstance().getConnection(); 
       try( PreparedStatement ps = conn.prepareStatement("select IDKorisnik from Korisnik where KorisnickoIme = ?");
            PreparedStatement ps2 = conn.prepareStatement("select count(*) from Paket where IDKorisnik = ?");){
           ResultSet rs = null;
           for (String username: strings){
               ps.setString(1, username);
               rs = ps.executeQuery();
               if(rs.next()){
                   idK = rs.getInt(1);
               } else {
                   rs.close();
                   return -1;
               }
               
               ps2.setInt(1, idK);
               rs = ps2.executeQuery();
               if(rs.next()){
                   num += rs.getInt(1);
               }
               
               rs.close();
           }
       }catch(SQLException ex){}
       
       return num;
    }

    @Override
    public int deleteUsers(String... strings) {
        int num = 0, idK = -1;
        Connection conn = DB.getInstance().getConnection(); 
        try( PreparedStatement ps = conn.prepareStatement("select IDKorisnik from Korisnik where KorisnickoIme = ?"); 
             PreparedStatement ps2 = conn.prepareStatement("select IDPrijavaZK from PrijavaZaKurira where IDPrijavitelj = ?"); 
             PreparedStatement ps3 = conn.prepareStatement("select IDPaket from Paket where IDKorisnik = ?");
             PreparedStatement ps4 = conn.prepareStatement("select IDKorisnik from Administrator where IDKorisnik = ?");
             PreparedStatement ps5 = conn.prepareStatement("select IDKorisnik from Kurir where IDKorisnik = ?");
             PreparedStatement ps6 = conn.prepareStatement("delete from Korisnik where IDKorisnik = ?");){
            ResultSet rs = null;
            for(String username: strings){
                ps.setString(1, username);
                rs = ps.executeQuery();
                if(rs.next()){
                    idK = rs.getInt(1);
                } else {
                    rs.close();
                    return -1;
                }
                
                ps2.setInt(1, idK);
                rs = ps2.executeQuery();
                if(rs.next())
                    continue;
                
                ps3.setInt(1, idK);
                rs = ps3.executeQuery();
                if(rs.next())
                    continue;
                
                ps4.setInt(1, idK);
                rs = ps4.executeQuery();
                if(rs.next())
                    continue;
                
                ps5.setInt(1, idK);
                rs = ps5.executeQuery();
                if(rs.next())
                    continue;
                    
                ps6.setInt(1, idK);
                ps6.executeUpdate();
                num++;
                
            }
            
            rs.close();
        }catch(SQLException ex){}
        
        return num;
    }

    @Override
    public List<String> getAllUsers() {
       List<String> rezultat = new ArrayList<>();
       Connection conn = DB.getInstance().getConnection();
       try( Statement ps = conn.createStatement(); ){
           ResultSet rs = ps.executeQuery("select KorisnickoIme from Korisnik");
           while(rs.next()){
               rezultat.add( rs.getString(1) );
           }
           
           rs.close();
       }catch(SQLException ex){}
       
       return rezultat;
    }
    
}
