package Main;

import java.net.ConnectException;
import java.util.*;
import java.io.*;

public class SpellChecker
{
    private final Corpus corpus;
    private final DistanceCalculator distance_calculator;

    /**
     * The unigram has to be in the form <String freq>
     * @param uniGram The File Containing the unigram
     */
    SpellChecker(File uniGram) throws IOException
    {
        if(!uniGram.exists())   throw new IOException("UniGram File Doesn't Exist");

        corpus = new Corpus(uniGram);
        distance_calculator = new DistanceCalculator(1, 1, 1, 1);
    }

    /**
     * The unigram has to be in the form <String freq> anf the bigram <String String freq>
     * @param uniGram The File Containing the unigram as <String freq>
     * @param biGram The File Containing the bigram as <String String freq>
     */
    public SpellChecker(File uniGram, File biGram) throws  IOException
    {
        if(!uniGram.exists())   throw new IOException("UniGram File Doesn't Exist");
        if(!biGram.exists())   throw new IOException("BiGram File Doesn't Exist");

        corpus = Corpus.getInstance(uniGram, biGram);
        distance_calculator = new DistanceCalculator(1, 1, 1, 1);
    }

    public SpellChecker() throws ConnectException {
        corpus = new Corpus();
        distance_calculator = new DistanceCalculator(1, 1, 1, 1);
    }

    /**
     * Constructs a spellcheck object from a corpus object
     * @param corpus the corpus object
     */
    SpellChecker(Corpus corpus) {
        this.corpus = corpus;
        distance_calculator = new DistanceCalculator(1, 1, 1, 1);
    }

    /**
     * returns a single word as a prediction after possible correcting spelling errors
     * @param word The word that is possibly mis spelt
     * @return  The Top Prediction
     */
    public String getTopPredictionSingle(String word) {
        if(corpus.special_words.containsKey(word)) {
            return corpus.special_words.get(word);
        }
        Set<SuggestionItem> suggestion_set = getPredictionSet(word, 5);
        for(SuggestionItem item : suggestion_set) {
            return item.getWord();
        }
        return "";
    }

    /**
     *
     * @param word
     * @param prev
     * @return
     */
    public String getTopPredictionSingleWithPrev(String word, String prev) {
        if(corpus.special_words.containsKey(word)) {
            return corpus.special_words.get(word);
        }
        Set<SuggestionItem> suggestion_set = getPredictionSet(word, 5);
        long max_freq = -1;
        String best_word = "";
        for(SuggestionItem item : suggestion_set) {
            String wrd = item.getWord();
            long freq = corpus.getFreq(prev, wrd);
            if(max_freq < freq) {
                max_freq = freq;
                best_word = wrd;
            }
        }
        return best_word;
    }

    /**
     *
     * @param word
     * @param next
     * @return
     */
    public String getTopPredictionSingleWithNext(String word, String next) {
        if(corpus.special_words.containsKey(word)) {
            return corpus.special_words.get(word);
        }
        Set<SuggestionItem> suggestion_set = getPredictionSet(word, 5);
        long max_freq = -1;
        String best_word = "";
        for(SuggestionItem item : suggestion_set) {
            String wrd = item.getWord();
            long freq = corpus.getFreq(wrd, next);
            if(max_freq < freq) {
                max_freq = freq;
                best_word = wrd;
            }
        }
        return best_word;
    }

    public String[] getBestPair(String word_1, String word_2) {
        if(isValidWord(word_1) && isValidWord(word_2)) {
            return new String[]{word_1, word_2};
        }
        if(isValidWord(word_2)) {
            return new String[]{getTopPredictionSingleWithNext(word_1, word_2), word_2};
        }
        if(isValidWord(word_1)) {
            return new String[]{word_1, getTopPredictionSingleWithPrev(word_2, word_1)};
        }

        Set<SuggestionItem> suggestion_set_1 = getPredictionSet(word_1, 5);
        Set<SuggestionItem> suggestion_set_2 = getPredictionSet(word_2, 5);

        long max_freq = -1;
        String best_word_1 = "";
        String best_word_2 = "";
        for(SuggestionItem item_1 : suggestion_set_1) {
            String w1 = item_1.getWord();
            for(SuggestionItem item_2 : suggestion_set_2) {
                String w2 = item_2.getWord();
                long freq = corpus.getFreq(w1, w2);
                if(freq > max_freq) {
                    max_freq = freq;
                    best_word_1 = w1;
                    best_word_2 = w2;
                }
            }
        }
        return new String[]{best_word_1, best_word_2};
    }

