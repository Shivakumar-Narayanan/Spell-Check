package GUI;
import Main.SpellChecker;

import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
public class TextEditorGUI extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = (2 * WIDTH) / 3;
    JFrame parent;
    JFrame frame;
    SpellChecker sp;

    JPanel outerPanel;
    JLabel heading;
    JTextArea area;
    JButton analyze;
    JButton clear;

    TextEditorGUI(JFrame parent) {
        super("Enter Text");
        this.parent = parent;
        parent.setVisible(false);
        sp = ((MainGUI)parent).sp;
        frame = this;
        setSize(new Dimension(WIDTH, HEIGHT));
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        //outer panel
        outerPanel = new JPanel();
        outerPanel.setLayout(null);
        outerPanel.setBorder(new TitledBorder(" "));
        outerPanel.setBackground(Color.BLACK);

        //heading
        String s1 = "<font color='blue'>Enter </font>";
        String s2 = "<font color='yellow'>the </font>";
        String s3 = "<font color='red'>text </font>";
        String s4 = "<font color='blue'>to </font>";
        String s5 = "<font color='green'>correct </font>";
        heading = new JLabel("<html><p style=\"font-size:30px\">" + s1 + s2 + s3 + s4 + s5 + "</p></html>");
        heading.setBounds((WIDTH * 6) / 24, 30, (2 * WIDTH) / 3, 100);
        heading.setSize(new Dimension((2 * WIDTH) / 3, 100));

        //area
        area = new JTextArea();
        area.setLineWrap(true);
        area.setBounds(60, (3 * HEIGHT) / 12, (WIDTH * 8) / 12, (HEIGHT * 7) / 12);
        area.setFont(new Font("Serif", Font.BOLD, 27));
        area.setBackground(Color.LIGHT_GRAY);
        //area.setBackground(new Color(225, 222, 222));

        //analyze
        analyze = new JButton("<html><p style=\"font-size:13px\"><font color='white'>Analyze</font></p></html>");
        analyze.setFocusPainted(false);
        analyze.setBackground(Color.DARK_GRAY);
        analyze.setBounds(120 + (WIDTH * 8) / 12, 120 + (3 * HEIGHT) / 12, 95, 30);
        analyze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = area.getText();
                if(text.length() == 0) {
                    return;
                }
                if(text.contains("\n")) {
                    System.out.println("contains newline");
                    text = text.replaceAll("\n", " ");
                }
                List<String> words = new ArrayList<>();
                String[] sentences = text.split("\\.");
                for(String sentence : sentences) {
                    String[] arr = sentence.split(" +");
                    words.addAll(Arrays.asList(arr));
                }
                new CorrectionGUI(frame, words, null);
            }
        });

        //clear
        clear = new JButton("<html><p style=\"font-size:13px\"><font color='white'>Clear</font></p></html>");
        clear.setFocusPainted(false);
        clear.setBackground(Color.DARK_GRAY);
        clear.setBounds(120 + (WIDTH * 8) / 12, 180 + (3 * HEIGHT) / 12, 95, 30);
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {area.setText("");}
        });

        outerPanel.add(heading);
        outerPanel.add(area);
        outerPanel.add(analyze);
        outerPanel.add(clear);
        add(outerPanel);
        setLocationRelativeTo(null);
        setVisible(true);

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
    }
}
