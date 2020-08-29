/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author Xtender
 */
public class CityOp implements CityOperations {

    public Connection connection = DB.getInstance().getConnection();

    @Override
    public int insertCity(String name, String postalCode) {
        String sql = "insert into Gradovi (Naziv, PostanskiBroj) values(?, ?)";

        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, postalCode);

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int returnVal;
            if (rs.next()) {
                returnVal = rs.getInt(1);
            } else {
                returnVal = -1;
            }
            ps.close();
            rs.close();
            return returnVal;
        } catch (SQLException ex) {
            Logger.getLogger(CityOp.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

    }

    @Override
    public int deleteCity(String... gradovi) {
        String sql = "delete from gradovi where naziv in (?)";
        PreparedStatement ps;
        int numberOfRows = 0;
        int last = gradovi.length - 1;
        for (int i = 0; i < gradovi.length; i++) {
            try {
                ps = connection.prepareStatement(sql);
                ps.setString(1, gradovi[i]);
                numberOfRows += ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(CityOp.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }
        }

        return numberOfRows;
    }

    @Override
    public boolean deleteCity(int idG) {
        try {
            String sql = "delete from gradovi where IdG = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idG);
            int numberOfRows = ps.executeUpdate();
            ps.close();
            if (numberOfRows > 0) {
                return true;
            } else {
                return false;

            }
        } catch (SQLException ex) {
            Logger.getLogger(CityOp.class
                    .getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public List<Integer> getAllCities() {

        String sql = "select IdG from Gradovi";
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
            Logger.getLogger(CityOp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return returnVal;

    }

    public static void main(String[] args) {
        CityOp c = new CityOp();
        int i = c.insertCity("Beograd", "11000");
//        c.insertCity("Novi Sad", "22000");
//        c.insertCity("Nis", "33000");
//
//        List l = c.getAllCities();
//        l.forEach(elem -> System.out.println(elem));
//        int broj = c.deleteCity("Beograd", "Nis");
//           boolean i = c.deleteCity(17);
//        l = c.getAllCities();
//        l.forEach(elem -> System.out.println(elem));
//
//        System.out.println("broj obrisanih gradova " + broj);
//
//        System.out.println(i);
    }

}
