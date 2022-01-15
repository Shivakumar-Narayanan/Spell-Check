package Main;

import java.net.ConnectException;
import java.util.*;
import java.io.*;

/**
 *
 * Is a Singleton Class
 * Contains the unigram, ie <word freq>
 * Contains the bigram,ie <word word freq>
 * Contains the functions to reconstruct a word from a level one or two deletion
 */
public class Corpus {

    private boolean useClient;
    Corpus_Client client;

    private static Corpus instance;

    private final Map<String, Set<String>> dict_2;
    private final Map<String, Set<String>> dict_1;
    private final Map<String, Long> dict_0;
    public Map<String, String> special_words = new HashMap<>();

    private final Map<String, Map<String, Long>> bigram;

    private final Map<String, Map<String, Map<String, Long>>> trigram;
    /**
     *
     * @param unigram_file The Unigram file as <String freq>
     * @throws IOException When the unigram file doesn't exist
     */
    Corpus(File unigram_file) throws IOException {
        if(!unigram_file.exists())  throw new IOException("Unigram File Doesn't Exist");
        long start = System.nanoTime();
        dict_0 = new HashMap<>();
        dict_1 = new HashMap<>();
        dict_2 = new HashMap<>();
        bigram = new HashMap<>();
        trigram = new HashMap<>();

        constructLevels(unigram_file);

        long end = System.nanoTime();
        double time = (double)(end - start) / 1000000000;
        System.out.println("Time to construct corpus: " + time + " seconds");
    }

    /**
     *
     * @param unigram_file The Unigram file as <String freq>
     * @param bigram_file The Bigram file as <String String freq>
     * @throws IOException when the unigram or bigram file doesn't exist
     */
    Corpus(File unigram_file, File bigram_file) throws IOException {
        if(!unigram_file.exists())  throw new IOException("Unigram File Doesnt Exist");
        if(!bigram_file.exists())  throw new IOException("Bigram File Doesn't Exist");
        long start = System.nanoTime();
        dict_0 = new HashMap<>();
        dict_1 = new HashMap<>();
        dict_2 = new HashMap<>();
        bigram = new HashMap<>();
        trigram = new HashMap<>();

        constructLevels(unigram_file);
        constructBigram(bigram_file);

        long end = System.nanoTime();
        double time = (double)(end - start) / 1000000000;
        System.out.println("Time to construct corpus: " + time + " seconds");
    }

    /**
     *
     * @param unigram_file
     * @param bigram_file
     * @param trigram_file
     * @throws IOException
     */
    Corpus(File unigram_file, File bigram_file, File trigram_file) throws IOException {
        if(!unigram_file.exists())  throw new IOException("Unigram File Doesnt Exist");
        if(!bigram_file.exists())  throw new IOException("Bigram File Doesn't Exist");
        if(!trigram_file.exists())  throw new IOException("Trigram File Doesn't Exist");
        long start = System.nanoTime();
        dict_0 = new HashMap<>();
        dict_1 = new HashMap<>();
        dict_2 = new HashMap<>();
        bigram = new HashMap<>();
        trigram = new HashMap<>();

        constructLevels(unigram_file);
        constructBigram(bigram_file);
        constructTrigram(trigram_file);

        long end = System.nanoTime();
        double time = (double)(end - start) / 1000000000;
        System.out.println("Time to construct corpus: " + time + " seconds");
    }

    Corpus() throws ConnectException {
        useClient = true;
        client = new Corpus_Client();
        dict_0 = null;
        dict_1 = null;
        dict_2 = null;
        bigram = null;
        trigram = null;
        special_words.put("ll", "will");
        special_words.put("hrs", "hours");
        special_words.put("hw", "how");
        special_words.put("shld", "should");
    }

