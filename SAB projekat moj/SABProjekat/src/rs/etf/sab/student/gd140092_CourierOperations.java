/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.*;
import rs.etf.sab.operations.CourierOperations;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Dusan
 */
public class gd140092_CourierOperations implements CourierOperations{

    @Override
    public boolean insertCourier(String string, String string1) {
        int idK = 0;
        Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement ps = conn.prepareStatement(
                "select IDKorisnik from Korisnik where KorisnickoIme = ?");
             PreparedStatement ps2 = conn.prepareStatement(
                "select IDKorisnik from Kurir where IDKorisnik = ?");
            PreparedStatement ps3 = conn.prepareStatement(
                "insert into Kurir values (?,?,0,0.0)");
             ){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                idK = rs.getInt(1);
                
                ps2.setInt(1, idK);
                rs = ps2.executeQuery();
                if(rs.next()){
                    rs.close();
                    return false;
                }
                
                ps3.setInt(1, idK);
                ps3.setString(2, string1);
                ps3.executeUpdate();
                
            }else {
                rs.close();
                return false;
            }
            
            rs.close();
        } catch(SQLException ex){}
        
        return true;
    }

    @Override
    public boolean deleteCourier(String string) {
        int idK = 0;
        Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement ps = conn.prepareStatement("select IDKorisnik from Korisnik where KorisnickoIme = ?");
             PreparedStatement ps2 = conn.prepareStatement("select IDVoznja from Voznja where IDKorisnik = ?"); 
             PreparedStatement ps3 = conn.prepareStatement("delete from Kurir where IDKorisnik = ?"); ){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                idK = rs.getInt(1);
            } else {
                rs.close();
                return false;
            }
            
            ps2.setInt(1, idK);
            rs = ps2.executeQuery();
            if(rs.next()){
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
    public List<String> getCouriersWithStatus(int i) {
       List<String> rezultat = new ArrayList<>();
       Connection conn = DB.getInstance().getConnection();
       try( PreparedStatement ps = conn.prepareStatement(
        "select K.KorisnickoIme from Korisnik K join Kurir R on K.IDKorisnik = R.IDKorisnik join Voznja V " + 
                " on V.IDKorisnik = R.IDKorisnik where V.StatusVoznje = 1");
            PreparedStatement ps2 = conn.prepareStatement(
        "select K1.KorisnickoIme from Korisnik K1, Kurir R1 where K1.IDKorisnik = R1.IDKorisnik and R1.IDKorisnik not in (select V.IDKorisnik from Korisnik K join Kurir R on K.IDKorisnik = R.IDKorisnik join Voznja V " + 
                " on V.IDKorisnik = R.IDKorisnik where V.StatusVoznje = 1)");){
        ResultSet rs = null;
        if(i == 1){
            rs = ps.executeQuery();
            while(rs.next()){
                rezultat.add( rs.getString(1) );
            }
        } else {
            rs = ps2.executeQuery();
            while(rs.next()){
               rezultat.add( rs.getString(1) );
            }
        }
           
           rs.close();
       }catch(SQLException ex){}
       
       return rezultat;
    }

    @Override
    public List<String> getAllCouriers() {
       List<String> rezultat = new ArrayList<>();
       Connection conn = DB.getInstance().getConnection();
       try( Statement ps = conn.createStatement(); ){
           ResultSet rs = ps.executeQuery(
            "select K.KorisnickoIme from Korisnik K, Kurir R where K.IDKorisnik = R.IDKorisnik order by R.OstvarenProfit desc");
           while(rs.next()){
               rezultat.add( rs.getString(1) );
           }
           
           rs.close();
       } catch(SQLException ex){}
       
       return rezultat;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int i) {
        BigDecimal rezultat = BigDecimal.valueOf(0);
        Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement ps = conn.prepareStatement(
                "select avg(OstvarenProfit) from Kurir where BrojIsporucenihPaketa = ?");
             Statement ps2 = conn.createStatement(); ){
            ResultSet rs = null;
            if (i == -1){
                rs = ps2.executeQuery("select avg(OstvarenProfit) from Kurir");
            }else {
                ps.setInt(1, i);
                rs = ps.executeQuery();
            }
            
            if(rs.next())
                rezultat = rs.getBigDecimal(1);
                
            
            rs.close();
        }catch(SQLException ex){ Logger.getLogger(gd140092_PackageOperations.class.getName()).log(Level.SEVERE, null, ex); }
        
        return rezultat;
    }
    
}
