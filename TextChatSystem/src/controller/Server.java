/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dal.MessageDAO;
import dal.UserDAO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import model.Message;
import model.User;

/**
 *
 * @author hoang
 */
public class Server {

    public static void main(String[] args) {
        final int PORT = 4444;
        boolean stop = false;
        Executor pool = Executors.newFixedThreadPool(20);
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server start at port " + PORT);
            while (!stop) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    pool.execute(clientHandler);
                    System.out.println("Connect to server success!");

                } catch (IOException e) {
                    System.out.println("Client Server can not connect to server! " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("Can not create Server Socket! " + e);
        }
    }
}

class ClientHandler implements Runnable {

    private Socket socket;
    private UserDAO userDAO;
    private MessageDAO msgDAO;
    private User userConnected;
    private volatile boolean stop;

    private DataOutputStream dataOutStream;
    private DataInputStream dataInStream;
    private ObjectOutputStream objOutStream;
    private ObjectInputStream objInStream;

    public ClientHandler() {
    }

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.userDAO = new UserDAO();
        this.msgDAO = new MessageDAO();
        this.userConnected = null;
        this.stop = false;

        try {
            dataOutStream = new DataOutputStream(socket.getOutputStream());
            dataInStream = new DataInputStream(socket.getInputStream());
            objOutStream = new ObjectOutputStream(socket.getOutputStream());
            objInStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            System.out.println("Error when create in/output stream! " + e);
        }
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                String code = dataInStream.readUTF();
                switch (code) {
                    case "login": {
                        try {
                            User userLogin = (User) objInStream.readObject();
                            userLogin = userDAO.loginUser(userLogin);
                            if (userLogin != null) { //set data for set status user when socket close
                                userConnected = userLogin;
                            }
                            objOutStream.writeObject(userLogin);
                            objOutStream.flush();
                        } catch (ClassNotFoundException e) {
                            System.out.println("Can not cast from objInputStream to User" + e);
                        }
                    }
                    break;
                    case "register": {
                        try {
                            User userRegister = (User) objInStream.readObject();
                            boolean isSuccess = userDAO.registerUser(userRegister);
                            dataOutStream.writeBoolean(isSuccess);
                            dataOutStream.flush();
                        } catch (ClassNotFoundException e) {
                            System.out.println("Can not cast from objInStream to User " + e);
                        }
                    }
                    break;
                    case "listRegistedUser": {
                        ArrayList<User> listRegistedUser = userDAO.getListRegistedUser(userConnected);
                        try {
                            objOutStream.writeObject(listRegistedUser);
                            objOutStream.flush();
                        } catch (IOException e) {
                            System.out.println("Can not send ArrayList<User> to client! " + e);
                        }
                    }
                    break;
                    case "getNumberOfflineMsg": {
                        int numberOfflineMsg = msgDAO.getNumberOfflineMessage(userConnected);
                        dataOutStream.writeInt(numberOfflineMsg);
                        dataOutStream.flush();
                    }
                    break;
                    case "getMessageFromUser": {
                        try {
                            User receiver = (User) objInStream.readObject();
                            ArrayList<Message> listMessage = msgDAO.getMessageFromUser(userConnected,receiver);
                            objOutStream.writeObject(listMessage);
                            objOutStream.flush();
                        } catch (ClassNotFoundException e) {
                            System.out.println("Can not cast objInStream to User! " + e);
                        }
                    }
                    break;
                    case "sendMessage":{
                        try {
                            Message msg = (Message) objInStream.readObject();
                            msgDAO.sendMessage(msg);
                        } catch (ClassNotFoundException e) {
                            System.out.println("Can not cast objInStream to Message! "+e);
                        }
                    }
                    break;
                    case "getListHasOfflineMsg":{
                        ArrayList<String> listHasOfflineMsg = msgDAO.getListHasOfflineMsg(userConnected);
                        try {
                            objOutStream.writeObject(listHasOfflineMsg);
                            objOutStream.flush();
                        } catch (IOException e) {
                            System.out.println("Can not send data from Server to Client! "+e);
                        }
                    }
                    break;
                }
            } catch (IOException e) {
                System.out.println("Close socket " + e);
                if (userConnected != null) {
                    userDAO.setStatusUser(false, userConnected);
                }
                try {
                    socket.close();
                    dataInStream.close();
                    dataOutStream.close();
                    objInStream.close();
                    objOutStream.close();
                } catch (IOException ex) {
                    System.out.println("Can not close connection! " + ex);
                }
                stop = true;
            }
        }
    }

}
