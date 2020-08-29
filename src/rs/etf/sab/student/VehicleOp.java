/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author Xtender
 */
public class VehicleOp implements VehicleOperations {

    public Connection connection = DB.getInstance().getConnection();

    private int getIdVFromLicence(String licence) {
        String sql = "select idV from vozila where RegistarskiBroj = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {

            ps.setString(1, licence);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            } else {
                System.out.println("nema vozila sa zadatim tablicama");
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(VehicleOp.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public boolean insertVehicle(String tablice, int tipGoriva, BigDecimal potrosnja, BigDecimal kapacitet) {
        String sql = "Insert into vozila (RegistarskiBroj, TipGoriva, Potrosnja, Nosivost, Driving, idK) values (?,?,?,?, 0, null)";
        if (kapacitet.longValue() <= 0 || potrosnja.longValue() <= 0) {
            System.out.println("kapacitet i potrosnja ne mogu biti manji od 0");
            return false;
        }
        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {

            ps.setString(1, tablice);
            ps.setInt(2, tipGoriva);
            ps.setBigDecimal(3, potrosnja);
            ps.setBigDecimal(4, kapacitet);
            int numberOfRows = ps.executeUpdate();
            return numberOfRows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(VehicleOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    @Override
    public int deleteVehicles(String... licencePlateNumbers) {
        int numberOfRows = 0;
        String sql = "delete from vozila where RegistarskiBroj = ?";
        for (int i = 0; i < licencePlateNumbers.length; i++) {
            try (PreparedStatement ps = connection.prepareStatement(sql);) {
                ps.setString(1, licencePlateNumbers[i]);
                numberOfRows += ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(VehicleOp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return numberOfRows;
    }

    @Override
    public List<String> getAllVehichles() {
        String sql = "select RegistarskiBroj from vozila";
        Statement stmt;
        List returnVal = new ArrayList<>();
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

                returnVal.add(rs.getString(1));

            }

            stmt.close();
            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(VehicleOp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return returnVal;
    }

    @Override
    public boolean changeFuelType(String licence, int type) {

        String sql = "update vozila set TipGoriva = ? where RegistarskiBroj = ? and driving = 0";

        int idV = getIdVFromLicence(licence);
        if (idV == -1 || type > 2 || type < 0) {
            return false;
        }

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, type);
            ps.setString(2, licence);
            int numberOfRows = ps.executeUpdate();
            return numberOfRows > 0;

        } catch (SQLException ex) {
            Logger.getLogger(VehicleOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    @Override
    public boolean changeConsumption(String licence, BigDecimal consumption) {
        String sql = "update vozila set Potrosnja = ? where RegistarskiBroj = ? and driving = 0";

        int idV = getIdVFromLicence(licence);
        if (idV == -1 || consumption.floatValue() <= 0) {
            return false;
        }

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setBigDecimal(1, consumption);
            ps.setString(2, licence);
            int numberOfRows = ps.executeUpdate();
            return numberOfRows > 0;

        } catch (SQLException ex) {
            Logger.getLogger(VehicleOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean changeCapacity(String licence, BigDecimal capacity) {
        String sql = "update vozila set Nosivost = ? where RegistarskiBroj = ? and driving = 0";

        int idV = getIdVFromLicence(licence);
        if (idV == -1 || capacity.floatValue() <= 0) {
            return false;
        }

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setBigDecimal(1, capacity);
            ps.setString(2, licence);
            int numberOfRows = ps.executeUpdate();
            return numberOfRows > 0;

        } catch (SQLException ex) {
            Logger.getLogger(VehicleOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean parkVehicle(String licence, int idM) {
//        TREBA PROVERITI JOS DA LI JE VOZILO TRENUTNO U VOZNJI NEMAM POJMA KAKO TO SAD TAKO DA NEK OSTANE 
        String sql = "insert into VozilaMagacin (idM, idV) values(?,?)";
        String sql1 = "update vozila set driving = 0 where Idv = ?";

        int idV = getIdVFromLicence(licence);
        if (idV == -1) {
            return false;
        }

        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);) {
            
            ps.setInt(1, idM);
            ps.setInt(2, idV);
            
            boolean status = ps.executeUpdate() > 0;
            
            ps1.setInt(1, idV);
            
            boolean status1 = ps1.executeUpdate() > 0;
            
            return status && status1;

        } catch (SQLException ex) {
            Logger.getLogger(VehicleOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public static void main(String[] args) {
        VehicleOp v = new VehicleOp();

//        boolean ret = v.insertVehicle("061261325", 1, BigDecimal.ONE, BigDecimal.TEN);
//        System.out.println(ret);
//        v.insertVehicle("0665", 0, BigDecimal.ZERO, BigDecimal.TEN);
    }

}
