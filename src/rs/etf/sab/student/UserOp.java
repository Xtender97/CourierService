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
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author Xtender
 */
public class UserOp implements UserOperations {

    public Connection connection = DB.getInstance().getConnection();

    //    -1 nepostoji user sa zadatim usernamom
//    idK
    public int getUserIdFromUserName(String userName) {
        String sql = "select idK from Korisnici where KorisnickoIme = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }

        } catch (SQLException ex) {
            //// Logger.getLogger(CourierRequestOp.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

    }

    @Override
    public boolean insertUser(String userName, String firstName, String lastName, String password, int idA) {
        String sql = "insert into korisnici (ime, prezime, korisnickoIme, sifra, idA) values(?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, userName);
            ps.setString(4, password);
            ps.setInt(5, idA);

            int numberOfRows = ps.executeUpdate();
            ps.close();
            return numberOfRows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(UserOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean declareAdmin(String userName) {
        try {
            String sql = "select idK from korisnici where korisnickoIme = ?";
            String sql1 = "select count(*) from administratori where idK= ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            int idK = 0;
//            ps.close();
            if (rs.next()) {
                idK = rs.getInt(1);
                rs.close();
                ps = connection.prepareStatement(sql1);
                ps.setInt(1, idK);
                rs = ps.executeQuery();
                if (rs.next()) {
                    boolean vecAdmin = rs.getInt(1) > 0;
                    rs.close();
                    ps.close();
                    if (vecAdmin) {
                        return false;
                    } else {
                        String sql2 = "insert into administratori values (?)";
                        PreparedStatement ps2 = connection.prepareStatement(sql2);
                        ps2.setInt(1, idK);
                        int numberOfRows = ps2.executeUpdate();
                        ps2.close();
                        return numberOfRows > 0;
                    }
                } else {
                    rs.close();
                    return false;
                }
            } else {
                rs.close();
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    @Override
    public int getSentPackages(String... userNames) {
        try {
            int numberOfSentPackeges = 0;
            int numberOfFoundUsers = 0;
            String sql = "select idK from korisnici where korisnickoIme = ?";
            String sql1 = "select count(*) from paketi where idK = ? and status <4 and status > 0";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = null;
            for (int i = 0; i < userNames.length; i++) {
                ps.setString(1, userNames[i]);
                rs = ps.executeQuery();
                int idK = 0;
                if (rs.next()) {
                    numberOfFoundUsers++;
                    idK = rs.getInt(1);
                    PreparedStatement ps1 = connection.prepareCall(sql1);
                    ps1.setInt(1, idK);
                    ResultSet rs1 = ps1.executeQuery();
                    if (rs1.next()) {
                        numberOfSentPackeges += rs1.getInt(1);
                    }
                    ps1.close();
                    rs1.close();
                } else {
                    System.out.println("no user with userName: " + userNames[i]);
                }
            }
            ps.close();
            rs.close();
            if (numberOfFoundUsers == 0) {
                return -1;
            } else {
                return numberOfSentPackeges;
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserOp.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public int deleteUsers(String... userNames) {
        int numberOfRows = 0;
        String sql = "delete from korisnici where korisnickoIme = ?";
        for (int i = 0; i < userNames.length; i++) {
            try (
                    PreparedStatement ps = connection.prepareStatement(sql);) {

                ps.setString(1, userNames[i]);
                numberOfRows += ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(UserOp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return numberOfRows;
    }

    @Override
    public List<String> getAllUsers() {
        String sql = "select korisnickoIme from korisnici";
        Statement stmt;
        List returnVal = new ArrayList<String>();
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {

                returnVal.add(rs.getString(1));

            }

            stmt.close();
            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(UserOp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return returnVal;
    }

    //TREBA TESTIRATI BROJ POSLATIH PAKETAAAAAAAAAAAA
//    public static void main(String[] args) {
//        UserOp u = new UserOp();
//        AddressOp a = new AddressOp();
//        CityOp c = new CityOp();
//        int idG = c.insertCity("Beograd", "11000");
//        int idA = a.insertAddress("ulica1", 1, idG, 23, 24);
//        u.insertUser("Xtender", "milan", "boskovic", "miki", idA);
//        u.insertUser("miki", "lepi", "baki", "sifra", idA);
//        List l = u.getAllUsers();
//        l.forEach(elem -> System.out.println(elem));
//        u.declareAdmin("Xtender");
//       u.deleteUsers("Xtender");
//    }
}
