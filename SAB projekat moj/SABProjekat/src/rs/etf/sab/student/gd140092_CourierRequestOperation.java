/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import rs.etf.sab.operations.CourierRequestOperation;
import java.sql.*;

/**
 *
 * @author Dusan
 */
public class gd140092_CourierRequestOperation implements CourierRequestOperation {

    @Override
    public boolean insertCourierRequest(String string, String string1) {
        Connection conn = DB.getInstance().getConnection();
        int idK = 1;
        try (PreparedStatement ps = conn.prepareStatement("select IDKorisnik from Korisnik where KorisnickoIme = ?");) {
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idK = rs.getInt(1);
            } else {
                rs.close();
                return false; 
            }
            rs.close();
        } catch (SQLException e) {}
        
        try( PreparedStatement ps = conn.prepareStatement("select * from Kurir where IDKorisnik = ?"); ){
            ps.setInt(1, idK);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                rs.close();
                return false;
            }
            rs.close();
        } catch (SQLException e) {}
        
        try( PreparedStatement ps = conn.prepareStatement("select * from PrijavaZaKurira where IDPrijavitelj = ?"); ){
            ps.setInt(1, idK);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                rs.close();
                return false;
            }
            rs.close();
        } catch (SQLException e) {}
        
        try( PreparedStatement ps = conn.prepareStatement("select * from PrijavaZaKurira where BrojVozackeDozvole = ?"); ){
            ps.setString(1, string1);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                rs.close();
                return false;
            }
            rs.close();
        } catch (SQLException e) {}
        
        try( PreparedStatement ps = conn.prepareStatement("insert into PrijavaZaKurira values (?, ?)"); ){
            ps.setString(1, string1);
            ps.setInt(2, idK);
            ps.executeUpdate();
        } catch (SQLException e) {}
        return true;
    }

    @Override
    public boolean deleteCourierRequest(String string) {
        Connection conn = DB.getInstance().getConnection();
        int idK = 1;
        try (PreparedStatement ps = conn.prepareStatement("select IDKorisnik from Korisnik where KorisnickoIme = ?");) {
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idK = rs.getInt(1);
            } else {
                rs.close();
                return false; 
            }
            rs.close();
        } catch (SQLException e) {}
        
        try (PreparedStatement ps = conn.prepareStatement(
                "select * from PrijavaZaKurira where IDPrijavitelj = ?", 
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {
            ps.setInt(1, idK);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.deleteRow();
            } else {
                rs.close();
                return false; 
            }
            rs.close();
        } catch (SQLException e) {}
              
        return true;
    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String string, String string1) {
        Connection conn = DB.getInstance().getConnection();
        int idK = 1;
        try (PreparedStatement ps = conn.prepareStatement("select IDKorisnik from Korisnik where KorisnickoIme = ?");) {
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idK = rs.getInt(1);
            } else {
                rs.close();
                return false; 
            }
            rs.close();
        } catch (SQLException e) {}
        
        try (PreparedStatement ps = conn.prepareStatement(
                "select * from PrijavaZaKurira where IDPrijavitelj = ?", 
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {
            ps.setInt(1, idK);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.updateString(2, string1);
                rs.updateRow();
            } else {
                rs.close();
                return false; 
            }
            rs.close();
        } catch (SQLException e) {}
        
        return true;
    }

    @Override
    public List<String> getAllCourierRequests() {
        List<String> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try (Statement ps = conn.createStatement();
             PreparedStatement ps2 = conn.prepareStatement("select KorisnickoIme from Korisnik where IDKorisnik = ?");) {
            ResultSet rs = ps.executeQuery("select IDPrijavitelj from PrijavaZaKurira");
            while (rs.next()) {
                int idK = rs.getInt(1);
                ps2.setInt(1, idK);
                ResultSet rs2 = ps2.executeQuery();
                if(rs2.next())
                    rezultat.add( rs2.getString(1) );
                rs2.close();
            } 
            
            rs.close();
        } catch (SQLException e) {}
        
        return rezultat;
    }

    @Override
    public boolean grantRequest(String string) {
        Connection conn = DB.getInstance().getConnection();
        int idK = 1;
        try (PreparedStatement ps = conn.prepareStatement("select IDKorisnik from Korisnik where KorisnickoIme = ?");) {
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idK = rs.getInt(1);
            } else {
                rs.close();
                return false; 
            }
            rs.close();
        } catch (SQLException e) {}
        
        String brVozacke = "";
        try (PreparedStatement ps = conn.prepareStatement(
                "select * from PrijavaZaKurira where IDPrijavitelj = ?",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
             PreparedStatement ps2 = conn.prepareStatement(
                "insert into Kurir values (?, ?, ?, ?)");) {
            ps.setInt(1, idK);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                brVozacke = rs.getString(2);
                ps2.setInt(1, idK);
                ps2.setString(2, brVozacke);
                ps2.setInt(3, 0);
                ps2.setBigDecimal(4, BigDecimal.valueOf(0));
                ps2.executeUpdate();
                
                rs.deleteRow();
            } else {
                rs.close();
                return false;
            }
            rs.close();
        } catch (SQLException e) {}
        
        return true;
    }

}
