/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author Xtender
 */
public class PackageOp implements PackageOperations {

    public Connection connection = DB.getInstance().getConnection();

    @Override
    public int insertPackage(int idAFrom, int idATo, String userName, int packageType, BigDecimal weight) {
        int idK = 0;
        if (userName == null) {
            return -1;
        } else {
            UserOp userOp = new UserOp();
            idK = userOp.getUserIdFromUserName(userName);
            if (idK == -1) {
                return -1;
            }
        }
        if (weight == null) {
            weight = BigDecimal.TEN;
        }

        String sql = "insert into paketi (idAPreuzimanja, idADostavljanja, TipPaketa, tezina, Status, VremeKreiranja, IdK) values (?, ?, ? ,? ,? ,? ,?)";
        try (
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
            ps.setInt(1, idAFrom);
            ps.setInt(2, idATo);
            ps.setInt(3, packageType);
            ps.setBigDecimal(4, weight);
            ps.setInt(5, 0);
            ps.setTimestamp(6, timestamp);
            ps.setInt(7, idK);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

    }

    @Override
    public boolean acceptAnOffer(int idP) {
        String sql = "select status from paketi where idP = ?";
        String sql1 = "update paketi set status = ?, vremePrihvatanja = ? where idP = ?";
        Calendar cal = Calendar.getInstance();
        java.sql.Timestamp timestamp = new Timestamp(cal.getTimeInMillis());

        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);) {

            ps.setInt(1, idP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int status = rs.getInt(1);
                if (status == 0) {
                    ps1.setInt(1, 1);// status 1 = prihvacena
                    ps1.setTimestamp(2, timestamp);
                    ps1.setInt(3, idP);
                    int numberOfRows = ps1.executeUpdate();
                    return numberOfRows > 0;
                } else {
                    System.out.println("Accept - status nije 0");
                    return false;
                }

            } else {
                System.out.println("Accept - nema paketa sa zadatim id-jem");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    @Override
    public boolean rejectAnOffer(int idP) {
        String sql = "select status from paketi where idP = ?";
        String sql1 = "update paketi set status = ? where idP = ?";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);) {

            ps.setInt(1, idP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int status = rs.getInt(1);
                if (status == 0) {
                    ps1.setInt(1, 4);// status 4 = odbijena
                    ps1.setInt(2, idP);
                    int numberOfRows = ps1.executeUpdate();
                    return numberOfRows > 0;
                } else {
                    System.out.println("Accept - status nije 0");
                    return false;
                }

            } else {
                System.out.println("Accept - nema paketa sa zadatim id-jem");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public List<Integer> getAllPackages() {
        String sql = "select idP from paketi";
        Statement stmt;
        List returnVal = new ArrayList<>();
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

                returnVal.add(rs.getInt(1));

            }

            stmt.close();
            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return returnVal;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int type) {
        String sql = "select idP from paketi where tipPaketa = ?";
        List returnVal = new ArrayList<>();
        if (type < 0 || type > 3) {
            return returnVal;
        }
        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, type);
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {

                returnVal.add(rs.getInt(1));

            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return returnVal;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        String sql = "select idP from paketi where status in (1, 2)";
        List returnVal = new ArrayList<>();
        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {

                returnVal.add(rs.getInt(1));

            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return returnVal;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int idG) {
        String sql = "select idP from paketi p, adrese a where status in (1, 2) and a.IdA = p.idAPreuzimanja and a.idG = ?"; // prihvacena i dostavlja se
        List returnVal = new ArrayList<>();
        if (idG <= 0) {
            return returnVal;
        }
        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idG);
            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {

                returnVal.add(rs.getInt(1));

            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return returnVal;
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int idG) {
        String sql = "select idP from paketi p, adrese a where (status = 1 and a.IdA = p.idAPreuzimanja and a.idG = ?) "
                + "or (status = 3 and a.IdA = p.idADostavljanja and a.idG = ?)";
        String sql1 = "select idP from paketi where status = 2 and lokacija = ?";
        List returnVal = new ArrayList<>();
        if (idG <= 0) {
            return returnVal;
        }
        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);) {
            ps.setInt(1, idG);
            ps.setInt(2, idG);

            ResultSet rs = ps.executeQuery(sql);
            while (rs.next()) {

                returnVal.add(rs.getInt(1));

            }

            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {

                returnVal.add(rs1.getInt(1));

            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return returnVal;
    }

    @Override
    public boolean deletePackage(int idP) {
        String sql = "select status from paketi where idP = ?";
        String sql1 = "delete from paketi where idP = ?";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);) {

            ps.setInt(1, idP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int status = rs.getInt(1);
                if (status == 0 || status == 4) {
                    ps1.setInt(1, idP);
                    int numberOfRows = ps1.executeUpdate();
                    return numberOfRows > 0;
                } else {
                    System.out.println("Delete - status nije 0 ili 4");
                    return false;
                }

            } else {
                System.out.println("Delete - nema paketa sa zadatim id-jem");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean changeWeight(int idP, BigDecimal newWeight) {
        if(newWeight == null || newWeight.longValue() <= 0 ){
            System.out.println("NEW WEIGHT - nova tezina manja od 0 ili null");
            return false;
        }
        int status = getDeliveryStatus(idP);
        
        if(status != 0){
            System.out.println("NEW WEIGHT - status nije 0");
            return false;
        }
        
        String sql = "update paketi set tezina = ? where idP = ?";
        
        try (
            PreparedStatement ps = connection.prepareStatement(sql);){
            
            ps.setBigDecimal(1, newWeight);
            ps.setInt(2, idP);
            int numberOfRows = ps.executeUpdate();
            return numberOfRows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean changeType(int idP, int type) {
        if(type > 3 || type < 0 ){
            System.out.println("NEW TYPE - novi tip nije dobar");
            return false;
        }
        int status = getDeliveryStatus(idP);
        
        if(status != 0){
            System.out.println("NEW TYPE - status nije 0");
            return false;
        }
        
        String sql = "update paketi set tipPaketa = ? where idP = ?";
        
        try (
            PreparedStatement ps = connection.prepareStatement(sql);){
            
            ps.setInt(1, type);
            ps.setInt(2, idP);
            int numberOfRows = ps.executeUpdate();
            return numberOfRows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public int getDeliveryStatus(int idP) {
        String sql = "select status from paketi where idP = ?";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getInt(1);
            }
            else {
                System.out.println("Status - nema paketa sa zadatim id-jem");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public BigDecimal getPriceOfDelivery(int idP) {
        String sql = "select cena from paketi where idP = ?";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getBigDecimal(1);
            }
            else {
                System.out.println("Status - nema paketa sa zadatim id-jem");
                return null;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public int getCurrentLocationOfPackage(int idP) {
        int status = getDeliveryStatus(idP);
        if(status == 0 || status == 2 || status == 4 ){
            System.out.println("LOKACIJA - status je ili 0 ili 2 ili 4");
            return -1;
        }
        if(status == -1){
            System.out.println("LOKACIJA - nema paketa sa zadatim id-jem");
            return -1;
        }
        
        String sql = "select lokacija from paketi where idP = ?";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getInt(1);
            }
            else {
                System.out.println("LOKACIJA - nema paketa sa zadatim id-jem");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
    }

    @Override
    public Date getAcceptanceTime(int idP) {
        int status = getDeliveryStatus(idP);
        if(status == -1 ){
            System.out.println("ACC TIME - nema paketa sa zadatim id-jel");
            return null;
        }
        
        if(status ==0 || status == 4){
            System.out.println("ACC TIME - status paketa 0 ili 4");
            return null;
        }
        
        String sql = "select vremePrihvatanja from paketi where idP = ?";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getDate(1);
            }
            else {
                System.out.println("ACC TIME - nema paketa sa zadatim id-jem");
                return null;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PackageOp.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }

    public static void main(String[] args) {
        CityOp c = new CityOp();
        AddressOp a = new AddressOp();
        PackageOp p = new PackageOp();
        UserOp u = new UserOp();

        int idC1 = c.insertCity("Beograd", "11000");
        int idC2 = c.insertCity("Novi Sad", "22000");
        int idA1 = a.insertAddress("Beogradska", 11, idC1, 2, 2);
        int idA2 = a.insertAddress("Novosadska", 22, idC2, 4, 4);
        int idA3 = a.insertAddress("Boegradska Druga", 15, idC1, 3, 3);

        boolean isSuccessful = u.insertUser("Xtender", "Milan", "Boskovic", "password", idA3);

        p.insertPackage(idA1, idA2, "Xtender", 1, BigDecimal.ONE);

    }
}
