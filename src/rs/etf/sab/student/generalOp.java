/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author Xtender
 */
public class generalOp implements GeneralOperations{

    public Connection connection = DB.getInstance().getConnection();
    @Override
    public void eraseAll() {
        String sql = "{call eraseAll( )}";
        try (
            CallableStatement cs = connection.prepareCall(sql);){
            cs.execute();
//            System.out.println("Deleted all record from database!");
            
        } catch (SQLException ex) {
            Logger.getLogger(generalOp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    public static void main(String[] args) {
        new generalOp().eraseAll();
    }
}
