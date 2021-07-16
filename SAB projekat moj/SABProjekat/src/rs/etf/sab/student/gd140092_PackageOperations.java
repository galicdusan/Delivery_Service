/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import rs.etf.sab.operations.PackageOperations;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Dusan
 */
public class gd140092_PackageOperations implements PackageOperations{

    public int insertPackage(int i, int i1, String string, int i2){
        return insertPackage(i,i1,string,i2, BigDecimal.valueOf(10));
    }
    
    @Override
    public int insertPackage(int i, int i1, String string, int i2, BigDecimal bd) {
        int idP = 0, idK = 0;
        Connection conn = DB.getInstance().getConnection();       
        try( PreparedStatement ps = conn.prepareStatement(
                "select IDKorisnik from Korisnik where KorisnickoIme = ?"); ){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idK = rs.getInt(1);
            } else {
                rs.close();
                return -1; 
            }
            rs.close();
        } catch(SQLException e){}
 
        try( PreparedStatement ps = conn.prepareStatement(
                "insert into Paket(IDAdresaOd, IDAdresaDo, IDKorisnik, TipPaketa, Tezina, IDLokacija, StatusIsporuke, VremeKreiranja) values(?,?,?,?,?,?,0,getdate())", Statement.RETURN_GENERATED_KEYS);){
            ps.setInt(1, i);
            ps.setInt(2, i1);
            ps.setInt(3, idK);
            ps.setInt(4, i2);
            ps.setBigDecimal(5, bd);
            ps.setInt(6, i);
            ps.executeUpdate();
            if (ps.getGeneratedKeys().next())
                idP = ps.getGeneratedKeys().getInt(1);
            
            ps.getGeneratedKeys().close();
        } catch(SQLException ex){ Logger.getLogger(gd140092_PackageOperations.class.getName()).log(Level.SEVERE, null, ex); }
        
