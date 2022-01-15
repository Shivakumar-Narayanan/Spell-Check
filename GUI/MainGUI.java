package GUI;
import Main.SpellChecker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.net.ConnectException;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLDocument;
public class MainGUI extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = (2 * WIDTH) / 3;

    JFrame frame;
    JPanel outerPanel;
    JLabel heading;
    JButton dym;
    JButton text;
    JButton document;
    JButton benchmark;
    JFileChooser file_chooser;
    JLabel error;
    SpellChecker sp;
    MainGUI() {
        super("Spell Check");
        frame = this;
        setSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        //outer panel
        outerPanel = new JPanel();
        outerPanel.setLayout(null);
        outerPanel.setBorder(new TitledBorder(" "));
        outerPanel.setBackground(Color.BLACK);

        //heading
        String s1 = "<font color='#ADD8E6'>Spell </font>";
        String s2 = "<font color='yellow'>Check </font>";
        String s3 = "<font color='red'>using </font>";
        String s4 = "<font color='green'>SymSpell </font>";
        heading = new JLabel("<html><p style=\"font-size:30px\">" + s1 + s2 + s3 + s4 + "</p></html>");
        heading.setBounds((WIDTH * 6) / 24, 30, (2 * WIDTH) / 3, 100);
        heading.setSize(new Dimension((2 * WIDTH) / 3, 100));

        int offset = 30;
        //dym
        dym = new JButton("<html><p style=\"font-size:20px\"><font color='white'>Entr Txet (Serch Qurey)</font></p></html>");
        dym.setBackground(new Color(32, 32, 32));
        dym.setBounds((WIDTH / 2) - ((WIDTH * 12) / 24) / 2, ((9 * HEIGHT) / 24) + 3 - offset, (WIDTH * 12) / 24, 50);
        dym.setFocusPainted(false);
        dym.addMouseListener(new ZoomListener());

        //text
        text = new JButton("<html><p style=\"font-size:20px\"><font color='white'>Enteer Txt</font></p></html>");
        text.setBackground(new Color(32, 32, 32));
        text.setBounds((WIDTH / 2) - ((WIDTH * 12) / 24) / 2, ((9 * HEIGHT) / 24) + 83 - offset, (WIDTH * 12) / 24, 50);
        text.setFocusPainted(false);
        text.addMouseListener(new ZoomListener());

        //file chooser
        file_chooser = new JFileChooser();
        file_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //document
        document = new JButton("<html><p style=\"font-size:20px\"><font color='white'>Uplod Documnet</font></p></html>");
        document.setBackground(new Color(32, 32, 32));
        document.setBounds((WIDTH / 2) - ((WIDTH * 12) / 24) / 2, ((9 * HEIGHT) / 24) + 163 - offset, (WIDTH * 12) / 24, 50);
        document.setFocusPainted(false);
        document.addMouseListener(new ZoomListener());

        //benchmark
        benchmark = new JButton("<html><p style=\"font-size:20px\"><font color='white'>Bnchmark</font></p></html>");
        benchmark.setBackground(new Color(32, 32, 32));
        benchmark.setBounds((WIDTH / 2) - ((WIDTH * 12) / 24) / 2, ((9 * HEIGHT) / 24) + 243 - offset, (WIDTH * 12) / 24, 50);
        benchmark.setFocusPainted(false);
        benchmark.addMouseListener(new ZoomListener());

        //error
        error = new JLabel("<html><p style=\"font-size:15px\"><font color='red'>Cannot connect to Server, Please run RemoteCorpus and wait for 20 to 40 seconds</font></p></html>");
        error.setBounds(107, (20 * HEIGHT) / 24, (WIDTH * 20) / 24, 30);
        error.setVisible(false);

        outerPanel.add(heading);
        outerPanel.add(dym);
        outerPanel.add(text);
        outerPanel.add(document);
        outerPanel.add(benchmark);
        outerPanel.add(error);
        add(outerPanel);
        setLocationRelativeTo(null);
        setVisible(true);
        try {
            sp = new SpellChecker();
        }
        catch(ConnectException e) {
            System.out.println("Could not connect to Server");
            error.setVisible(true);
            while(true) {
                try {
                    sp = new SpellChecker();
                    break;
                }
                catch(Exception exception) {
                    try {
                        Thread.sleep(5000);
                    }
                    catch(Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }
        }
        error.setVisible(false);
        dym.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DidYouMeanGUI(frame);
            }
        });
        text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TextEditorGUI(frame);
            }
        });
        document.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = file_chooser.showOpenDialog(frame);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = file_chooser.getSelectedFile();
                    try {
                        BufferedReader in = new BufferedReader(new FileReader(file));
                        String line = "";
                        StringBuilder pool = new StringBuilder();
                        List<String> words;
                        while((line = in.readLine()) != null) {
                            pool.append(line);
                        }
                        String p = pool.toString();
                        p = p.replaceAll("\\.", " ");
                        p = p.replaceAll("\n", " ");
                        String[] arr = p.split(" +");
                        words = new ArrayList<>(Arrays.asList(arr));
                        new CorrectionGUI(frame, words, file);
                    }
                    catch(Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        benchmark.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new BenchMarkGUI(frame);
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

    private class ZoomListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton)e.getSource();
            String txt = button.getText();
            txt = txt.replaceAll("<[^>]*>", "");
            if(button == dym) {
                button.setText("<html><p style=\"font-size:25px\"><font color='FF3333'>Enter Text (Search Query)</font></p></html>");
            }
            else if(button == text) {
                button.setText("<html><p style=\"font-size:25px\"><font color='FF3333'>Enter Text</font></p></html>");
            }
            else if(button == document) {
                button.setText("<html><p style=\"font-size:25px\"><font color='FF3333'>Upload Document</font></p></html>");
            }
            else if(button == benchmark) {
                button.setText("<html><p style=\"font-size:25px\"><font color='FF3333'>Benchmark</font></p></html>");
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton button = (JButton)e.getSource();
            if(button == dym) {
                button.setText("<html><p style=\"font-size:20px\"><font color='white'>Entr Txet (Serch Qurey)</font></p></html>");
            }
            else if(button == text) {
                button.setText("<html><p style=\"font-size:20px\"><font color='white'>Enteer Txt</font></p></html>");
            }
            else if(button == document) {
                button.setText("<html><p style=\"font-size:20px\"><font color='white'>Uplod Documnet</font></p></html>");
            }
            else if(button == benchmark) {
                button.setText("<html><p style=\"font-size:20px\"><font color='white'>Bnchmark</font></p></html>");
            }
        }
    }

    public static void main(String[] args) {
        new MainGUI();
    }
}
