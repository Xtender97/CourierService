/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.StockroomOperations;

/**
 *
 * @author Xtender
 */
public class StockroomOp implements StockroomOperations {

    public Connection connection = DB.getInstance().getConnection();

    @Override
    public int insertStockroom(int idA) {
        String sql = "select idG from adrese where idA = ?";
        String sql1 = "select count(*) from magacini m, adrese a where a.idA = m.idA and a.idG = ?";
        String sql2 = "insert into magacini (idA) values (?)";
        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);
                PreparedStatement ps2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1, idA);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int idG = rs.getInt(1);
                ps1.setInt(1, idG);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    int numberOfStockRoomsInThisCity = rs1.getInt(1);
                    if (numberOfStockRoomsInThisCity > 0) {
                        return -1;
                    } else {
                        ps2.setInt(1, idA);
                        ps2.executeUpdate();
                        ResultSet rs2 = ps2.getGeneratedKeys();
                        if (rs2.next()) {

                            return rs2.getInt(1);
                        }
                        return -1;
                    }
                }

            }
            return -1;

        } catch (SQLException ex) {
            Logger.getLogger(StockroomOp.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public boolean deleteStockroom(int idM) {
        String sql = "select count(*) from paketimagacin where idM = ?";
        String sql1 = "select count(*) from vozilamagacin where idM = ?";
        String sql2 = "delete from magacini where idM = ?";

        try (
                PreparedStatement ps1 = connection.prepareStatement(sql1);
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps2 = connection.prepareStatement(sql2);) {
            ps.setInt(1, idM);
            ps1.setInt(1, idM);

            int numberOfRows = 0;

            ResultSet rs = ps.executeQuery();
            ResultSet rs1 = ps1.executeQuery();

            if (rs.next()) {
                numberOfRows += rs.getInt(1);
            } else {
                return false;
            }
            if (rs1.next()) {
                numberOfRows += rs1.getInt(1);
            } else {
                return false;
            }

            if (numberOfRows > 0) {
                return false;
            } else {
                ps2.setInt(1, idM);
                int affected = ps2.executeUpdate();
                return affected > 0;
            }

        } catch (SQLException ex) {
            Logger.getLogger(StockroomOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public int deleteStockroomFromCity(int idG) {
        String sql = "select IdM from magacini m, adrese a where a.IdA = m.IdA and a.IdG = ?";
        int idM = 0;
        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setInt(1, idG);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idM = rs.getInt(1);
                if (deleteStockroom(idM)) {
                    return idM;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(StockroomOp.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

    }

    @Override
    public List<Integer> getAllStockrooms() {
        String sql = "select IdM from magacini";
        Statement stmt;
        List returnVal = new ArrayList<>();
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

                returnVal.add(rs.getInt(1));

            }

            stmt.close();     

        } catch (SQLException ex) {
            Logger.getLogger(CityOp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return returnVal;
    }

}