    /**
     * Returns the top k predictions after correcting spelling errors. If there aren't k predictions, all are returned
     * @param word The word that is possibly mis spelt
     * @param k The number of results to return
     * @return The Top K Predictions in order
     */
    public List<String> getTopK(String word, int k) {
        Set<SuggestionItem> suggestion_set = getPredictionSet(word, k);
        List<String> res = new ArrayList<>();
        for(SuggestionItem item : suggestion_set) {
            if(k-- == 0)  break;
            res.add(item.getWord());
        }
        return res;
    }

    /**
     * Returns the top prediction after correcting spelling errors, including missing a single space between two words
     * @param word The word that is possible mis spelt and possibly contains 2 words without spaces in between
     * @return The word(s) after correcting the spelling mistake. The word may have been split
     */
    public List<String> getTopPredictionSplit(String word) {
        List<String> res = new ArrayList<>();
        int[] dp_1 = new int[word.length() + 1];
        int[] dp_2 = new int[word.length() + 1];
        Arrays.fill(dp_1, 999);
        dp_1[word.length()] = 0;
        dp_2[word.length()] = word.length();

        for(int start = word.length() - 1; start >= 0; start --) {
            /* end is not inclusive */
            for(int end = start  + 1; end <= word.length() && (end - start <= 10); end ++) {
                String split = word.substring(start, end);
                Set<SuggestionItem> items = getPredictionSet(split, 1);
                int dist = 999;
                for(SuggestionItem item : items) {
                    //System.out.println("word: " + item.getWord() + " dist: " + item.getDist());
                    dist = item.getDist();
                    break;
                }
                if(dp_1[start] == dist + dp_1[end]) {
                    if(dp_1[start] != 0)    continue;

                    String old_1 = word.substring(start, dp_2[start]);
                    String old_2 = dp_2[start] == word.length() ? "" : word.substring(dp_2[start], dp_2[dp_2[start]]);

                    String new_1 = word.substring(start, end);
                    String new_2 = end == word.length() ? "" : word.substring(end, dp_2[end]);

                    if(corpus.shouldChange(old_1, old_2, new_1, new_2)) {
                        dp_1[start] = dist + dp_1[end];
                        dp_2[start] = end;
                    }

                    System.out.println("Conflict...");
                    System.out.println(old_1 + " " + old_2);
                    System.out.println(new_1 + " " + new_2);
                    System.out.println();
                }
                if(dp_1[start] > dist + dp_1[end]) {
                    dp_1[start] = dist + dp_1[end];
                    dp_2[start] = end;
                }
            }
        }

        /* reconstructing */
        int start = 0;
        int end = dp_2[0];
        while(start != end) {
            res.add(word.substring(start, end));
            start = end;
            end = dp_2[start];
        }
        System.out.println("Sum of dist: " + dp_1[0]);
        return res;
    }

    /**
     * Returns the top k predictions after possibly splitting the word because of a missing space in between
     * @param word The word that is possible mis spelt and possibly contains 2 words without spaces in between
     * @param k The number of results to return
     * @return The List of word(s) after correcting the spelling mistake. The word may have been split
     */
    public List<List<String>> getTopKSplit(String word, int k) {
        return null;
    }

    public double getScore(List<String> sentence) {
        double score = 0;
        for(int i = 0; i < sentence.size() - 1; i++) {
            int j = i + 1;
            score += corpus.getConditionalLog(sentence.get(i), sentence.get(j));
        }

        System.out.println("Score: " + score);
        return score;
    }

