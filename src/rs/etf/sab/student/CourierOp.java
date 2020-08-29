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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author Xtender
 */
public class CourierOp implements CourierOperations {

    public Connection connection = DB.getInstance().getConnection();

    @Override
    public boolean insertCourier(String userName, String licence) {
        String sql = "select idK from korisnici where KorisnickoIme = ?";
        String sql1 = "insert into kuriri (idk, brojisporucenihpaketa, brojvozackedozvole, profit, status) values (?,?,?,?,?)";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int idK = rs.getInt(1);
                ps1.setInt(1, idK);
                ps1.setInt(2, 0);
                ps1.setString(3, licence);
                ps1.setBigDecimal(4, new BigDecimal(0));
                ps1.setBoolean(5, false);
                int numberOfRows = ps1.executeUpdate();
                return numberOfRows > 0;
            } else {
                System.out.println("nema korisnika sa zadatim korisnickim imenom");
                return false;
            }
        } catch (SQLException ex) {
            // Logger.getLogger(CourierOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean deleteCourier(String userName) {
        String sql = "select idK from korisnici where KorisnickoIme = ?";
        String sql1 = "delete from kuriri where idK = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int idK = rs.getInt(1);
                ps1.setInt(1, idK);
                int numberOfRows = ps1.executeUpdate();
                return numberOfRows > 0;
            } else {
                System.out.println("nema korisnika sa zadatim korisnickim imenom");
                return false;
            }
        } catch (SQLException ex) {
            // Logger.getLogger(CourierOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public List<String> getCouriersWithStatus(int status) {
        String sql = "select idK from kuriri where status = ?";
        String sql1 = "select korisnickoIme from korisnici where idK = ?";
        boolean statusBool = status == 1;
        List<String> returnVal = new ArrayList<>();
        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);) {
            ps.setBoolean(1, statusBool);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int idK = rs.getInt(1);
                ps1.setInt(1, idK);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    returnVal.add(rs1.getString(1));
                }
            }
        } catch (SQLException ex) {
            // Logger.getLogger(CourierOp.class.getName()).log(Level.SEVERE, null, ex);

        }
        return returnVal;
    }

    @Override
    public List<String> getAllCouriers() {
        String sql = "select idK from kuriri order by profit desc";
        String sql1 = "select korisnickoIme from korisnici where idK = ?";
        List<String> returnVal = new ArrayList<>();
        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int idK = rs.getInt(1);
                ps1.setInt(1, idK);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    returnVal.add(rs1.getString(1));
                }
            }
        } catch (SQLException ex) {
            // Logger.getLogger(CourierOp.class.getName()).log(Level.SEVERE, null, ex);

        }
        return returnVal;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
        String sql = "";
        if (numberOfDeliveries == -1) {
            sql = "select avg(profit) from kuriri";
        } else {
            sql = "select avg(profit) from kuriri where BrojIsporucenihPaketa = ?";
        }

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            if (numberOfDeliveries != -1) {
                ps.setInt(1, numberOfDeliveries);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            } else {
                return null;
            }

        } catch (SQLException ex) {
            // Logger.getLogger(CourierOp.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public static void main(String[] args) {

//        CourierOp c = new CourierOp();
//        BigDecimal b = c.getAverageCourierProfit(-1);
//        System.out.println(b);
    }

}
