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
import javax.swing.DefaultListModel;
import model.Message;
import model.User;

/**
 *
 * @author hoang
 */
public class MessageDAO {

    private Connection connection;
    private DBContext db;
    private ResultSet rs;
    private PreparedStatement pstt;

    public MessageDAO() {
        db = new DBContext();
        connection = db.connection;
    }

    public void sendMessage(Message msg) {
        String query = "insert into Messages(msgSender, msgReceiver, msgContent) values(?,?,?)";
        try {
            pstt = connection.prepareStatement(query);
            pstt.setString(1, msg.getSender());
            pstt.setString(2, msg.getReceiver());
            pstt.setString(3, msg.getContent());
            pstt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Can not send message to database! " + e);
        }
    }

    public int getNumberOfflineMessage(User userConnected) {
        int numberOfflineMsg = -99;
        String query = "select COUNT(msgID) as numberOfflineMsg  from Messages where msgReceiver = ? and msgIsRead = 0";
        try {
            pstt = connection.prepareStatement(query);
            pstt.setString(1, userConnected.getUserName());
            rs = pstt.executeQuery();
            if (rs.next()) {
                numberOfflineMsg = rs.getInt("numberOfflineMsg");
            }
        } catch (SQLException e) {
            System.out.println("Can not get number offline msg from DB! " + e);
        }
        return numberOfflineMsg;
    }

    public ArrayList<Message> getMessageFromUser(User sender, User receiver) {
        String query = "SELECT *\n"
                + "FROM Messages where msgReceiver =? and msgSender = ?\n"
                + "UNION ALL\n"
                + "SELECT *\n"
                + "FROM Messages where msgSender =? and msgReceiver =?\n"
                + "ORDER BY msgSendTime ASC";
        ArrayList<Message> listMessage = new ArrayList<>();
        try {
            pstt = connection.prepareStatement(query);
            pstt.setString(1, sender.getUserName());
            pstt.setString(3, sender.getUserName());
            pstt.setString(2, receiver.getUserName());
            pstt.setString(4, receiver.getUserName());

            rs = pstt.executeQuery();
            setStatusMessage(sender.getUserName(), receiver.getUserName());
            while (rs.next()) {
                listMessage.add(new Message(rs.getString("msgSender"), rs.getString("msgReceiver"), rs.getString("msgContent"), rs.getBoolean("msgIsRead")));
            }
        } catch (SQLException e) {
            System.out.println("Can not get list message by User from DB! " + e);
        }
        return listMessage;
    }

    private void setStatusMessage(String senderName, String receiverName) {
        String query = "update Messages\n"
                + "set \n"
                + "	msgIsRead = 1\n"
                + "where msgReceiver = ? and msgSender = ?";
        try {
            pstt = connection.prepareStatement(query);
            pstt.setString(1,senderName);
            pstt.setString(2,receiverName);
            pstt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Can not set status msg to DB! "+e);
        }
            
    }
    public ArrayList<String> getListHasOfflineMsg(User receiver){
        String query = "select msgSender  from Messages where msgReceiver = ? and msgIsRead = 0 group by msgSender";
        ArrayList<String> listHasOfflineMsg = new ArrayList<>();
        try {
            pstt = connection.prepareStatement(query);
            pstt.setString(1, receiver.getUserName());
            rs = pstt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("msgSender"));
                listHasOfflineMsg.add(rs.getString("msgSender")+"("+getNumberOfflineMsgFromUser(rs.getString("msgSender"),receiver.getUserName())+")");
            }
        } catch (SQLException e) {
            System.out.println("Can not get list has offline msg from DB! "+e);
        }
        return listHasOfflineMsg;
    }
    
    private int getNumberOfflineMsgFromUser(String senderName, String receiverName){
        String query = "select COUNT(msgID) as numberOfflineMsg from Messages where msgSender = ? and msgReceiver = ? and msgIsRead = 0 group by msgSender";
        try {
            pstt = connection.prepareStatement(query);
            pstt.setString(1, senderName);
            pstt.setString(2, receiverName);
            ResultSet rs2;
            rs2 = pstt.executeQuery();
            if(rs2.next()){
                return rs2.getInt("numberOfflineMsg");
            }
        } catch (SQLException e) {
            System.out.println("Can not get number offline msg from use in DB! "+e);
        }
        return 0;
    }
    
}