        return idP;
    }
    
    @Override
    public boolean acceptAnOffer(int i) {
       Connection conn = DB.getInstance().getConnection(); 
                
        try( PreparedStatement ps = conn.prepareStatement(
                "select StatusIsporuke, Cena from Paket where IDPaket = ?", 
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE) ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                if (rs.getBigDecimal(2) != null && rs.getInt(1) == 0){
                    rs.updateInt(1, 1);
                    rs.updateRow();
                    
                    PreparedStatement ps2 = conn.prepareStatement(
                            "update Paket set VremePrihvatanja = getdate() where IDPaket = ?");
                    ps2.setInt(1, i);
                    ps2.executeUpdate();
                    ps2.close();
                }
                else{
                    rs.close();
                    return false;
                }
            }
            else {
                rs.close();
                return false;
            }
            rs.close();
        }catch(SQLException ex){}
        
        return true;
    }

    @Override
    public boolean rejectAnOffer(int i) {
        Connection conn = DB.getInstance().getConnection(); 
                
        try( PreparedStatement ps = conn.prepareStatement(
                "select StatusIsporuke, Cena from Paket where IDPaket = ?", 
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE) ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                if (rs.getBigDecimal(2) != null && rs.getInt(1) == 0){
                    rs.updateInt(1, 4);
                    rs.updateRow();
                }
                else{
                    rs.close();
                    return false;
                }
            }
            else {
                rs.close();
                return false;
            }
            rs.close();
        }catch(SQLException ex){}
        
        return true;
    }

    @Override
    public List<Integer> getAllPackages() {
        List<Integer> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection(); 
        try( Statement ps = conn.createStatement(); ){
            ResultSet rs = ps.executeQuery("select IDPaket from Paket");
            while(rs.next()){
                rezultat.add( rs.getInt(1) );
            }
            rs.close();
        } catch(SQLException ex){}
        
        return rezultat;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int i) {
        List<Integer> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection(); 
        try( PreparedStatement ps = conn.prepareStatement(
                "select IDPaket from Paket where TipPaketa = ?"); ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                rezultat.add( rs.getInt(1) );
            }
            rs.close();
        } catch(SQLException ex){}
        
        return rezultat;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
         List<Integer> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection(); 
        try( Statement ps = conn.createStatement(); ){
            ResultSet rs = ps.executeQuery("select IDPaket from Paket where StatusIsporuke in (1,2)");
            while(rs.next()){
                rezultat.add( rs.getInt(1) );
            }
            rs.close();
        } catch(SQLException ex){}
        
        return rezultat;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int i) {
        List<Integer> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection(); 
        try( PreparedStatement ps = conn.prepareStatement(
                "select P.IDPaket from Paket P, Adresa A where P.StatusIsporuke in (1,2) and P.IDAdresaOd = A.IDAdresa and A.IDGrad = ?"); ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                rezultat.add( rs.getInt(1) );
            }
            rs.close();
        } catch(SQLException ex){}
        
        return rezultat;
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int i) {
        List<Integer> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection(); 
        try( PreparedStatement ps = conn.prepareStatement(
                "select P.IDPaket from Paket P, Adresa A where P.IDLokacija = A.IDAdresa and A.IDGrad = ?");
             PreparedStatement ps2 = conn.prepareStatement("select * from Prenosi where IDPaket = ?");
                ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = null;
            while(rs.next()){
                
                ps2.setInt(1, rs.getInt(1));
                rs2 = ps2.executeQuery();
                if(rs2.next()){
                    
                } else {
                    rezultat.add( rs.getInt(1) );
                }
                                
            }
            rs2.close();
            rs.close();
        } catch(SQLException ex){}
        
        return rezultat;
    }

    @Override
    public boolean deletePackage(int i) {
       Connection conn = DB.getInstance().getConnection(); 
       int num = 0;
       try( PreparedStatement ps = conn.prepareStatement("delete from Prenosi where IDPaket = ?");
            PreparedStatement ps2 = conn.prepareStatement("delete from Paket where IDPaket = ? and StatusIsporuke in (0,4)");){
           ps.setInt(1, i);
           ps.executeUpdate();
           ps2.setInt(1, i);
           num = ps2.executeUpdate();
           if (num == 0){
               return false;
           }
       } catch (SQLException ex){}
       
       return true;
    }

    @Override
    public boolean changeWeight(int i, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection(); 
        try( PreparedStatement ps = conn.prepareStatement(
                "select StatusIsporuke, Tezina from Paket where IDPaket = ?",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE) ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                if (rs.getInt(1) == 0){
                    rs.updateBigDecimal(2, bd);
                    rs.updateRow();
                }
                else {
                    rs.close();
                    return false;
                }
            }
            else {
                rs.close();
                return false;
            }
            rs.close();
        } catch(SQLException ex){}
        
        return true;
    }

    @Override
    public boolean changeType(int i, int i1) {
        Connection conn = DB.getInstance().getConnection(); 
        try( PreparedStatement ps = conn.prepareStatement(
                "select StatusIsporuke, TipPaketa from Paket where IDPaket = ?",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE) ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                if (rs.getInt(1) == 0){
                    rs.updateInt(2, i1);
                    rs.updateRow();
                }
                else {
                    rs.close();
                    return false;
                }
            }
            else {
                rs.close();
                return false;
            }
            rs.close();
        } catch(SQLException ex){}
        
        return true;
    }

    @Override
    public int getDeliveryStatus(int i) {
        Connection conn = DB.getInstance().getConnection(); 
        int status = 0;
        try( PreparedStatement ps = conn.prepareStatement(
                "select StatusIsporuke from Paket where IDPaket = ?"); ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                status = rs.getInt(1);
            }
            else {
                rs.close();
                return -1;
            }
            rs.close();
        } catch(SQLException ex){ Logger.getLogger(gd140092_PackageOperations.class.getName()).log(Level.SEVERE, null, ex); }
        return status;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
        Connection conn = DB.getInstance().getConnection(); 
        
        try( PreparedStatement ps = conn.prepareStatement(
                "select Cena from Paket where IDPaket = ?"); ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                if (rs.getBigDecimal(1) != null){
                    BigDecimal cena = rs.getBigDecimal(1);
                    rs.close();
                    return cena;
                }
                else{
                    rs.close();
                    return BigDecimal.valueOf(-1);
                }
            }
            else {
                rs.close();
                return BigDecimal.valueOf(-1);
            }
            
        }catch(SQLException ex){}
        
        return BigDecimal.valueOf(-1);
    }

    @Override
    public int getCurrentLocationOfPackage(int i) {
        Connection conn = DB.getInstance().getConnection(); 
        int idL = 0;
        try( PreparedStatement ps = conn.prepareStatement(
                "select A.IDGrad from Paket P, Adresa A where P.IDPaket = ? and P.IDLokacija = A.IDAdresa");
             PreparedStatement ps2 = conn.prepareStatement("select * from Prenosi where IDPaket = ?");){
            ps2.setInt(1, i);
            ResultSet rs = ps2.executeQuery();
            if(rs.next()){
                rs.close();
                return -1;
            }
            
            ps.setInt(1, i);
            rs = ps.executeQuery();
            if(rs.next()){
                idL = rs.getInt(1);
            }
            else {
                rs.close();
                return -1;
            }
            rs.close();
        } catch(SQLException ex){ Logger.getLogger(gd140092_PackageOperations.class.getName()).log(Level.SEVERE, null, ex); }
        return idL;
    }

    @Override
    public Date getAcceptanceTime(int i) {
        Connection conn = DB.getInstance().getConnection(); 
        Date rezultat = null;
        try( PreparedStatement ps = conn.prepareStatement("select VremePrihvatanja from Paket where IDPaket = ?"); ){
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                if(rs.getTimestamp(1) != null)
                    rezultat = java.sql.Date.valueOf( rs.getTimestamp(1).toLocalDateTime().toLocalDate() );
            }
            else {
                rs.close();
                return rezultat;
            }
            rs.close();
        } catch(SQLException ex){}
        
        return rezultat;
    }
    
}
