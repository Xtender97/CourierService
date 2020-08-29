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
import rs.etf.sab.operations.AddressOperations;

/**
 *
 * @author Xtender
 */
public class AddressOp implements AddressOperations {

    public Connection connection = DB.getInstance().getConnection();

    @Override
    public int insertAddress(String street, int number, int cityId, int xCord, int yCord) {
        
        String sql = "insert into adrese (ulica, broj, IdG, Xcord, Ycord) values(?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, street);
            ps.setInt(2, number);
            ps.setInt(3, cityId);
            ps.setInt(4, xCord);
            ps.setInt(5, yCord);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
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
            Logger.getLogger(AddressOp.class
                    .getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public int deleteAddresses(String ulica, int broj) {
        String sql = "delete from adrese where ulica = ? and broj = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, ulica);
            ps.setInt(2, broj);
            int numOfRows = ps.executeUpdate();
            ps.close();
            return numOfRows;
        } catch (SQLException ex) {

            Logger.getLogger(AddressOp.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }

    }

    @Override
    public boolean deleteAdress(int idA) {
        try {
            String sql = "delete from adrese where IdA = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idA);
            int numberOfRows = ps.executeUpdate();
            ps.close();
            return numberOfRows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(CityOp.class
                    .getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public int deleteAllAddressesFromCity(int idG) {
        String sql = "delete from adrese where IdG = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idG);
            int numOfRows = ps.executeUpdate();
            ps.close();
            return numOfRows;
        } catch (SQLException ex) {

            Logger.getLogger(AddressOp.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public List<Integer> getAllAddresses() {
        String sql = "select IdA from adrese";
        List returnVal = new ArrayList<Integer>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                returnVal.add(rs.getInt(1));

            }

            stmt.close();
            rs.close();
            return returnVal;


        } catch (SQLException ex) {
            Logger.getLogger(AddressOp.class.getName()).log(Level.SEVERE, null, ex);
            return returnVal;
        }

    }

    @Override
    public List<Integer> getAllAddressesFromCity(int idG) {
          String sql = "select IdA from adrese where IdG = ?";
        List returnVal = new ArrayList<Integer>();

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idG);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                returnVal.add(rs.getInt(1));

            }

            stmt.close();
            rs.close();
            return returnVal;


        } catch (SQLException ex) {
            Logger.getLogger(AddressOp.class.getName()).log(Level.SEVERE, null, ex);
            return returnVal;
        }
    }

    public static void main(String[] args) {
//        AddressOp a = new AddressOp();
//        int i = a.insertAddress("ulica2", 3, 18, 2, 2);
//        System.out.println("dodao sam ulicu sa id: " + i);
//        List l = a.getAllAddresses();
//        l.forEach(elem -> System.out.println(elem));
//        
//        boolean brojObrisanih = a.deleteAdress(5);
//                System.out.println(brojObrisanih);
//        l = a.getAllAddressesFromCity(17);
//        l.forEach(elem -> System.out.println(elem));
        
    }
}