    /**
     * Returns the sentence after correcting spelling errors. Uses a bigram to predict context level corrections
     * @param sentence The list of words in the sentence, last word may contain full stop
     * @return The corrected sentence
     */
    public List<String> predictSentence(List<String> sentence) {
        /**
         * Step 1: get a list of candidate words for each word in the sentence
                * Possible cases :
                    * correct word, length <= 3: use as it is (CASE 1A)
                    * correct word, length > 3: get level 1 predictions alone (CASE 1B)

                    * incorrect word, length <= 3: get level 1 predictions alone (CASE 2A)
                    * incorrect word, length > 3: get first 10, if still at level 1, get 20 (CASE 2B)
         */

        List<List<String>> candidates = new ArrayList<>();
        /* dp arrays are created right away to mimic the structure of the candidate list */
        List<List<Double>> dp_score = new ArrayList<>();
        List<List<Integer>> dp_recon = new ArrayList<>();

        for(int i = 0; i < sentence.size(); i++) {
            candidates.add(new ArrayList<>());
            dp_score.add(new ArrayList<>());
            dp_recon.add(new ArrayList<>());
            String word = sentence.get(i);

            Set<SuggestionItem> set = new TreeSet<>();

            /* CASE 1 */
            if(corpus.getFreq(word) != 0) {
                /* 1A */
                if(word.length() <= 3);

                /* 1B */
                //else {
                    //set = getPredictionSet(word, 10, 1);
                //}
            }

            /* CASE 2 */
            else {
                /* 2A */
                if(word.length() <= 3) {
                    set = getPredictionSet(word, 10, 3);
                }

                /* 2B */
                else if(word.length() <= 5){
                    set = getPredictionSet(word, 10, 4);
                }
                else {
                    set = getPredictionSet(word, 10, 4);
                }
            }

            candidates.get(i).add(word);
            dp_score.get(i).add(i == sentence.size() - 1 ? Math.log(corpus.getFreq(word) + 1) : 0);
            dp_recon.get(i).add(i == sentence.size() - 1 ? -1 : 0);
            for(SuggestionItem item : set) {
                candidates.get(i).add(item.getWord());
                dp_score.get(i).add(i == sentence.size() - 1 ? Math.log(corpus.getFreq(item.getWord()) + 1) : 0);
                dp_recon.get(i).add(i == sentence.size() - 1 ? -1 : 0);
            }
        }

        for(List<String> list : candidates) {
            System.out.println(list);
        }

        /**
         * STEP 2: Construct the dp array and set all the decision pointers (recon pointers)
         *
         */
        int best_start_index = 0;
        double best_overall_score = Integer.MIN_VALUE;
        String best_start_string = "";
        for(int i = sentence.size() - 2; i >= 0; i--) {
            List<String> cur_candidate_list = candidates.get(i);
            List<String> next_candidate_list = candidates.get(i + 1);
            for(int j = 0; j < cur_candidate_list.size(); j++) {
                String cur = cur_candidate_list.get(j);
                double max_score = Integer.MIN_VALUE;
                String max_score_string = "";
                for(int k = 0; k < next_candidate_list.size(); k++) {
                    String next = next_candidate_list.get(k);

                    double score = corpus.getConditionalLog(cur, next);
                    System.out.println("cur: " + cur + " next: " + next + " raw score: " + score);
                    score += dp_score.get(i + 1).get(k);
                    System.out.println("cur: " + cur + " next: " + next + " score: " + score);

                    /*if(score == max_score) {
                        if(corpus.getFreq(next) > corpus.getFreq(max_score_string)) {
                            dp_score.get(i).set(j, score);
                            dp_recon.get(i).set(j, k);
                            max_score_string = next;
                        }
                    }*/

                    if(score > max_score) {
                        max_score = score;
                        dp_score.get(i).set(j, score);
                        dp_recon.get(i).set(j, k);
                    }
                }
                if(i == 0 && max_score == best_overall_score) {
                    if(corpus.getFreq(best_start_string) < corpus.getFreq(cur)) {
                        best_start_string = cur;
                        best_start_index = j;
                    }
                }
                if(i == 0 && max_score > best_overall_score) {
                    best_overall_score = max_score;
                    best_start_index = j;
                }
            }
        }

        /**
         * STEP 3: Reconstructing the best sentence
         */
        List<String> res = new ArrayList<>();
        int i = 0;
        while(best_start_index != -1) {
            res.add(candidates.get(i).get(best_start_index));
            best_start_index = dp_recon.get(i).get(best_start_index);
            i ++;
        }

        System.out.print("Prediction: ");
        System.out.println(res);
        System.out.println("score: " + best_overall_score);

        return res;
    }

