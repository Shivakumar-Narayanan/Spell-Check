package Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Scanner;
public class Dym {
    public Dym() {

    }

    public String getDidYoutMean(String s) {
        try {
            String query = s;
            //remove duplicate spaces
            query = query.replaceAll(" ", "+");
            URLConnection connection = new URL("https://www.google.com/search?q=" + query).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            BufferedReader br  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while((line = br.readLine()) != null) {
                sb.append(line);
                //System.out.println(sc.next());
            }

            String result = sb.toString();
            //System.out.println(result);

            result = result.replaceAll("<[^>]*>", "");
            //System.out.println("length of the result is: " + result.length());
            int index = result.indexOf("Showing results for");
            if(index == -1) {
                return "";
            }
            result = result.substring(index == -1 ? 0 : index);
            index = result.indexOf('(');
            result = result.substring(0, index);
            result = result.substring(20);
            return result;
        }
        catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String args[]) throws IOException {
        long start = System.nanoTime();
        String query = "";
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a search query: ");
        query = sc.nextLine();
        query = query.replaceAll(" ", "+");
        URLConnection connection = new URL("https://www.google.com/search?q=" + query).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();

        BufferedReader br  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
        //Instantiating the StringBuffer class to hold the result
        StringBuffer sb = new StringBuffer();
        String line = "";
        while((line = br.readLine()) != null) {
            sb.append(line);
            //System.out.println(sc.next());
        }
        //Retrieving the String from the String Buffer object
        String result = sb.toString();
        //System.out.println(result);
        //Removing the HTML tags
        result = result.replaceAll("<[^>]*>", "");
        //System.out.println("length of the result is: " + result.length());
        int index = result.indexOf("Showing results for");
        if(index == -1) {
            System.out.println("Cannot find a match");
            return;
        }
        result = result.substring(index == -1 ? 0 : index);
        index = result.indexOf('(');
        result = result.substring(0, index);
        result = result.substring(20);
        //System.out.println("Contents of the web page: "+result);
        System.out.println("Did you mean: " + result);
        long end = System.nanoTime();
        double time = ((double)end - start) / 1000000000;
        //System.out.println("Time taken: " + time);
    }
}