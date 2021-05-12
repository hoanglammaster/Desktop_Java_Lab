/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.User;

/**
 *
 * @author hoang
 */
public class UserDAO {

    private Connection connection;
    private DBContext db;
    private ResultSet rs;
    private PreparedStatement pstt;

    public UserDAO() {
        db = new DBContext();
        connection = db.connection;
    }

    public boolean registerUser(User userRegister) {
        String query = "insert into Users(userName, userFullName, userPassword)"
                + "values(?,?,?)";
        try {
            pstt = connection.prepareStatement(query);
            pstt.setString(1, userRegister.getUserName());
            pstt.setString(2, userRegister.getFullName());
            pstt.setString(3, userRegister.getPassword());
            pstt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Can not insert to Users table in sql! "+e);
            return false;
        }
    }
    public User loginUser(User userLogin){
        String query = "select userFullName from Users where userName = ? and userPassword = ? and userIsOnline = 0";
        try {
            pstt = connection.prepareStatement(query);
            pstt.setString(1, userLogin.getUserName());
            pstt.setString(2, userLogin.getPassword());
            rs = pstt.executeQuery();
            if(rs.next()){
                userLogin.setFullName(rs.getString("userFullName"));
                setStatusUser(true, userLogin);
            }else{
                userLogin = null;
            }
        } catch (SQLException e) {
            System.out.println("Can not login to User table "+e);
        }
        return userLogin;
    }

    public void setStatusUser(boolean isOnline, User user){
        String query = "update Users set userIsOnline = ? where userName = ?";
        try {
            pstt = connection.prepareStatement(query);
            pstt.setBoolean(1, isOnline);
            pstt.setString(2,user.getUserName());
            pstt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Can not update status for User "+e);
        }
    }
    
    public ArrayList<User> getListRegistedUser(User userConnected){
        ArrayList<User> listRegistedUser  = new ArrayList<>();
        String query = "select userName, userFullName, userPassword, userIsOnline from Users where userName != ?";
        try {
            pstt = connection.prepareStatement(query);
            pstt.setString(1, userConnected.getUserName());
            rs = pstt.executeQuery();
            while(rs.next()){
                listRegistedUser.add(new User(rs.getString("userName"),rs.getString("userFullName"), rs.getString("userPassword"), rs.getBoolean("userIsOnline")));
            }
        } catch (SQLException e) {
            System.out.println("Can not get list registed user! "+e);
        }
        return listRegistedUser;
    }
    
}