    /**
     * Returns the sentence after correcting spelling errors. Uses a trigram to predict context level corrections
     * @param sentence The list of words in the sentence, last word may contain full stop
     * @return The corrected sentence
     */
    public List<String> predictSentenceTest(List<String> sentence) {
        /**
         * Step 1: get a list of candidate words for each word in the sentence
         * Possible cases :
         * correct word, length <= 3: use as it is (CASE 1A)
         * correct word, length > 3: get level 1 predictions alone (CASE 1B)

         * incorrect word, length <= 3: get level 1 predictions alone (CASE 2A)
         * incorrect word, length > 3: get first 10, if still at level 1, get 20 (CASE 2B)
         */

        List<List<String>> candidates = new ArrayList<>();

        for(int i = 0; i < sentence.size(); i++) {
            candidates.add(new ArrayList<>());
            String word = sentence.get(i);

            Set<SuggestionItem> set = new TreeSet<>();

            /* CASE 1 */
            if(corpus.getFreq(word) != 0) {
                /* 1A */
                if(word.length() <= 3);

                /* 1B */
                //else {
                //set = getPredictionSet(word, 10, 1);
                //}
            }

            /* CASE 2 */
            else {
                /* 2A */
                if(word.length() <= 3) {
                    set = getPredictionSet(word, 10, 3);
                }

                /* 2B */
                else if(word.length() <= 5){
                    set = getPredictionSet(word, 10, 4);
                }
                else {
                    set = getPredictionSet(word, 10, 4);
                }
            }

            candidates.get(i).add(word);
            for(SuggestionItem item : set) {
                candidates.get(i).add(item.getWord());
            }
        }

        for(List<String> list : candidates) {
            System.out.println(list);
        }

        /**
         * STEP 2: Construct the dp array and set all the decision pointers (recon pointers)
         *
         */
        /* dp[word_no][option_no][option_no_of_prev_word] */
        double[][][] dp_score = new double[candidates.size() + 1][20][20];
        int[][][] dp_recon = new int[candidates.size() + 1][20][20];

        for(double[][] arr_1 : dp_score) {
            for(double[] arr_2 : arr_1) {
                Arrays.fill(arr_2, (double)Integer.MIN_VALUE);
            }
        }

        /*  base case for word_no = 0 */
        for(double[] arr : dp_score[0]) {
            Arrays.fill(arr, 0.0);
        }

        /* base case for word_no = 1 */
        for(int i = 0; i < candidates.get(1).size(); i++) {
            String cur = candidates.get(1).get(i);
            for(int j = 0; j < candidates.get(0).size(); j++) {
                String prev = candidates.get(0).get(j);
                dp_score[1][i][j] = corpus.getConditionalLog(prev, cur);
            }
        }

        for(int word_no = 2; word_no < candidates.size(); word_no++) {
            for(int cur_option = 0; cur_option < candidates.get(word_no).size(); cur_option ++) {
                String cur = candidates.get(word_no).get(cur_option);
                for(int prev_option_1 = 0; prev_option_1 < candidates.get(word_no - 1).size(); prev_option_1 ++) {
                    String prev_1 = candidates.get(word_no - 1).get(prev_option_1);
                    double max_score = (double)Integer.MIN_VALUE;
                    int max_score_option_index = -1;
                    for(int prev_option_2 = 0; prev_option_2 < candidates.get(word_no - 2).size(); prev_option_2 ++) {
                        String prev_2 = candidates.get(word_no - 2).get(prev_option_2);
                        //System.out.println("Cur: " + cur + " prev_1: " + prev_1 + " prev_2: " + prev_2);
                        double score = corpus.getConditionalLog(prev_2, prev_1, cur);
                        //System.out.println("raw score: " + score);
                        score += dp_score[word_no - 1][prev_option_1][prev_option_2];
                        //System.out.println("score: " + score);

                        if(score > max_score) {
                            max_score = score;
                            max_score_option_index = prev_option_2;
                        }
                    }

                    dp_score[word_no][cur_option][prev_option_1] = max_score;
                    dp_recon[word_no][cur_option][prev_option_1] = max_score_option_index;
                }
            }
        }

        /**
         * STEP 3: Reconstructing the best sentence
         */
        int word_no = candidates.size() - 1;
        int option = -1;
        int prev_option_1 = -1;
        int prev_option_2 = -1;
        double global_max = (double)(Integer.MIN_VALUE);
        for(int i = 0; i < candidates.get(candidates.size() - 1).size(); i++) {
            for(int j = 0; j < candidates.get(candidates.size() - 2).size(); j++) {
                if(dp_score[candidates.size() - 1][i][j] > global_max) {
                    global_max = dp_score[candidates.size() - 1][i][j];
                    option = i;
                    prev_option_1 = j;
                }
            }
        }
        Stack<String> stack = new Stack<>();
        prev_option_2 = dp_recon[candidates.size() - 1][option][prev_option_1];
        while(true) {
            if(word_no == 2) {
                stack.push(candidates.get(2).get(option));
                stack.push(candidates.get(1).get(prev_option_1));
                stack.push(candidates.get(0).get(prev_option_2));
                break;
            }

            stack.push(candidates.get(word_no).get(option));

            word_no --;
            option = prev_option_1;
            prev_option_1 = prev_option_2;
            prev_option_2 = dp_recon[word_no][option][prev_option_1];
        }

        List<String> res = new ArrayList<>();
        while(!stack.isEmpty()) {
            res.add(stack.pop());
        }
        System.out.println("Prediction: " + res);
        System.out.println("Score: " + global_max);
        return res;
    }

