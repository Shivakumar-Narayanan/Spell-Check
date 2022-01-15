package Main;

import java.util.*;
import java.net.*;
import java.io.*;

public class Remote_Corpus {

    private static final int PORT = 6969;

    ServerSocket server_socket;
    Socket client_socket;
    BufferedReader in;
    PrintWriter out;
    Corpus corpus;

    Remote_Corpus() {
        try {
            /*corpus = new Corpus(
                    new File("C:\\Users\\WELCOME\\Desktop\\SymSpell\\SpellCheckProject\\src\\Main\\Unigram_58k.txt"),
                    new File("C:\\Users\\WELCOME\\Desktop\\SymSpell\\SpellCheckProject\\src\\Main\\Bigram_10M_3.txt"),
                    new File("C:\\Users\\WELCOME\\Desktop\\SymSpell\\SpellCheckProject\\src\\Main\\Trigram_10M.txt"));*/
            /*corpus = new Corpus(new File("C:\\Users\\WELCOME\\Desktop\\SymSpell\\SpellCheckProject\\src\\Main\\Unigram_58k.txt"),
                                new File("C:\\Users\\WELCOME\\Desktop\\SymSpell\\SpellCheckProject\\src\\Main\\Bigram_10M_3.txt"));*/
            corpus = new Corpus(new File(System.getProperty("user.dir") + "\\src\\Main\\Unigram_58k.txt"),
                    new File(System.getProperty("user.dir") + "\\src\\Main\\Bigram_10M_3.txt"));
            server_socket = new ServerSocket(PORT);
        }
        catch(Exception e) {
          e.printStackTrace();
        }
    }

    private void accept() {
        try {
            client_socket = server_socket.accept();
            in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            out = new PrintWriter(client_socket.getOutputStream(), true);
            System.out.println("[+] Connected to client...");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean serviceRequest() {
        try {
            String line = in.readLine();
            System.out.println("[+] New Request: " + line);

            if(line == null)    return false;
            String[] arr = line.split(" ");
            if(arr.length == 1) {
                out.println(corpus.getFreq(arr[0]));
            }
            else if(arr.length == 2) {
                out.println(corpus.getFreq(arr[0], arr[1]));
            }
            else if(arr.length == 3) {
                out.println(corpus.getFreq(arr[0], arr[1], arr[2]));
            }
            else {
                Set<String> res = corpus.reconstruct(arr[1], Integer.parseInt(arr[2]));
                for(String s : res) {
                    out.println(s);
                }
                out.println("OVER");
            }
            return true;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        Remote_Corpus rc = new Remote_Corpus();

        while(true) {
            rc.accept();
            while(rc.serviceRequest());
        }
    }
}
