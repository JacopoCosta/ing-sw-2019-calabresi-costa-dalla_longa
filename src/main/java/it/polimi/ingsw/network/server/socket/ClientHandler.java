package it.polimi.ingsw.network.server.socket;

import it.polimi.ingsw.network.server.StreamReceiver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket socket;

    private Scanner in;
    private PrintWriter out;

    private StreamReceiver streamReceiver;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();

            try {
                socket.close();
            } catch (IOException ignored) {
            }
            return;
        }

        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();

            try {
                in.close();
            } catch (Exception ignored) {
            }
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            return;
        }
        streamReceiver = StreamReceiver.getInstance();
        //socket not ready
    }
}