    /**
     *
     * @param s The string s which is to be corrected
     * @param NUM_SUGGESTIONS The minimum number of suggestions to return
     * @return A sorted set of suggestion items which contains at least num_suggestions, if possible or as many if not
     */
    private Set<SuggestionItem> getPredictionSet(String s, int NUM_SUGGESTIONS) {

        Set<String> res = new HashSet<>();

        /* Generating level 1 deletions */
        Set<String> level_1 = new HashSet<>();

        char[] arr = new char[s.length() - 1];
        for(int del = 0; del < s.length(); del++) {
            int k = 0;
            for(int i = 0; i < s.length(); i++) {
                if(i != del)    arr[k ++] = s.charAt(i);
            }
            level_1.add(String.valueOf(arr));
        }

        /*generating level 2 deletions */

        Set<String> level_2 = new HashSet<>();
        if(s.length() > 1) {
            arr = new char[s.length() - 2];
            for(String level_one_string : level_1) {
                for(int del = 0; del < s.length() - 1; del++) {
                    int k = 0;
                    for(int i = 0; i < s.length() - 1; i++) {
                        if(i != del)    arr[k ++] = level_one_string.charAt(i);
                    }
                    level_2.add(String.valueOf(arr));
                }
            }
        }

        /* trying different levels of reconstruction: */
        /* 00  01 10 11*/
        res.addAll(corpus.reconstruct(s, 0));
        res.addAll(corpus.reconstruct(s, 1));
        for(String level_1_string : level_1) {
            res.addAll(corpus.reconstruct(level_1_string, 0));
            res.addAll(corpus.reconstruct(level_1_string, 1));
        }

        /* 02 20 */
        res.addAll(corpus.reconstruct(s, 2));
        for(String level_2_string : level_2) {
            res.addAll(corpus.reconstruct(level_2_string, 0));
        }

        /* 12 21 */
        if(res.size() < NUM_SUGGESTIONS) {
            for(String level_1_string : level_1) {
                res.addAll(corpus.reconstruct(level_1_string, 2));
            }
            for(String level_2_string : level_2) {
                res.addAll(corpus.reconstruct(level_2_string, 1));
            }
        }

        /* 22 */
        if(res.size() < NUM_SUGGESTIONS) {
            for(String level_2_string : level_2) {
                res.addAll(corpus.reconstruct(level_2_string, 2));
            }
        }

        Set<SuggestionItem> set = new TreeSet<>();

        for(String string : res) {
            SuggestionItem item = new SuggestionItem(string,
                    distance_calculator.getDistance(s, string),
                    distance_calculator.getLcs(s, string),
                    distance_calculator.getLongestCommonPrefix(s, string),
                    distance_calculator.getLongestCommonSuffix(s, string),
                    corpus.getFreq(string)
            );
            set.add(item);
        }
        Set<SuggestionItem> filtered_set = new TreeSet<>();
        int count = 0;
        for(SuggestionItem item : set) {
            if(count >= NUM_SUGGESTIONS) break;
            count ++;
            filtered_set.add(item);
        }

        return filtered_set;
    }

