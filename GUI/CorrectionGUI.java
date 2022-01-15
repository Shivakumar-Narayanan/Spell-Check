package GUI;
import Main.SpellChecker;
import org.w3c.dom.Text;

import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLDocument;

public class CorrectionGUI extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = (2 * WIDTH) / 3;
    JFrame frame;
    JFrame parent;
    SpellChecker sp;

    List<String> words;
    JPanel outerPanel;
    JPanel gridPanel;
    JLabel heading;
    JButton save;
    JButton close;
    JLabel tip_1;
    JLabel tip_2;

    Map<JButton, SelectionItem> map;
    Map<JButton, Integer> map_index;
    List<JButton> buttons;

    File file;

    CorrectionGUI(JFrame parent, List<String> words, File file) {
        super("Correct Errors");
        //System.out.println(words);
        this.file = file;
        frame = this;
        this.parent = parent;
        parent.setVisible(false);
        try {
            sp = ((TextEditorGUI)parent).sp;
        }
        catch(Exception exception) {
            sp = ((MainGUI)parent).sp;
        }
        this.words = words;
        map = new HashMap<>();
        map_index = new HashMap<>();
        buttons = new ArrayList<>();
        for(int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            StringBuilder sb = new StringBuilder();
            for(int j = 0; j < word.length(); j++) {
                char c = word.charAt(j);
                if(Character.isLetter(c)) {
                    sb.append(c);
                }
            }
            word = sb.toString();
            if(word.equals("")) {
                continue;
            }
            JButton button;
            if(sp.isValidWord(word)) {
                button = new JButton("<html><p style=\"font-size:20px\"><font color='white'>" + word + "</font></p></html>");
            }
            else {
                button = new JButton("<html><p style=\"font-size:20px\"><font color='red'>" + word + "</font></p></html>");
            }
            button.setFocusPainted(false);
            button.setBackground(Color.BLACK);
            button.setSize(90, 50);
            String prev = i == 0 ? "" : words.get(i - 1);
            SelectionItem item = new SelectionItem(word, prev, sp);
            map.put(button, item);
            map_index.put(button, i);
            buttons.add(button);
            button.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JButton button = (JButton)e.getSource();
                    SelectionItem item = map.get(button);
                    if(SwingUtilities.isLeftMouseButton(e)) {
                        item.cycleForward();
                    }
                    else {
                        item.cycleBackward();
                    }
                    button.setText("<html><p style=\"font-size:20px\"><font color='white'>" + item.getSelection() + "</font></p></html>");
                    int index = map_index.get(button);
                    if(index != buttons.size() - 1) {
                        SelectionItem it = map.get(buttons.get(index + 1));
                        it.setPrev(item.getSelection());
                        it.sort();
                    }
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
            });
        }
        //System.out.println("Made all the labels");

        setSize(new Dimension(WIDTH, HEIGHT));
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        //outer panel
        outerPanel = new JPanel();
        outerPanel.setLayout(null);
        outerPanel.setBorder(new TitledBorder(""));
        outerPanel.setBackground(Color.BLACK);

        //gridPanel
        gridPanel = new JPanel();
        gridPanel.setBounds(30, 180, (9 * WIDTH) / 12, (15 * HEIGHT) / 24);
        GridLayout gl = new GridLayout();
        gl.setColumns(10);
        gridPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        gridPanel.setBorder(new TitledBorder(" "));
        gridPanel.setBackground(Color.BLACK);
        for(JButton button : buttons) {
            gridPanel.add(button);
        }

        //heading
        String s1 = "<font color='blue'>Correct </font>";
        String s2 = "<font color='yellow'>the </font>";
        String s3 = "<font color='red'>Spelling </font>";
        String s4 = "<font color='blue'>Errors </font>";
        //String s5 = "<font color='green'>correct </font>";
        heading = new JLabel("<html><p style=\"font-size:30px\">" + s1 + s2 + s3 + s4 + "</p></html>");
        heading.setBounds((WIDTH * 6) / 24, 30, (2 * WIDTH) / 3, 100);
        heading.setSize(new Dimension((2 * WIDTH) / 3, 100));

        //save
        save = new JButton("<html><p style=\"font-size:13px\"><font color='white'>Save</font></p></html>");
        save.setFocusPainted(false);
        save.setBackground(Color.DARK_GRAY);
        save.setBounds(170 + (WIDTH * 8) / 12, 120 + (3 * HEIGHT) / 12, 95, 30);
        save.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String res = "";
                for(JButton button : buttons) {
                    String text = button.getText();
                    text = text.replaceAll("<[^>]*>", "");
                    if(text.equals("")) {
                        break;
                    }
                    res += text + " ";
                }
                if(parent instanceof TextEditorGUI) {
                    TextEditorGUI p = (TextEditorGUI)parent;
                    p.area.setText(res);
                }
                else {
                    try {
                        PrintWriter out = new PrintWriter(new FileWriter(file));
                        out.println(res);
                        out.flush();
                        out.close();
                    }
                    catch(Exception exception) {
                        exception.printStackTrace();
                    }
                }

                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //close
        close = new JButton("<html><p style=\"font-size:13px\"><font color='white'>Close</font></p></html>");
        close.setBackground(Color.DARK_GRAY);
        close.setBounds(170 + (WIDTH * 8) / 12, 190 + (3 * HEIGHT) / 12, 95, 30);
        save.setFocusPainted(false);
        close.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //tip_1
        tip_1 = new JLabel("<html><p style=\"font-size:13px\"><font color='white'>* Correct the mistakes left to right, top to bottom</font></p></html>");
        tip_1.setBounds((WIDTH * 2) / 24, 120, (WIDTH * 20) / 24, 30);

        //tip_2
        tip_2 = new JLabel("<html><p style=\"font-size:13px\"><font color='white'>* Left click to cycle forward, right click to cycle backwards</font></p></html>");
        tip_2.setBounds((WIDTH * 2) / 24, 150, (WIDTH * 20) / 24, 30);

        outerPanel.add(heading);
        outerPanel.add(tip_1);
        outerPanel.add(tip_2);
        outerPanel.add(gridPanel);
        outerPanel.add(save);
        outerPanel.add(close);
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
