/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kurirskasluzba;

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
    public int deleteCity(String... strings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteCity(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAllCities() {

        String sql = "select IdG from Gradovi";
        Statement stmt;
        List returnVal = new ArrayList<Integer>();
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.print(rs.getString(i) + " ");
                }
                returnVal.add(rs.getInt(1));
                System.out.println();
            }

            stmt.close();
            rs.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return returnVal;

    }

    public static void main(String[] args) {
        CityOp c = new CityOp();
//        int i = c.insertCity("Beograd", "11000");
        c.getAllCities();
//        System.out.println(i);
    }

}
