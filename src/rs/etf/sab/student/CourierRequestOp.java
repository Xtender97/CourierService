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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author Xtender
 */
public class CourierRequestOp implements CourierRequestOperation {

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
    public boolean insertCourierRequest(String userName, String licence) {
        String sql = "select count(*) from Kuriri where idK = ? or BrojVozackeDozvole = ?";
        String sql1 = "select count(*) from KurirZahtevi where idK = ? or BrojVozacke = ?";
        String sql2 = "insert into KurirZahtevi values(?,?)";

        int IdK = getUserIdFromUserName(userName);
        if (IdK == -1) {
            System.out.println("Nema korisnika sa ovim korisnickim imenom");
            return false;
        }
        int numberOfRows = 0;

        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);
                PreparedStatement ps2 = connection.prepareStatement(sql2);) {
            ps.setInt(1, IdK);
            ps1.setInt(1, IdK);
            ps.setString(2, licence);
            ps1.setString(2, licence);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                numberOfRows += rs.getInt(1);
            } else {
                return false;
            }
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                numberOfRows += rs1.getInt(1);
            } else {
                return false;
            }

            if (numberOfRows > 0) {
                System.out.println("Postoji zahtev ili kurir sa ovim userNamemo ili brojem vozacke");
                return false;
            } else {
                ps2.setInt(1, IdK);
                ps2.setString(2, licence);

                int broj = ps2.executeUpdate();
                return broj > 0;
            }

        } catch (SQLException ex) {
           //// Logger.getLogger(CourierRequestOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    @Override
    public boolean deleteCourierRequest(String userName) {

        int idK = getUserIdFromUserName(userName);
        if (idK == -1) {
            System.out.println("Nema korisnika sa ovim korisnickim imenom");
            return false;
        }
        String sql = "delete from KurirZahtevi where idK = ?";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {

            ps.setInt(1, idK);
            int numberOfRows = ps.executeUpdate();
            return numberOfRows > 0;

        } catch (SQLException ex) {
           //// Logger.getLogger(CourierRequestOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String userName, String licence) {

        int idK = getUserIdFromUserName(userName);
        if (idK == -1) {
            System.out.println("Nema korisnika sa ovim korisnickim imenom");
            return false;
        }
        String sql = "select count(*) from Kuriri where BrojVozackeDozvole = ?";
        String sql1 = "select count(*) from KurirZahtevi where BrojVozacke = ?";
        String sql2 = "update KurirZahtevi set BrojVozackeDozvole = ? where idK = ?";

        int numberOfRows = 0;

        try (
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatement ps1 = connection.prepareStatement(sql1);
                PreparedStatement ps2 = connection.prepareStatement(sql2);) {

            ps.setString(1, licence);
            ps1.setString(1, licence);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                numberOfRows += rs.getInt(1);
            } else {
                return false;
            }
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                numberOfRows += rs1.getInt(1);
            } else {
                return false;
            }

            if (numberOfRows > 0) {
                System.out.println("Postoji zahtev sa ovim brojem vozacke");
                return false;
            } else {
                ps2.setString(1, licence);
                ps2.setInt(2, idK);

                int broj = ps2.executeUpdate();
                return broj > 0;
            }

        } catch (SQLException ex) {
           //// Logger.getLogger(CourierRequestOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    @Override
    public List<String> getAllCourierRequests() {
        String sql = "select idK from KurirZahtevi";
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
            Logger.getLogger(CourierOp.class.getName()).log(Level.SEVERE, null, ex);

        }
        return returnVal;
    }

    @Override
    public boolean grantRequest(String userName) {

        CourierOp courierOp = new CourierOp();

        int idK = getUserIdFromUserName(userName);
        if (idK == -1) {
            System.out.println("Nema korisnika sa ovim korisnickim imenom");
            return false;
        }

        String sql = "select BrojVozacke from KurirZahtevi where idK = ?";

        try (
                PreparedStatement ps = connection.prepareStatement(sql);) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String licence = rs.getString(1);
                boolean retVal1 = courierOp.insertCourier(userName, licence);
                if (retVal1) {
                    boolean retVal2 = deleteCourierRequest(userName);
                    if (retVal2) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                System.out.println("Nema zahteva sa ovim korisnickim imenom");
                return false;
            }
        } catch (SQLException ex) {
           ////// Logger.getLogger(CourierRequestOp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

}