    public static Corpus getInstance(File unigram_file, File bigram_file) {
        if(instance == null)    {
            try {
                instance = new Corpus(unigram_file, bigram_file);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     *
     * @param s The String whose frequency in the unigram is to be checked
     * @return The freq of s
     */
    public long getFreq(String s) {
        if(useClient) {
            return client.getFreq(s);
        }

        Long freq = dict_0.get(s);
        if(freq == null)    freq = (long)0;
        return freq;
    }

    /**
     *
     * @param a The first string in the phrase
     * @param b The second string in the phrase
     * @return The freq of a then b in the bigram
     */
    public long getFreq(String a, String b) {
        if(useClient) {
            return client.getFreq(a, b);
        }

        if(!bigram.containsKey(a))  return 0;
        Long freq = bigram.get(a).get(b);
        if(freq == null)    freq = (long)0;
        return freq;
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public long getFreq(String a, String b, String c) {
        if(useClient) {
            return client.getFreq(a, b, c);
        }

        if(!trigram.containsKey(a))  return 0;
        if(!trigram.get(a).containsKey(b)) return 0;
        Long freq = trigram.get(a).get(b).get(c);
        if(freq == null)    freq = (long)0;
        return freq;
    }

    /**
     * Constructs dict_0, dict_1 and dict_2 from the unigram
     * @param unigram_file The file containing the unigram
     */
    private void constructLevels(File unigram_file) {
        /* getting lines from the file */
        try {
            Scanner sc = new Scanner(unigram_file);
            String line;
            while(sc.hasNextLine()) {
                /* each line will be of the form <word freq> */
                line = sc.nextLine();
                String[] arr = line.split(" ");
                String word = arr[0].toLowerCase().trim();
                long freq = Long.parseLong(arr[1].trim());

                /*adding the string to dict_0, level 1 deletions to dict_1 and level 2 deletion to dict_2 */
                add_to_maps(word, freq);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * adding the string to dict_0, level 1 deletions to dict_1 and level 2 deletion to dict_2
     * @param s The String s from the unigram
     */
    private void add_to_maps(String s, long freq) {

        /*generating level 1 deletions */
        Set<String> level_1 = new HashSet<>();

        char[] arr = new char[s.length() - 1];
        for(int del = 0; del < s.length(); del++) {
            int k = 0;
            for(int i = 0; i < s.length(); i++) {
                if(i != del)    arr[k ++] = s.charAt(i);
            }
            level_1.add(String.valueOf(arr));
        }

        /* generating level 2 deletions */
        Set<String> level_2 = new HashSet<>();
        if(s.length() > 1) {
            arr = new char[s.length() - 2];
            for (String level_one_string : level_1) {
                for (int del = 0; del < s.length() - 1; del++) {
                    int k = 0;
                    for (int i = 0; i < s.length() - 1; i++) {
                        if (i != del) arr[k++] = level_one_string.charAt(i);
                    }
                    level_2.add(String.valueOf(arr));
                }
            }
        }
        dict_0.put(s, freq);
        for(String level_1_string : level_1) {
            if(!dict_1.containsKey(level_1_string)) dict_1.put(level_1_string, new HashSet<>());
            dict_1.get(level_1_string).add(s);
        }
        for(String level_2_string : level_2) {
            if(!dict_2.containsKey(level_2_string)) dict_2.put(level_2_string, new HashSet<>());
            dict_2.get(level_2_string).add(s);
        }
    }

    /**
     *
     * @param trigram_file The file containing the bigram
     */
    private void constructTrigram(File trigram_file) {
        /* getting lines from the file */
        try {
            System.out.println(0);

            BufferedReader in = new BufferedReader(new FileReader(trigram_file));
            String line;
            int count = 0;
            while((line = in.readLine()) != null) {
                /* each line will be of the form <word word word freq> */
                String[] arr = line.split(" ");
                String word_1 = arr[0].toLowerCase().trim();
                String word_2 = arr[1].toLowerCase().trim();
                String word_3 = arr[2].toLowerCase().trim();
                long freq = Long.parseLong(arr[3].trim());

                if(!trigram.containsKey(word_1)) {
                    trigram.put(word_1, new HashMap<>());
                }
                if(!trigram.get(word_1).containsKey(word_2)) {
                    trigram.get(word_1).put(word_2, new HashMap<>());
                }

                Long f = trigram.get(word_1).get(word_2).get(word_3);
                if(f == null)   f = (long)0;
                trigram.get(word_1).get(word_2).put(word_3, f + freq);

                count ++;
                if(count == 5000000) {
                    break;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void constructBigram(File bigram_file) {
        /* getting lines from the file */
        try {
            System.out.println(0);

            BufferedReader in = new BufferedReader(new FileReader(bigram_file));
            String line;
            int count = 0;
            while((line = in.readLine()) != null) {
                /* each line will be of the form <word word freq> */
                String[] arr = line.split(" ");
                String word_1 = arr[0].toLowerCase().trim();
                String word_2 = arr[1].toLowerCase().trim();
                long freq = Long.parseLong(arr[2].trim());

                if(!bigram.containsKey(word_1)) {
                    bigram.put(word_1, new HashMap<>());
                }

                Long f = bigram.get(word_1).get(word_2);
                if(f == null)   f = (long)0;
                bigram.get(word_1).put(word_2, f + freq);

                count ++;
                if(count == 5000000) {
                    break;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reconstructs a String at a given level to a word in the unigram
     * @param s The string to reconstruct
     * @param right_level the level from which the string has to be reconstructed from
     * @return The reconstructed string
     */
    public Set<String> reconstruct(String s, int right_level) {
        if(useClient) {
            return client.reconstruct(s, right_level);
        }

        Set<String> res = new HashSet<>();
        if(right_level == 0)    {
            if(dict_0.containsKey(s))   res.add(s);
            return res;
        }
        Map<String, Set<String>> map;
        if(right_level == 1)   map = dict_1;
        else   map = dict_2;
        if(map.containsKey(s))  res.addAll(map.get(s));
        return res;
    }

    /**
     * Compares the phrases a b and c d and returns whether a b should be replaced by c d
     * @param a String a
     * @param b String b
     * @param c String c
     * @param d  String d
     * @return Whether a b should be replaced by c d
     */
    public boolean shouldChange(String a, String b, String c, String d) {
        if((a + b).equals(c))  return true;

        long count_1 = getFreq(a);
        long count_2 = getFreq(b);
        long count_3 = getFreq(c);
        long count_4 = getFreq(d);

        long count_1_2 = getFreq(a, b);
        long count_3_4 = getFreq(c, d);

        //System.out.println("count_1: " + count_1 + " count_2: " + count_2 + " count_1_2: " + count_1_2);
        //System.out.println("count_3: " + count_3 + " count_4: " + count_4 + " count_3_4: " + count_3_4);

        if(d.equals("")) {
            return true;
        }

        else if(count_1_2 == 0) {
            if(count_3_4 == 0) {
                return count_3 >= count_1;
            }
            else {
                return true;
            }
        }
        else if(count_3_4 == 0) return false;

        else {
            return count_3_4 >= count_1_2;
        }
    }

    /**
     * Calculates (log of) conditional probability of a pair of words (in order)
     * @param a String a
     * @param b String b
     * @return log(p(a then b) / p(a))
     */
    public double getConditionalLog(String a, String b) {
        long freq_a = getFreq(a) + 1;
        long freq_b = getFreq(b) + 1;
        long freq_a_b = getFreq(a, b) + 1;

        System.out.println("Count of " + a + " " + b + " is: " + freq_a_b);
        System.out.println("Count of " + a + " is: " + freq_a);

        if(freq_a_b == 1) {
            return -25;
        }

        return Math.log((double)freq_a_b / freq_a);
    }

    /**
     * Calculates (log of) conditional probability of a triplet of words (in order)
     * @param third String a
     * @param second String b
     * @param first String c
     * @return log(p(a | c b)) ie log(prob of third given first second)
     */
    public double getConditionalLog(String first, String second, String third) {
        long numerator = getFreq(first, second, third);
        long denominator = getFreq(first, second);

        System.out.println("count of " + first + " " + second + " " + third + " is: " + numerator);
        System.out.println("count of " + first + " " + second + " is: " + denominator);

        if(denominator == 0) {
            return -50;
        }
        if(numerator == 0) {
            return getConditionalLog(first, second) + getConditionalLog(second, third);
        }
        return Math.log((double)numerator / denominator);

    }

    public static void main(String[] args) {
        getInstance(
                new File("C:\\Users\\WELCOME\\Desktop\\SymSpell\\SpellCheckProject\\src\\freq_dict_80k.txt"),
                new File("C:\\Users\\WELCOME\\Desktop\\SymSpell\\SpellCheckProject\\src\\Bigram_10M_2.txt"));

        try {
            Thread.sleep((long)1000000000 * 1000000000);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}

