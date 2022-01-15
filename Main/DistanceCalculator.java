package Main;

public class DistanceCalculator {
    private final int INSERT_COST;
    private final int DELETE_COST;
    private final int TRANSPOSE_COST;
    private final int REPLACE_COST;

    /**
     * Creates a new Distance calculator object with the costs associated with edits
     * @param insert_cost cost to insert a character
     * @param delete_cost cost to delete a character
     * @param transpose_cost cost to swap adjacent character
     * @param replace_cost cost to replace a character with another
     */
    DistanceCalculator(int insert_cost, int delete_cost, int transpose_cost, int replace_cost) {
        INSERT_COST = insert_cost;
        DELETE_COST = delete_cost;
        TRANSPOSE_COST = transpose_cost;
        REPLACE_COST = replace_cost;
    }

    private static int minOfFour(int a, int b, int c, int d) {
        return Math.min(a, Math.min(b, Math.min(c, d)));
    }
    /**
     * Get the Edit distance between words a and b
     * @param a word1
     * @param b word2
     * @return the distance to get from word1 to word2
     */
    public int getDistance(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        // Fill d[][] in bottom up manner
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                // If first string is empty, only option is
                // to insert all characters of second string
                if (i == 0) dp[i][j] = j; // Min. operations = j
                    // If second string is empty, only option is
                    // to remove all characters of second string
                else if (j == 0) dp[i][j] = i; // Min. operations = i
                    // If last characters are same, ignore last
                    // char and recur for remaining string
                else if (a.charAt(i - 1) == b.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
                else {
                    int insert = INSERT_COST + dp[i][j - 1];
                    int remove = DELETE_COST + dp[i - 1][j];
                    int replace = REPLACE_COST + dp[i - 1][j - 1];
                    int transpose = Integer.MAX_VALUE;

                    //trying to transpose(swap adjacent characters)
                    if (i > 1 && j > 1) {
                        if (a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(j - 1)) {
                            transpose = TRANSPOSE_COST + dp[i - 2][j - 2];
                        }
                    }

                    dp[i][j] = minOfFour(insert, remove, replace, transpose);
                }
            }
        }
        return dp[n][m];
    }

    /**
     * Get the length of the Longest common subsequence
     * @param a word 1
     * @param b word 2
     * @return the LCS(word1, word2)
     */
    public int getLcs(String a, String b) {
        int n = a.length();
        int m = b.length();

        int[][] dp = new int[n + 1][m + 1];
        for(int i = 1; i <= n; i++)
        {
            for(int j = 1; j <= m; j++)
            {
                if(a.charAt(i -  1) == b.charAt(j - 1)) dp[i][j] = 1 + dp[i - 1][j - 1];
                else    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }

        return dp[n][m];
    }

    /**
     * Get the length of the Longest common prefix
     * @param a word 1
     * @param b word 2
     * @return The length of the Longest common prefix
     */
    public int getLongestCommonPrefix(String a, String b) {
        int i = 0;
        int j = 0;
        while(i < a.length() && j < b.length() && a.charAt(i) == b.charAt(j)) {
            i ++;
            j ++;
        }
        return i;
    }

    /**
     * Get the length of the Longest common suffix
     * @param a word 1
     * @param b word 2
     * @return The length of the Longest common suffix
     */
    public int getLongestCommonSuffix(String a, String b) {
        int i = a.length() - 1;
        int j = b.length() - 1;
        int count = 0;
        while(i >= 0 && j >= 0 && a.charAt(i) == b.charAt(j)) {
            i --;
            j --;
            count  ++;
        }
        return count;
    }
}


