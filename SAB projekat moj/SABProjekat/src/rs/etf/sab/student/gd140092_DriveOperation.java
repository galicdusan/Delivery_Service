/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.*;
import rs.etf.sab.operations.DriveOperation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Dusan
 */
public class gd140092_DriveOperation implements DriveOperation{
    
    class PaketData{
        public int idP;
        public int Xkoord;
        public int Ykoord;
        public BigDecimal tezina;
        
        public PaketData(int a, int b, int c, BigDecimal d){
            this.idP = a;
            this.Xkoord = b;
            this.Ykoord = c;
            this.tezina = d;
        }
    };
    
    private double distance(int x1, int y1, int x2, int y2){
        return Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
    }
    
    @Override
    public boolean planingDrive(String string) {
        List<PaketData> listaPaketa = new ArrayList<>();
        List<PaketData> listaIsporuka = new ArrayList<>();
        int idK = 0, idV = 0, idM = 0, idVoznja = 0, idMAdr = 0, idG = 0, idP = 0, idL = 0;
        int currX =0, currY=0, redniBroj = 1, nextGr = 0, delimiter = -1, krajX = 0, krajY = 0, validIDP = 0;
        BigDecimal teret = BigDecimal.valueOf(0), tezina, nosivost = BigDecimal.valueOf(0);
        Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement ps = conn.prepareStatement("select IDKorisnik from Korisnik where KorisnickoIme = ?");
             CallableStatement ps2 = conn.prepareCall("{ call spGetAvailableCar(?,?,?) }");
             PreparedStatement ps8 = conn.prepareStatement("select Nosivost from Vozilo where IDVozilo = ?");
             PreparedStatement ps3 = conn.prepareStatement("delete from Parkiran where IDVozilo = ? and IDMagacin = ?");
             PreparedStatement ps5 = conn.prepareStatement("select IDAdresa from Magacin where IDMagacin = ?");
             PreparedStatement ps4 = conn.prepareStatement(
                     "insert into Voznja(IDVozilo,IDKorisnik,StatusVoznje,OstvarenProfit) values (?,?,1,0.0)",
                     Statement.RETURN_GENERATED_KEYS);
             PreparedStatement ps6 = conn.prepareStatement("select IDGrad from Adresa where IDAdresa = ?");
             PreparedStatement ps7 = conn.prepareStatement(
            "select P.IDPaket, P.Tezina, P.IDLokacija, P.IDAdresaDo from Paket P, Adresa A where "+
            " P.StatusIsporuke = 1 and P.IDLokacija = A.IDAdresa and A.IDGrad = ? "+
            " order by VremePrihvatanja ASC");
            PreparedStatement ps9 = conn.prepareStatement("select Xkoord, Ykoord from Adresa where IDAdresa = ?");
            PreparedStatement ps10 = conn.prepareStatement(
            "select IDPaket, Tezina from Paket where IDLokacija = ? and StatusIsporuke = 2 order by VremePrihvatanja ASC");
            PreparedStatement ps11 = conn.prepareStatement(
            "select A.Xkoord, A.Ykoord from Paket P, Adresa A where P.IDPaket = ? and P.IDAdresaDo = A.IDAdresa"); 
            PreparedStatement ps12 = conn.prepareStatement(
                    "insert into PlanRute(IDVoznja,RedniBroj,IDPaket,Tip,Xkoord,Ykoord) values (?,?,?,?,?,?)");
            PreparedStatement ps13 = conn.prepareStatement(
                    "select A.IDGrad from Paket P, Adresa A where P.IDPaket = ? and P.IDAdresaDo = A.IDAdresa");
            PreparedStatement ps14 = conn.prepareStatement("update Voznja set Xkoord = ?, Ykoord = ? where IDVoznja = ?");
            PreparedStatement ps15 = conn.prepareStatement(
            "select P.IDPaket, P.Tezina, P.IDLokacija from Paket P, Adresa A where P.StatusIsporuke = 2 and P.IDLokacija = A.IDAdresa "+
            " and A.IDGrad = ? order by P.VremePrihvatanja ASC");
             ){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = null;
            if(rs.next()){
                idK = rs.getInt(1);
            } else {
                rs.close();
                return false;
            }
            
            ps2.setInt(1, idK);
            ps2.registerOutParameter(2, java.sql.Types.INTEGER);
            ps2.registerOutParameter(3, java.sql.Types.INTEGER);
            ps2.execute();
            idV = ps2.getInt(2);
            idM = ps2.getInt(3);
            
//            System.out.println(idV+" "+idM);
            if(idV <= 0){
                rs.close();
                return false;
            }
            
            ps8.setInt(1, idV);
            rs = ps8.executeQuery();
            if(rs.next()){
                nosivost = rs.getBigDecimal(1);
            }
            
            ps3.setInt(1, idV);
            ps3.setInt(2, idM);
            ps3.executeUpdate();
            
            ps5.setInt(1, idM);
            rs = ps5.executeQuery();
            if(rs.next()){
                idMAdr = rs.getInt(1);
            }
            
            ps4.setInt(1, idV);
            ps4.setInt(2, idK);
            ps4.executeUpdate();
            if(ps4.getGeneratedKeys().next())
                idVoznja = ps4.getGeneratedKeys().getInt(1);
            ps4.getGeneratedKeys().close();
            
            ps6.setInt(1, idMAdr);
            rs = ps6.executeQuery();
            if(rs.next()){
                idG = rs.getInt(1);
            }
            
            ps7.setInt(1, idG);
            rs = ps7.executeQuery();
            while(rs.next()){
                idP = rs.getInt(1);
                tezina = rs.getBigDecimal(2);
                idL = rs.getInt(3);
                
                validIDP = idP;
                
                if( (teret.add(tezina)).compareTo(nosivost) > 0 )
                    break;
                teret = teret.add(tezina);
                
                ps9.setInt(1, idL);
                rs2 = ps9.executeQuery();
                if(rs2.next()){
                    listaPaketa.add( new PaketData(idP, rs2.getInt(1), rs2.getInt(2), tezina) );
                }
            }
            
            ps10.setInt(1, idMAdr);
            rs = ps10.executeQuery();
            while(rs.next()){
                idP = rs.getInt(1);
                tezina = rs.getBigDecimal(2);
                idL = idMAdr;
                
                validIDP = idP;
                
                if( (teret.add(tezina)).compareTo(nosivost) > 0 )
                    break;
                teret = teret.add(tezina);
                
                ps9.setInt(1, idL);
                rs2 = ps9.executeQuery();
                if(rs2.next()){
                    listaPaketa.add( new PaketData(idP, rs2.getInt(1), rs2.getInt(2), tezina) );
                }
            }
            
//            for(int i=0; i<listaPaketa.size(); i++)
//                System.out.println(listaPaketa.get(i).idP + " " + listaPaketa.get(i).Xkoord + " " + listaPaketa.get(i).Ykoord);
            if(listaPaketa.size() == 0){
                rs2.close();
                rs.close();
                return false;
            }
            

            ps9.setInt(1, idMAdr);
            rs = ps9.executeQuery();
            if(rs.next()){
                currX = rs.getInt(1);
                currY = rs.getInt(2);
                
                krajX = currX;
                krajY = currY;
            }
            
/*            for(int i=0; i<listaPaketa.size()-1; i++){
                for(int j=i+1; j<listaPaketa.size(); j++){
                    int x1 = listaPaketa.get(i).Xkoord;
                    int y1 = listaPaketa.get(i).Ykoord;
                    int x2 = listaPaketa.get(j).Xkoord;
                    int y2 = listaPaketa.get(j).Ykoord;
                    if(distance(currX,currY,x1,y1) > distance(currX,currY,x2,y2)){
                        Collections.swap( listaPaketa, i, j );
                    }
                }
                currX = listaPaketa.get(i).Xkoord;
                currY = listaPaketa.get(i).Ykoord;
            }
*/
            currX = listaPaketa.get( listaPaketa.size()-1 ).Xkoord;
            currY = listaPaketa.get( listaPaketa.size()-1 ).Ykoord;

            
//            for(int i=0; i<listaPaketa.size(); i++)
//                System.out.println(listaPaketa.get(i).idP + " " + listaPaketa.get(i).Xkoord + " " + listaPaketa.get(i).Ykoord);   
            
            for(int i=0; i<listaPaketa.size(); i++){
                ps11.setInt(1, listaPaketa.get(i).idP);
                rs = ps11.executeQuery();
                if(rs.next()){
                    listaIsporuka.add( new PaketData(listaPaketa.get(i).idP, rs.getInt(1), rs.getInt(2), listaPaketa.get(i).tezina) );
                }
            }
            
            for(int i=0; i<listaIsporuka.size()-1; i++){
                for(int j=i+1; j<listaIsporuka.size(); j++){
                    int x1 = listaIsporuka.get(i).Xkoord;
                    int y1 = listaIsporuka.get(i).Ykoord;
                    int x2 = listaIsporuka.get(j).Xkoord;
                    int y2 = listaIsporuka.get(j).Ykoord;
                    if(distance(currX,currY,x1,y1) > distance(currX,currY,x2,y2)){
                        Collections.swap( listaIsporuka, i, j );
                    }
                }
                currX = listaIsporuka.get(i).Xkoord;
                currY = listaIsporuka.get(i).Ykoord;
            }
            
//            for(int i=0; i<listaIsporuka.size(); i++)
//                System.out.println(listaIsporuka.get(i).idP + " " + listaIsporuka.get(i).Xkoord + " " + listaIsporuka.get(i).Ykoord); 
            
/*            ps12.setInt(1, idVoznja);
            ps12.setInt(2, redniBroj);
            ps12.setInt(3, validIDP);
            ps12.setInt(4, 0);
            ps12.setInt(5, krajX);
            ps12.setInt(6, krajY);
            redniBroj++;
            ps12.executeUpdate(); */
            ps14.setInt(1, krajX);
            ps14.setInt(2, krajY);
            ps14.setInt(3, idVoznja);
            ps14.executeUpdate();

            
            for(int i=0; i<listaPaketa.size(); i++){
                ps12.setInt(1, idVoznja);
                ps12.setInt(2, redniBroj);
                ps12.setInt(3, listaPaketa.get(i).idP);
                ps12.setInt(4, 1);
                ps12.setInt(5, listaPaketa.get(i).Xkoord);
                ps12.setInt(6, listaPaketa.get(i).Ykoord);
                redniBroj++;
                ps12.executeUpdate();                                
            }
            
            listaPaketa.clear();
            
            for(int i=0; i<listaIsporuka.size(); i++){
                ps13.setInt(1, listaIsporuka.get(i).idP);
                rs = ps13.executeQuery();
                if(rs.next())
                    nextGr = rs.getInt(1);
                if(nextGr == idG){
                    ps12.setInt(1, idVoznja);
                    ps12.setInt(2, redniBroj);
                    ps12.setInt(3, listaIsporuka.get(i).idP);
                    ps12.setInt(4, 3);
                    ps12.setInt(5, listaIsporuka.get(i).Xkoord);
                    ps12.setInt(6, listaIsporuka.get(i).Ykoord);
                    redniBroj++;
                    ps12.executeUpdate();
                    teret = teret.subtract(listaIsporuka.get(i).tezina);
                }
                else {
                    idG = nextGr;
                    delimiter = i;
                    break;
                }
            }
            for(int i=0; i<delimiter; i++)
                listaIsporuka.remove(i);
            
            while(listaIsporuka.size() > 0){
                ps13.setInt(1, listaIsporuka.get(0).idP);
                rs = ps13.executeQuery();
                if(rs.next()){
                    nextGr = rs.getInt(1);
                }
                if(nextGr == idG){
                    ps12.setInt(1, idVoznja);
                    ps12.setInt(2, redniBroj);
                    ps12.setInt(3, listaIsporuka.get(0).idP);
                    ps12.setInt(4, 3);
                    ps12.setInt(5, listaIsporuka.get(0).Xkoord);
                    ps12.setInt(6, listaIsporuka.get(0).Ykoord);
                    redniBroj++;
                    ps12.executeUpdate();
                    teret = teret.subtract(listaIsporuka.get(0).tezina);
                    
                    currX = listaIsporuka.get(0).Xkoord;
                    currY = listaIsporuka.get(0).Ykoord;
                    listaIsporuka.remove(0);
                }
                if(nextGr != idG || listaIsporuka.size() == 0) {
                    ps7.setInt(1, idG);
                    rs = ps7.executeQuery();
                    while(rs.next()){
                        idP = rs.getInt(1);
                        tezina = rs.getBigDecimal(2);
                        idL = rs.getInt(3);

                        if( (teret.add(tezina)).compareTo(nosivost) > 0 )
                            break;
                        teret = teret.add(tezina);

                        ps9.setInt(1, idL);
                        rs2 = ps9.executeQuery();
                        if(rs2.next()){
                            listaPaketa.add( new PaketData(idP, rs2.getInt(1), rs2.getInt(2), tezina) );
                        }
                    }
                    
                    ps15.setInt(1, idG);
                    rs = ps15.executeQuery();
                    while(rs.next()){
                        idP = rs.getInt(1);
                        tezina = rs.getBigDecimal(2);
                        idL = rs.getInt(3);
                        
                        if( (teret.add(tezina)).compareTo(nosivost) > 0 )
                            break;
                        teret = teret.add(tezina);
                        
                        ps9.setInt(1, idL);
                        rs2 = ps9.executeQuery();
                        if(rs2.next()){
                            listaPaketa.add( new PaketData(idP, rs2.getInt(1), rs2.getInt(2), tezina) );
                        }
                    }

/*                    for(int i=0; i<listaPaketa.size()-1; i++){
                        for(int j=i+1; j<listaPaketa.size(); j++){
                            int x1 = listaPaketa.get(i).Xkoord;
                            int y1 = listaPaketa.get(i).Ykoord;
                            int x2 = listaPaketa.get(j).Xkoord;
                            int y2 = listaPaketa.get(j).Ykoord;
                            if(distance(currX,currY,x1,y1) > distance(currX,currY,x2,y2)){
                                Collections.swap( listaPaketa, i, j );
                            }
                        }
                        currX = listaPaketa.get(i).Xkoord;
                        currY = listaPaketa.get(i).Ykoord;
                    }
*/                  

                    for(int i=0; i<listaPaketa.size(); i++){
                        ps12.setInt(1, idVoznja);
                        ps12.setInt(2, redniBroj);
                        ps12.setInt(3, listaPaketa.get(i).idP);
                        ps12.setInt(4, 2);
                        ps12.setInt(5, listaPaketa.get(i).Xkoord);
                        ps12.setInt(6, listaPaketa.get(i).Ykoord);
                        redniBroj++;
                        ps12.executeUpdate();                                
                    }

                    listaPaketa.clear();
                    
                    
                    idG = nextGr;
                }
            }
            
            ps12.setInt(1, idVoznja);
            ps12.setInt(2, redniBroj);
            ps12.setInt(3, validIDP);
            ps12.setInt(4, 4);
            ps12.setInt(5, krajX);
            ps12.setInt(6, krajY);
            redniBroj++;
            ps12.executeUpdate();
            
            
            
            rs2.close();
            rs.close();
        }catch(SQLException ex){}
        
