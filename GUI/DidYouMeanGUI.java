package GUI;
import Main.SpellChecker;
import Main.Dym;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class DidYouMeanGUI extends JFrame {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = (2 * WIDTH) / 3;

    JFrame parent;
    JPanel outerPanel;
    JLabel heading;
    JTextField text;
    JButton button;
    JLabel dym;
    JLabel res;
    SpellChecker sp;

    DidYouMeanGUI(JFrame parent) {
        super("Did You Mean?");
        this.parent = parent;
        parent.setVisible(false);
        sp = ((MainGUI)parent).sp;
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
        String s5 = "<font color='green'>search </font>";
        heading = new JLabel("<html><p style=\"font-size:30px\">" + s1 + s2 + s3 + s4 + s5 + "</p></html>");
        heading.setBounds((WIDTH * 6) / 24, 30, (2 * WIDTH) / 3, 100);
        heading.setSize(new Dimension((2 * WIDTH) / 3, 100));

        //text
        text = new JTextField();
        text.setBounds(30, (9 * HEIGHT) / 24, (3 * WIDTH) / 4, 30);
        text.setSize(new Dimension((3 * WIDTH) / 4, 35));
        text.setFont(new Font("Serif", Font.BOLD, 27));

        //button
        button = new JButton("<html><p style=\"font-size:13px\"><font color='white'>Search</font></p></html>");
        button.setFocusPainted(false);
        button.setBackground(Color.GRAY);
        button.setBounds(60 + (3 * WIDTH) / 4, ((9 * HEIGHT) / 24) + 3, 90, 30);
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    res.setText("");
                    dym.setText("");
                    String input = text.getText();
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < input.length(); i++) {
                        char c = input.charAt(i);
                        if(Character.isLetterOrDigit(c) || c == ' ') {
                            sb.append(c);
                        }
                    }
                    input = sb.toString();
                    String[] words = input.split(" +");
                    String result = "";
                    String prev = "";
                    for(String word : words) {
                        String pred = "";
                        if(prev.equals("") && words.length > 1) {
                            pred = sp.getBestPair(words[0], words[1])[0];
                        }
                        else {
                            pred = sp.getTopPredictionSingleWithPrev(word, prev);
                        }
                        result += pred + " ";
                        prev = pred;
                    }
                    result = result.trim();
                    if(result.equals(input)) {
                        DymPrinter printer = new DymPrinter("All Good", 1);
                        printer.start();
                        return;
                    }
                    DymPrinter printer = new DymPrinter("Did You Mean", 3);
                    printer.start();
                    res.setText("<html><p style=\"font-size:15px\"><font color='white'>" + result + "</font></p></html>");
                    new ResultGetter(input).start();
                    System.out.println("Done");
                }
                catch(Exception exp) {
                    exp.printStackTrace();
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setText("<html><p style=\"font-size:14px\"><font color='white'>Search</font></p></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setText("<html><p style=\"font-size:13px\"><font color='white'>Search</font></p></html>");
            }
        });

        //dym
        dym = new JLabel();
        dym.setBounds(30, ((9 * HEIGHT) / 24) + 60, 150, 60);

        //res
        res = new JLabel();
        res.setBounds(190, ((9 * HEIGHT) / 24) + 60, 450, 60);
        res.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String txt = res.getText().replaceAll("<[^>]*>", "");
                text.setText(txt);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {
                String txt = res.getText().replaceAll("<[^>]*>", "");
                res.setText("<html><p style=\"font-size:16px\"><font color='purple'>" + txt + "</font></p></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                String txt = res.getText().replaceAll("<[^>]*>", "");
                res.setText("<html><p style=\"font-size:15px\"><font color='white'>" + txt + "</font></p></html>");
            }
        });

        new BackGroundClearer().start();
        outerPanel.add(heading);
        outerPanel.add(text);
        outerPanel.add(button);
        outerPanel.add(dym);
        outerPanel.add(res);
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

    private static String[] colors = {"red", "green", "blue", "yellow"};
    private static int random_int = -1;
    private static int count = 0;
    private static String getStringWithRandomColor(String input, int thresh) {
        if(input.equals(" ")) {
            return input;
        }
        String color = "";
        if(random_int == -1) {
            random_int = new Random().nextInt(4);
        }
        else {
            if(count < thresh) {
                count ++;
            }
            else {
                count = 0;
                random_int = (random_int + 1) % 4;
            }
        }
        color = colors[random_int];
        String res = "<font color='" + color + "'>" + input + "</font>";
        return res;
    }

    private class DymPrinter extends Thread {
        String text;
        int thresh;
        DymPrinter(String text, int thresh) {
            this.text = text;
            this.thresh = thresh - 1;
        }
        @Override
        public void run() {
            try {
                String s = text;
                String res = "";
                String start = "<html><p style=\"font-size:15px\">";
                String end = "</p></html>";
                for(int i = 0; i < s.length(); i++) {
                    char cur = s.charAt(i);
                    res += getStringWithRandomColor("" + cur, thresh);
                    dym.setText(start + res + end);
                    //Thread.sleep(100);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BackGroundClearer extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    if(text.getText().equals("")) {
                        dym.setText("");
                        res.setText("");
                    }
                    Thread.sleep(2000);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ResultGetter extends Thread {
        String input;
        ResultGetter(String input) {
            this.input = input;
        }
        @Override
        public void run() {
            String result = new Dym().getDidYoutMean(input);
            String[] arr = result.split(" +");
            StringBuilder sb = new StringBuilder();
            String corrected_result = "";
            String prev = "";
            for(String word : arr) {
                if(word.equals("")) {
                    continue;
                }
                if(!sp.isValidWord(word)) {
                    String correction = "";
                    if(prev.equals("") && arr.length > 1) {
                        correction = sp.getBestPair(word, arr[1])[0];
                    }
                    else {
                        correction = sp.getTopPredictionSingleWithPrev(word, prev);
                    }
                    sb.append(correction).append(" ");
                    prev = correction;
                }
                else {
                    sb.append(word).append(" ");
                    prev = word;
                }
            }
            corrected_result = sb.toString();
            if(!corrected_result.equals("")) {
                res.setText("<html><p style=\"font-size:15px\"><font color='white'>" + corrected_result + "</font></p></html>");
            }
        }
    }
}
