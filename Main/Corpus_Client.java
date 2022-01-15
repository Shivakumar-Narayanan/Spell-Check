package Main;

import java.nio.Buffer;
import java.util.*;
import java.net.*;
import java.io.*;

public class Corpus_Client {

    private static final int PORT = 6969;
    private Socket client_socket;
    BufferedReader in;
    PrintWriter out;

    Corpus_Client() throws ConnectException {
        try {
            client_socket = new Socket("localhost", PORT);
            in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            out = new PrintWriter(client_socket.getOutputStream(), true);
            System.out.println("[+] Connected to remote Corpus");
        }
        catch(ConnectException e) {
            throw new ConnectException("Cannot connect to server");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public long getFreq(String a) {
        try {
            out.println(a);
            return Long.parseLong(in.readLine());
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getFreq(String a, String b) {
        try {
            out.println(a + " " + b);
            return Long.parseLong(in.readLine());
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getFreq(String a, String b, String c) {
        try {
            out.println(a + " " + b + " " + c);
            return Long.parseLong(in.readLine());
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Set<String> reconstruct(String s, int right_level) {
        try {
            out.println("r" + " " + s + " " + right_level + " r");
            Set<String> res = new HashSet<>();

            String line;
            while(!(line = in.readLine()).equals("OVER")) {
                res.add(line);
            }

            return res;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
