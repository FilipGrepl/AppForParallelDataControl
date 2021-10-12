/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNode.stepExecution;

import com.app.commons.socketMessages.AcceptConnMsg;
import com.app.commons.socketMessages.GenericSocketMsg;
import com.app.commons.socketMessages.GenericSocketMsg.MsgType;
import static com.app.secondaryNode.TestSettings.PATH_TO_SERVER_KEYSTORE;
import static com.app.secondaryNode.TestSettings.TESTSERVER_KEYSTORE_PASSWORD;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author filip
 */
public class ServerSocketTester extends Thread {
    
    public static final int PORT = 9001;

    private final SSLServerSocket listener;
    private ObjectInputStream objectReceiver;
    private ObjectOutputStream objectSender;
    private GenericSocketMsg receiveMessage;
    private SSLSocket socket;
    private final AtomicBoolean isConnected;

    public ServerSocketTester() throws SocketException, UnknownHostException, IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        
        SSLContext ctx;
        KeyManagerFactory kmf;
        KeyStore ks;
        char[] passphrase = TESTSERVER_KEYSTORE_PASSWORD.toCharArray();

        ctx = SSLContext.getInstance("TLS");
        kmf = KeyManagerFactory.getInstance("SunX509");
        ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(PATH_TO_SERVER_KEYSTORE), passphrase);
        kmf.init(ks, passphrase);
        ctx.init(kmf.getKeyManagers(), null, null);

        SSLServerSocketFactory sslServerSocketFactory = ctx.getServerSocketFactory();
        listener = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);
        receiveMessage = null;
        socket = null;
        isConnected = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        try {
            socket = (SSLSocket) listener.accept();
            objectSender = new ObjectOutputStream(socket.getOutputStream());
            objectReceiver = new ObjectInputStream(socket.getInputStream());
            while (true) {
                GenericSocketMsg message = (GenericSocketMsg) objectReceiver.readObject();

                if (message.getMessageType() == MsgType.NODE_NAME) {
                    this.sendMessage(new AcceptConnMsg());
                    this.setIsConnected(true);
                    synchronized (this) {
                        this.notifyAll();
                    }
                } else if (message.getMessageType() != MsgType.IS_ALIVE) {
                    System.out.println("PRIMARY Received message type: " + message.getMessageType());
                    synchronized (listener) {
                        receiveMessage = message;
                    }
                }
            }
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }
    }

    public GenericSocketMsg getReceiveMessage() {
        synchronized (listener) {
            try {
                return receiveMessage;
            } finally {
                receiveMessage = null;
            }
        }
    }

    public void sendMessage(GenericSocketMsg message) throws IOException {
        try {
            this.objectSender.writeObject(message);
            this.objectSender.flush();
        } catch (IOException e) {
            closeSocket();
            throw e;
        }
    }

    public void closeSocket() throws IOException {
        this.listener.close();
        if (socket != null) {
            this.socket.close();
        }
        this.setIsConnected(false);
        synchronized (this) {
            this.notifyAll();
        }
    }

    private void setIsConnected(boolean value) {
        synchronized (isConnected) {
            isConnected.set(value);
        }
    }

    public boolean IsConnected() {
        synchronized (isConnected) {
            return isConnected.get();
        }
    }

}