        return true;
    }

    @Override
    public int nextStop(String string) {
        int idV = 0, rb = 0, idP = 0, tip = 0, xk = 0, yk = 0, tipGoriva = 0, currX = 0, currY = 0;
        int magAdr = 0, idKorisnik = 0, idMag = 0, idVozilo = 0, idDest = 0, nextX = 0, nextY = 0;
        double trosak = 0;
        BigDecimal potrosnja = BigDecimal.valueOf(0), cena = BigDecimal.valueOf(0), profit = BigDecimal.valueOf(0);
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement ps = conn.prepareStatement(
            "select V.IDVoznja, V.IDKorisnik, V.IDVozilo from Korisnik K, Voznja V where K.KorisnickoIme = ? and K.IDKorisnik = V.IDKorisnik and V.StatusVoznje = 1");
            PreparedStatement ps2 = conn.prepareStatement(
            "select top 1 RedniBroj, IDPaket, Tip, Xkoord, Ykoord from PlanRute where IDVoznja = ? order by RedniBroj ASC");
            PreparedStatement ps3 = conn.prepareStatement(
            "update Voznja set Xkoord = ?, Ykoord = ? where IDVoznja = ?");
            PreparedStatement ps4 = conn.prepareStatement("insert into Prenosi values (?,?)");
            PreparedStatement ps5 = conn.prepareStatement(
            "select V.TipGoriva, V.Potrosnja from Vozilo V, Voznja Z where Z.IDVoznja = ? and Z.IDVozilo = V.IDVozilo");
            PreparedStatement ps6 = conn.prepareStatement("select Xkoord, Ykoord from Voznja where IDVoznja = ?");
            PreparedStatement ps7 = conn.prepareStatement(
                    "update Voznja set OstvarenProfit = OstvarenProfit + ? where IDVoznja = ?");
            PreparedStatement ps8 = conn.prepareStatement(
            "select A.IDAdresa from PlanRute P, Adresa A where P.IDVoznja = ? "+
            " and P.Tip = 4 and P.Xkoord = A.Xkoord and P.Ykoord = A.Ykoord");
            PreparedStatement ps9 = conn.prepareStatement(
            "update Paket set StatusIsporuke = ?, IDLokacija = ? where IDPaket = ?");
            PreparedStatement ps10 = conn.prepareStatement(
            "update Paket set StatusIsporuke = ? where IDPaket = ?");
            PreparedStatement ps11 = conn.prepareStatement("delete from Prenosi where IDVoznja = ? and IDPaket = ?");
            PreparedStatement ps12 = conn.prepareStatement("delete from PlanRute where IDVoznja = ? and RedniBroj = ?");
            PreparedStatement ps13 = conn.prepareStatement(
                    "update Kurir set BrojIsporucenihPaketa = BrojIsporucenihPaketa + 1 where IDKorisnik = ?");
            PreparedStatement ps14 = conn.prepareStatement("select Cena from Paket where IDPaket = ?");
            PreparedStatement ps15 = conn.prepareStatement("delete from Prenosi where IDVoznja = ?");
            PreparedStatement ps16 = conn.prepareStatement("update Voznja set StatusVoznje = 0 where IDVoznja = ?");
            PreparedStatement ps17 = conn.prepareStatement("select OstvarenProfit from Voznja where IDVoznja = ?");
            PreparedStatement ps18 = conn.prepareStatement("update Kurir set OstvarenProfit = ? where IDKorisnik = ?");  
            PreparedStatement ps19 = conn.prepareStatement("select IDMagacin from Magacin where IDAdresa = ?");
            PreparedStatement ps20 = conn.prepareStatement("insert into Parkiran values (?,?)");
            PreparedStatement ps21 = conn.prepareStatement("select IDAdresaDo from Paket where IDPaket = ?"); 
            PreparedStatement ps22 = conn.prepareStatement(
                    "select Xkoord, Ykoord from PlanRute where IDVoznja = ? and RedniBroj = ?");
                ){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                idV = rs.getInt(1);
                idKorisnik = rs.getInt(2);
                idVozilo = rs.getInt(3);
            } else {
                rs.close();
                return -1;
            }
            
            ps2.setInt(1, idV);
            rs = ps2.executeQuery();
            if(rs.next()){
                rb = rs.getInt(1);
                idP = rs.getInt(2);
                tip = rs.getInt(3);
                xk = rs.getInt(4);
                yk = rs.getInt(5);
            }
            
            if(tip == 0){
                

            } else if (tip == 1){
                
                ps4.setInt(1, idV);
                ps4.setInt(2, idP);
                ps4.executeUpdate();
                
                ps5.setInt(1, idV);
                rs = ps5.executeQuery();
                if(rs.next()){
                    tipGoriva = rs.getInt(1);
                    potrosnja = rs.getBigDecimal(2);
                }
                if(tipGoriva == 0) tipGoriva = 15;
                else if(tipGoriva == 1)tipGoriva = 32;
                else tipGoriva = 36;
                    
                
                ps6.setInt(1, idV);
                rs = ps6.executeQuery();
                if(rs.next()){
                    currX = rs.getInt(1);
                    currY = rs.getInt(2);
                }
                
                trosak = (-1)* distance(currX, currY, xk, yk) * potrosnja.doubleValue() * tipGoriva;
//                System.out.println(trosak);
                ps7.setBigDecimal(1, BigDecimal.valueOf(trosak));
                ps7.setInt(2, idV);
                ps7.executeUpdate();
                                
                ps10.setInt(1, 2);
                ps10.setInt(2, idP);
                ps10.executeUpdate();
            } else if (tip == 2){
                
                ps4.setInt(1, idV);
                ps4.setInt(2, idP);
                ps4.executeUpdate();
                
                ps8.setInt(1, idV);
                rs = ps8.executeQuery();
                if(rs.next()){
                    magAdr = rs.getInt(1);
                }
                
 //               System.out.println(magAdr);
                
                ps9.setInt(1, 2);
                ps9.setInt(2, magAdr);
                ps9.setInt(3, idP);
                ps9.executeUpdate();
                
                ps5.setInt(1, idV);
                rs = ps5.executeQuery();
                if(rs.next()){
                    tipGoriva = rs.getInt(1);
                    potrosnja = rs.getBigDecimal(2);
                }
                if(tipGoriva == 0) tipGoriva = 15;
                else if(tipGoriva == 1)tipGoriva = 32;
                else tipGoriva = 36;
                    
                
                ps6.setInt(1, idV);
                rs = ps6.executeQuery();
                if(rs.next()){
                    currX = rs.getInt(1);
                    currY = rs.getInt(2);
                }
                
                trosak = (-1)* distance(currX, currY, xk, yk) * potrosnja.doubleValue() * tipGoriva;
                ps7.setBigDecimal(1, BigDecimal.valueOf(trosak));
                ps7.setInt(2, idV);
                ps7.executeUpdate();
                
            } else if (tip == 3){
                
                ps11.setInt(1, idV);
                ps11.setInt(2, idP);
                ps11.executeUpdate();
                
                ps21.setInt(1, idP);
                rs = ps21.executeQuery();
                if(rs.next()){
                    idDest = rs.getInt(1);
                }
                
                ps9.setInt(1, 3);
                ps9.setInt(2, idDest);
                ps9.setInt(3, idP);
                ps9.executeUpdate();
      /*          ps10.setInt(1, 3);
                ps10.setInt(2, idP);
                ps10.executeUpdate(); */
                
                ps5.setInt(1, idV);
                rs = ps5.executeQuery();
                if(rs.next()){
                    tipGoriva = rs.getInt(1);
                    potrosnja = rs.getBigDecimal(2);
                }
                if(tipGoriva == 0) tipGoriva = 15;
                else if(tipGoriva == 1)tipGoriva = 32;
                else tipGoriva = 36;
                    
                
                ps6.setInt(1, idV);
                rs = ps6.executeQuery();
                if(rs.next()){
                    currX = rs.getInt(1);
                    currY = rs.getInt(2);
                }
                
                trosak = (-1)* distance(currX, currY, xk, yk) * potrosnja.doubleValue() * tipGoriva;
                ps7.setBigDecimal(1, BigDecimal.valueOf(trosak));
                ps7.setInt(2, idV);
                ps7.executeUpdate();
                
                ps13.setInt(1, idKorisnik);
                ps13.executeUpdate();
                
                ps14.setInt(1, idP);
                rs = ps14.executeQuery();
                if(rs.next()){
                    cena = rs.getBigDecimal(1);
                }
                
                ps7.setBigDecimal(1, cena);
                ps7.setInt(2, idV);
                ps7.executeUpdate();
                
            } else if (tip == 4){
                
                ps15.setInt(1, idV);
                ps15.executeUpdate();
                
                ps5.setInt(1, idV);
                rs = ps5.executeQuery();
                if(rs.next()){
                    tipGoriva = rs.getInt(1);
                    potrosnja = rs.getBigDecimal(2);
                }
                if(tipGoriva == 0) tipGoriva = 15;
                else if(tipGoriva == 1)tipGoriva = 32;
                else tipGoriva = 36;
                    
                
                ps6.setInt(1, idV);
                rs = ps6.executeQuery();
                if(rs.next()){
                    currX = rs.getInt(1);
                    currY = rs.getInt(2);
                }
                
                trosak = (-1)* distance(currX, currY, xk, yk) * potrosnja.doubleValue() * tipGoriva;
                ps7.setBigDecimal(1, BigDecimal.valueOf(trosak));
                ps7.setInt(2, idV);
                ps7.executeUpdate();
                
                ps16.setInt(1, idV);
                ps16.executeUpdate();
                
                ps17.setInt(1, idV);
                rs = ps17.executeQuery();
                if(rs.next()){
                    profit = rs.getBigDecimal(1);
                }
                
                ps18.setBigDecimal(1, profit);
                ps18.setInt(2, idKorisnik);
                ps18.executeUpdate();
                
                ps8.setInt(1, idV);
                rs = ps8.executeQuery();
                if(rs.next()){
                    magAdr = rs.getInt(1);
                }
                
                ps19.setInt(1, magAdr);
                rs = ps19.executeQuery();
                if(rs.next()){
                    idMag = rs.getInt(1);
                }
                
                ps20.setInt(1, idVozilo);
                ps20.setInt(2, idMag);
                ps20.executeUpdate();
            }
            
            ps3.setInt(1, xk);
            ps3.setInt(2, yk);
            ps3.setInt(3, idV);
            ps3.executeUpdate();
            
            ps12.setInt(1, idV);
            ps12.setInt(2, rb);
            ps12.executeUpdate();
            
            ps22.setInt(1, idV);
            ps22.setInt(2, rb+1);
            rs = ps22.executeQuery();
            if(rs.next()){
                nextX = rs.getInt(1);
                nextY = rs.getInt(2);
                if(xk == nextX && yk == nextY)
                    return nextStop(string);
            }
            
            rs.close();
        }catch(SQLException ex){ Logger.getLogger(gd140092_PackageOperations.class.getName()).log(Level.SEVERE, null, ex); }
        
        if (tip == 3) return idP;
        if (tip == 4) return -1;
        return -2;
    }

    @Override
    public List<Integer> getPackagesInVehicle(String string) {
        List<Integer> rezultat = new ArrayList<>();
        int idV = 0;
        Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement ps = conn.prepareStatement(
        "select V.IDVoznja from Korisnik K, Voznja V where K.KorisnickoIme = ? "+
        " and K.IDKorisnik = V.IDKorisnik and V.StatusVoznje = 1"); 
             PreparedStatement ps2 = conn.prepareStatement("select IDPaket from Prenosi where IDVoznja = ?");
                ){
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                idV = rs.getInt(1);
            }
            
            ps2.setInt(1, idV);
            rs = ps2.executeQuery();
            while(rs.next()){
                rezultat.add( rs.getInt(1) );
            }
            
            rs.close();
        }catch(SQLException ex){}
        
        return rezultat;
    }
    
}
