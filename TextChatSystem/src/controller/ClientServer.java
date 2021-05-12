/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import model.Message;
import model.User;

/**
 *
 * @author hoang
 */
public class ClientServer {

    private Socket socket;
    private final int PORT = 4444;
    private final String HOST = "localhost";
    private volatile boolean stop;

    private DataOutputStream dataOutStream;
    private DataInputStream dataInStream;
    private ObjectOutputStream objOutStream;
    private ObjectInputStream objInStream;

    public ClientServer() {
        try {
            this.socket = new Socket(HOST, PORT);
            this.stop = false;

            try {
                dataOutStream = new DataOutputStream(socket.getOutputStream());
                dataInStream = new DataInputStream(socket.getInputStream());
                objOutStream = new ObjectOutputStream(socket.getOutputStream());
                objInStream = new ObjectInputStream(socket.getInputStream());

            } catch (IOException e) {
                System.out.println("Error when create in/output stream! " + e);
            }
        } catch (IOException e) {
            System.out.println("Can not create client server! " + e);
        }
    }

//    @Override
//    public void run() {
//        while (!stop) {
//            try {
//                String code = dataInStream.readUTF();
//                System.out.println(code);
//            } catch (IOException e) {
//                System.out.println("Can not read from dataInStream! " + e);
//            }
//        }
//    }
    public User login(User user) {
        User userLogin = null;
        try {
            dataOutStream.writeUTF("login");
            dataOutStream.flush();
            objOutStream.writeObject(user);
            objOutStream.flush();
            try {
                userLogin = (User) objInStream.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Can not cast objInStream to User! " + e);
            }
        } catch (IOException e) {
            System.out.println("Can not send data to server when login! " + e);
        }
        return userLogin;
    }

    public boolean register(User user) {
        boolean registerSuccess = false;
        try {
            dataOutStream.writeUTF("register");
            dataOutStream.flush();
            objOutStream.writeObject(user);
            objOutStream.flush();
            registerSuccess = dataInStream.readBoolean();
        } catch (IOException e) {
            System.out.println("Can not send data to server when register! " + e);
        }
        return registerSuccess;
    }

    public ArrayList<User> getListRegistedUser() {
        ArrayList<User> listRegistedUser = null;
        try {
            dataOutStream.writeUTF("listRegistedUser");
            dataOutStream.flush();
            try {
                listRegistedUser = (ArrayList<User>) objInStream.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Can not cast objInStream to ArrayList<User> " + e);
            }
        } catch (IOException e) {
            System.out.println("Can not send data to server when get list registed user! " + e);
        }
        return listRegistedUser;
    }

    public int getNumberOfflineMessage() {
        int numberOfflineMsg = -99;
        try {
            dataOutStream.writeUTF("getNumberOfflineMsg");
            dataOutStream.flush();
            numberOfflineMsg = dataInStream.readInt();
        } catch (IOException e) {
            System.out.println("Can not send data to server when get number offline msg! " + e);
        }
        return numberOfflineMsg;
    }

    public ArrayList<Message> getMessageFromUser(User receiver) {
        ArrayList<Message> listMessage = null;
        try {
            dataOutStream.writeUTF("getMessageFromUser");
            dataOutStream.flush();
            objOutStream.writeObject(receiver);
            objOutStream.flush();
            try {
                listMessage = (ArrayList<Message>) objInStream.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Can not case objInStream to ArrayList<Message>! " + e);
            }
        } catch (IOException e) {
            System.out.println("Can not send data to server when get msg from User! " + e);
        }
        return listMessage;
    }

    public void sendMessageToUser(Message msg) {
        try {
            dataOutStream.writeUTF("sendMessage");
            dataOutStream.flush();
            objOutStream.writeObject(msg);
            objOutStream.flush();
        } catch (IOException e) {
            System.out.println("Can not send data to server when send msg!" + e);
        }

    }

    public ArrayList<String> getListHasOfflineMsg() {
        ArrayList<String> listUserHasOfflineMsg = null;
        try {
            dataOutStream.writeUTF("getListHasOfflineMsg");
            dataOutStream.flush();
            try {
                listUserHasOfflineMsg = (ArrayList<String>) objInStream.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println("Can not cast objInStream to ArrayList<String>! " + e);
            }
        } catch (IOException e) {
            System.out.println("Can not send data to Server when get list has offline msg! " + e);
        }
        return listUserHasOfflineMsg;
    }
    

}
