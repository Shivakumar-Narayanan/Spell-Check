package Main;

public class SuggestionItem implements Comparable<SuggestionItem> {

    private final String s;
    private final int dist;
    private final int lcS;
    private final int lcp;
    private final int lcs;
    private final long freq;

    /**
     * Create a new Suggestion Item with the given distance measures
     * @param s The suggested string
     * @param dist The Edit Distance between the suggested string and the input string
     * @param lcS The Longest Common Subsequence between the suggested string and the input string
     * @param lcp The Longest Common Prefix between the suggested string and the input string
     * @param lcs The Longest Common Suffix between the suggested string and the input string
     * @param freq The frequency of occurrence of the suggested string in the unigram
     */
    SuggestionItem(String s, int dist, int lcS, int lcp, int lcs, long freq) {
        this.s = s;
        this.dist = dist;
        this.lcS = lcS;
        this.lcp = lcp;
        this.lcs = lcs;
        this.freq = freq;
    }

    @Override
    public int compareTo(SuggestionItem other) {
        if(this.dist < other.dist)  return -1;
        if(this.dist > other.dist)  return 1;

        int sum1 = this.lcp + this.lcs + this.lcS + (int)Math.log(this.freq);
        int sum2 = other.lcp + other.lcs + other.lcS + (int)Math.log(other.freq);;
        if(sum1 > sum2) return -1;
        if(sum1 < sum2) return 1;

        //if(this.lcS > other.lcS)    return -1;
        //if(this.lcS < other.lcS)    return 1;

        if(this.freq > other.freq)  return -1;
        return 1;
    }

    @Override
    public String toString() {
        return s;
                /*" dist: " + dist +
                " LCS: " + lcS +
                " lcp: " + lcp +
                " lcs: " + lcs;*/
    }

    public String getWord() {
        return s;
    }

    public int getDist() {
        return dist;
    }

}
