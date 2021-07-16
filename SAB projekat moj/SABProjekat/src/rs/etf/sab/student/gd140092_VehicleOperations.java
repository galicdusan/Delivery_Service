/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.*;
import rs.etf.sab.operations.VehicleOperations;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Dusan
 */
public class gd140092_VehicleOperations implements VehicleOperations{

    @Override
    public boolean insertVehicle(String string, int i, BigDecimal bd, BigDecimal bd1) {
        Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement ps = conn.prepareStatement("select IDVozilo from Vozilo where RegBroj = ?");
             PreparedStatement ps2 = conn.prepareStatement("insert into Vozilo values(?,?,?,?)");){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                rs.close();
                return false;
            }
            
            ps2.setString(1, string);
            ps2.setInt(2, i);
            ps2.setBigDecimal(3, bd);
            ps2.setBigDecimal(4, bd1);
            ps2.executeUpdate();
         
            rs.close();
        }catch(SQLException ex){}
        
        return true;
    }

    @Override
    public int deleteVehicles(String... strings) {
       int num = 0;
       Connection conn = DB.getInstance().getConnection();
       try( PreparedStatement ps = conn.prepareStatement("select * from Vozilo where RegBroj = ?",
               ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE); ){
           ResultSet rs = null;
           for(String regBroj: strings){
               ps.setString(1, regBroj);
               rs = ps.executeQuery();
               if(rs.next()){
                   rs.deleteRow();
                   num++;
               }
           }
           
           rs.close();
       } catch(SQLException ex){}
       
       return num;
    }

    @Override
    public List<String> getAllVehichles() {
        List<String> rezultat = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try( Statement ps = conn.createStatement(); ){
            ResultSet rs = ps.executeQuery("select RegBroj from Vozilo");
            while(rs.next()){
                rezultat.add( rs.getString(1) );
            }
            rs.close();
        }catch(SQLException ex){}
        
        return rezultat;
    }

    @Override
    public boolean changeFuelType(String string, int i) {
       Connection conn = DB.getInstance().getConnection();
       try( PreparedStatement ps = conn.prepareStatement(
               "select P.IDVozilo from Parkiran P, Vozilo V where V.RegBroj = ? and V.IDVozilo = P.IDVozilo");
            PreparedStatement ps2 = conn.prepareStatement("update Vozilo set TipGoriva = ? where RegBroj = ?")){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                ps2.setInt(1, i);
                ps2.setString(2, string);
                ps2.executeUpdate();
                
            } else {
                rs.close();
                return false;
            }
            
            rs.close();
       }catch(SQLException ex){}
       
       return true;
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
       Connection conn = DB.getInstance().getConnection();
       try( PreparedStatement ps = conn.prepareStatement(
               "select P.IDVozilo from Parkiran P, Vozilo V where V.RegBroj = ? and V.IDVozilo = P.IDVozilo");
            PreparedStatement ps2 = conn.prepareStatement("update Vozilo set Potrosnja = ? where RegBroj = ?")){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                ps2.setBigDecimal(1, bd);
                ps2.setString(2, string);
                ps2.executeUpdate();
                
            } else {
                rs.close();
                return false;
            }
            
            rs.close();
       }catch(SQLException ex){}
       
       return true;
    }

    @Override
    public boolean changeCapacity(String string, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();
       try( PreparedStatement ps = conn.prepareStatement(
               "select P.IDVozilo from Parkiran P, Vozilo V where V.RegBroj = ? and V.IDVozilo = P.IDVozilo");
            PreparedStatement ps2 = conn.prepareStatement("update Vozilo set Nosivost = ? where RegBroj = ?")){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                ps2.setBigDecimal(1, bd);
                ps2.setString(2, string);
                ps2.executeUpdate();
                
            } else {
                rs.close();
                return false;
            }
            
            rs.close();
       }catch(SQLException ex){}
       
       return true;
    }

    @Override
    public boolean parkVehicle(String string, int i) {
       int idV = 0;
       Connection conn = DB.getInstance().getConnection();
       try( PreparedStatement ps = conn.prepareStatement("select IDVozilo from Vozilo where RegBroj = ?"); 
            PreparedStatement ps2 = conn.prepareStatement(
                    "select IDVoznja from Voznja where IDVozilo = ? and StatusVoznje = 1"); 
            PreparedStatement ps3 = conn.prepareStatement("insert into Parkiran values (?,?)");
            PreparedStatement ps4 = conn.prepareStatement("delete from Parkiran where IDVozilo = ?");
            PreparedStatement ps5 = conn.prepareStatement("select * from Magacin where IDMagacin = ?");
               ){
           ps.setString(1, string);
           ResultSet rs = ps.executeQuery();
           if(rs.next()){
               idV = rs.getInt(1);
               
               ps2.setInt(1, idV);
               rs = ps2.executeQuery();
               if(rs.next()){
                   rs.close();
                   return false;
               }
               
               ps5.setInt(1, i);
               rs = ps5.executeQuery();
               if(rs.next()){
                   
               } else {
                   rs.close();
                   return false;
               }
               
               ps4.setInt(1, idV);
               ps4.executeUpdate();
               
               ps3.setInt(1, idV);
               ps3.setInt(2, i);
               ps3.executeUpdate();
               
           }else {
               rs.close();
               return false;
           }
           
           rs.close();
       }catch(SQLException ex){}
       
       return true;
    }
    
}
