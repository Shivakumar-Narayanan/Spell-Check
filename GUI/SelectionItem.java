package GUI;
import Main.SpellChecker;

import java.util.*;
public class SelectionItem {
    SpellChecker sp;
    String word;
    List<String> suggestions;
    int ptr;
    String prev;
    boolean disturbed;

    private class FreqComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            long freq_1, freq_2;
            if(prev.equals("")) {
                freq_1 = sp.getCorpus().getFreq(o1);
                freq_2 = sp.getCorpus().getFreq(o2);
            }
            else {
                freq_1 = sp.getCorpus().getFreq(prev, o1);
                freq_2 = sp.getCorpus().getFreq(prev, o2);
            }
            if(freq_1 >= freq_2) {
                return -1;
            }
            return 1;
        }
    }

    SelectionItem(String word, String prev, SpellChecker sp) {
        this.word = word;
        this.prev = prev;
        ptr = 0;
        disturbed = false;
        this.sp = sp;
        suggestions = sp.getTopK(word, 15);
        if(sp.isValidWord(prev) || prev.equals("")) {
            sort();
        }
    }

    public void cycleForward() {
        if(disturbed) {
            ptr = -1;
        }
        disturbed = false;
        ptr = (ptr + 1) % suggestions.size();
    }

    public void cycleBackward() {
        if(disturbed) {
            ptr = 1;
        }
        disturbed = false;
        ptr = (ptr - 1);
        if(ptr == -1) {
            ptr = suggestions.size() - 1;
        }
    }

    public String getSelection() {
        if(disturbed) {
            ptr = 0;
        }
        return suggestions.get(ptr);
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public void sort() {
        disturbed = true;
        Collections.sort(suggestions, new FreqComparator());
    }
}