    /**
     *
     * @param s The string s which is to be corrected
     * @param NUM_SUGGESTIONS The minimum number of suggestions to return
     * @param max_level The max edit distance to be checked
     * @return A sorted set of suggestion items which contains at least num_suggestions, if possible or as many if not
     */
    private Set<SuggestionItem> getPredictionSet(String s, int NUM_SUGGESTIONS, int max_level) {

        Set<String> res = new HashSet<>();

        /* Generating level 1 deletions */
        Set<String> level_1 = new HashSet<>();

        char[] arr = new char[s.length() - 1];
        for(int del = 0; del < s.length(); del++) {
            int k = 0;
            for(int i = 0; i < s.length(); i++) {
                if(i != del)    arr[k ++] = s.charAt(i);
            }
            level_1.add(String.valueOf(arr));
        }

        /*generating level 2 deletions */

        Set<String> level_2 = new HashSet<>();
        if(s.length() > 1) {
            arr = new char[s.length() - 2];
            for(String level_one_string : level_1) {
                for(int del = 0; del < s.length() - 1; del++) {
                    int k = 0;
                    for(int i = 0; i < s.length() - 1; i++) {
                        if(i != del)    arr[k ++] = level_one_string.charAt(i);
                    }
                    level_2.add(String.valueOf(arr));
                }
            }
        }

        /* trying different levels of reconstruction: */
        /* 00  01 10 11*/
        res.addAll(corpus.reconstruct(s, 0));
        res.addAll(corpus.reconstruct(s, 1));
        for(String level_1_string : level_1) {
            res.addAll(corpus.reconstruct(level_1_string, 0));
            res.addAll(corpus.reconstruct(level_1_string, 1));
        }

        /* 02 20 */
        res.addAll(corpus.reconstruct(s, 2));
        for(String level_2_string : level_2) {
            res.addAll(corpus.reconstruct(level_2_string, 0));
        }

        /* 12 21 */
        if(res.size() < NUM_SUGGESTIONS) {
            for(String level_1_string : level_1) {
                res.addAll(corpus.reconstruct(level_1_string, 2));
            }
            for(String level_2_string : level_2) {
                res.addAll(corpus.reconstruct(level_2_string, 1));
            }
        }

        /* 22 */
        if(res.size() < NUM_SUGGESTIONS) {
            for(String level_2_string : level_2) {
                res.addAll(corpus.reconstruct(level_2_string, 2));
            }
        }

        Set<SuggestionItem> set = new TreeSet<>();

        for(String string : res) {
            SuggestionItem item = new SuggestionItem(string,
                    distance_calculator.getDistance(s, string),
                    distance_calculator.getLcs(s, string),
                    distance_calculator.getLongestCommonPrefix(s, string),
                    distance_calculator.getLongestCommonSuffix(s, string),
                    corpus.getFreq(string)
            );
            set.add(item);
        }
        Set<SuggestionItem> filtered_set = new TreeSet<>();
        int count = 0;
        for(SuggestionItem item : set) {
            if(count >= NUM_SUGGESTIONS || item.getDist() > max_level) break;
            count ++;
            filtered_set.add(item);
        }

        return filtered_set;
    }

    public boolean isValidWord(String word) {
        return corpus.getFreq(word) != 0;
    }

    public double getProb(List<String> sentence) {
        double res = 0.0;
        for(int i = 0; i < sentence.size() - 2; i++) {
            res += corpus.getConditionalLog(sentence.get(i), sentence.get(i + 1), sentence.get(i + 2));
        }
        return res;
    }

    public Corpus getCorpus() {
        return corpus;
    }
}
