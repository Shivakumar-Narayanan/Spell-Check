package GUI;
import Main.SpellChecker;

import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.ConnectException;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLDocument;
public class BenchMarkGUI extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = (2 * WIDTH) / 3;

    JFrame parent;
    JPanel outerPanel;
    JLabel heading;
    JLabel SymSpell, Lucene;
    JLabel words_encountered, words_misspelt, words_corrected, words_corrected_correctly, accuracy;
    JLabel time_taken, time_per_word, time_per_correction;
    BenchMarkGUI(JFrame parent) {
        super("BenchMark");
        this.parent = parent;
        parent.setVisible(false);
        setSize(new Dimension(WIDTH, HEIGHT));
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        //outer panel
        outerPanel = new JPanel();
        outerPanel.setLayout(null);
        outerPanel.setBorder(new TitledBorder(" "));
        outerPanel.setBackground(Color.BLACK);

        //heading
        String s1 = "<font color='FF3333'>Benchmark </font>";
        heading = new JLabel("<html><p style=\"font-size:30px\">" + s1 + "</p></html>");
        heading.setBounds((WIDTH * 9) / 24, 10, (2 * WIDTH) / 3, 100);
        heading.setSize(new Dimension((2 * WIDTH) / 3, 100));

        //Labels
        SymSpell = new JLabel("<html><p style=\"font-size:14px\"><font color='#ADD8E6'>SymSpell</font></p></html>");
        Lucene = new JLabel("<html><p style=\"font-size:14px\"><font color='#ADD8E6'>Lucene</font></p></html>");
        words_encountered = new JLabel("<html><p style=\"font-size:12px\"><font color='#ADD8E6'>Words_Encountered</font></p></html>");
        words_misspelt = new JLabel("<html><p style=\"font-size:12px\"><font color='#ADD8E6'>Words_Misspelt</font></p></html>");
        words_corrected = new JLabel("<html><p style=\"font-size:12px\"><font color='#ADD8E6'>Words_Corrected</font></p></html>");
        words_corrected_correctly = new JLabel("<html><p style=\"font-size:12px\"><font color='#ADD8E6'>Correct_Predictions</font></p></html>");
        accuracy = new JLabel("<html><p style=\"font-size:12px\"><font color='#ADD8E6'>Accuracy</font></p></html>");
        time_taken = new JLabel("<html><p style=\"font-size:12px\"><font color='#ADD8E6'>Time_Taken(sec)</font></p></html>");
        time_per_word = new JLabel("<html><p style=\"font-size:12px\"><font color='#ADD8E6'>Time_Per_Word(ms)</font></p></html>");
        time_per_correction = new JLabel("<html><p style=\"font-size:12px\"><font color='#ADD8E6'>Time_Per_Correction(ms)</font></p></html>");;

        int w = 200;
        int h = 30;
        int base = 30;
        SymSpell.setBounds((WIDTH * 9) / 24, base + (HEIGHT * 3) / 24, w, h);
        Lucene.setBounds((WIDTH * 18) / 24, base + (HEIGHT * 3) / 24, w, h);
        words_encountered.setBounds((WIDTH * 1) / 24, base + (HEIGHT * 5) / 24, w, h);
        words_misspelt.setBounds((WIDTH * 1) / 24, base + (HEIGHT * 7) / 24,  w, h);
        words_corrected.setBounds((WIDTH * 1) / 24, base + (HEIGHT * 9) / 24,  w, h);
        words_corrected_correctly.setBounds((WIDTH * 1) / 24, base + (HEIGHT * 11) / 24,  w, h);
        accuracy.setBounds((WIDTH * 1) / 24, base + (HEIGHT * 13) / 24,  w, h);
        time_taken.setBounds((WIDTH * 1) / 24, base + (HEIGHT * 15) / 24,  w, h);
        time_per_word.setBounds((WIDTH * 1) / 24, base + (HEIGHT * 17) / 24,  w, h);
        time_per_correction.setBounds((WIDTH * 1) / 24, base + (HEIGHT * 19) / 24,  w, h);

        double[] values = new double[]{
                137463, 137463, 26541, 26541, 26213, 25614, 23367, 19879, 88.04, 74.89, 135, 1600, 0.9, 11, 5.0, 60
        };
        JLabel[] labels = new JLabel[16];
        int w1 = (WIDTH * 9) / 24;
        int h1 = base + (HEIGHT * 5) / 24;
        int gap = (HEIGHT * 1) / 24;
        int h2 = h1;
        int w2 = (WIDTH * 18) / 24;
        w = 100;
        for(int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel("<html><p style=\"font-size:14px\"><font color='white'>" + Double.toString(values[i]) + "</font></p></html>");
            if(i % 2 == 0) {
                labels[i].setBounds(w1, h1, w, h);
            }
            else {
                labels[i].setBounds(w2, h1 - gap, w, h);
            }
            h1 += gap;
            h2 = h1;
        }

        outerPanel.add(heading);
        outerPanel.add(SymSpell);
        outerPanel.add(Lucene);
        outerPanel.add(words_encountered);
        outerPanel.add(words_misspelt);
        outerPanel.add(words_corrected);
        outerPanel.add(words_corrected_correctly);
        outerPanel.add(accuracy);
        outerPanel.add(time_taken);
        outerPanel.add(time_per_word);
        outerPanel.add(time_per_correction);
        for(JLabel label : labels) {
            outerPanel.add(label);
        }

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {parent.setVisible(true);}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });

        add(outerPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
