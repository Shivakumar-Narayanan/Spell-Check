package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import Main.SpellChecker;
import Main.Dym;

import java.io.*;

public class Client {

    JTextPane enter_text_pane, upload_pane;
    SpellChecker sp;
    JPanel bottom_panel;
    ImagePanel picture_panel;

    Client() {
        try {
            /* frame */
            JFrame frame = new JFrame("Enter Text");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            /* top panel */
            JPanel top_panel = new JPanel();
            top_panel.setBackground(Color.WHITE);
            top_panel.setBorder(new TitledBorder(""));
            /* heading pane */
            JTextPane heading_pane = new JTextPane();
            heading_pane.setEditable(false);
            SimpleAttributeSet heading_attr_set = new SimpleAttributeSet();
            StyleConstants.setBold(heading_attr_set, true);
            StyleConstants.setAlignment(heading_attr_set, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontSize(heading_attr_set, 30);

            Document doc_1 = heading_pane.getStyledDocument();
            doc_1.insertString(doc_1.getLength(), "SELECT OPTION", heading_attr_set);
            top_panel.add(heading_pane);

            /* bottom panel */
            bottom_panel = new JPanel();
            bottom_panel.setBackground(Color.WHITE);
            bottom_panel.setLayout(new GridLayout(3, 1));
            /* text pane */
            enter_text_pane = new JTextPane();
            enter_text_pane.setEditable(false);
            SimpleAttributeSet enter_text_attr_set = new SimpleAttributeSet();
            StyleConstants.setBold(enter_text_attr_set, true);
            StyleConstants.setAlignment(enter_text_attr_set, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontSize(enter_text_attr_set, 20);

            Document doc_2 = enter_text_pane.getStyledDocument();
            doc_2.insertString(doc_2.getLength(), "Enter Text", enter_text_attr_set);
            bottom_panel.add(enter_text_pane);

            enter_text_pane.addMouseListener(new HoverListener());

            /* upload pane */
            upload_pane = new JTextPane();
            upload_pane.setEditable(false);
            SimpleAttributeSet upload_set = new SimpleAttributeSet();
            StyleConstants.setBold(upload_set, true);
            StyleConstants.setAlignment(upload_set, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontSize(upload_set, 20);

            Document doc_3 = upload_pane.getStyledDocument();
            doc_3.insertString(doc_3.getLength(), "Upload Document", upload_set);
            bottom_panel.add(upload_pane);

            upload_pane.addMouseListener(new HoverListener());

            /* picture panel */
            picture_panel = new ImagePanel();
            picture_panel.setLayout(new GridLayout(1, 1));
            bottom_panel.add(picture_panel);
            //new PictureCycler().start();

            /* adding */
            JPanel panel = new JPanel();
            panel.setBackground(Color.WHITE);
            panel.setLayout(new GridLayout(3, 1));
            panel.add(top_panel);
            panel.add(bottom_panel);
            frame.add(panel);
            frame.setPreferredSize(new Dimension(600, 800));
            frame.pack();
            frame.setVisible(true);

            sp = new SpellChecker();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private class PictureCycler extends Thread {

        @Override
        public void run() {

            while(true) {
                for(int i = 6; i >= 0; i--) {
                    String image_path = "C:\\Users\\WELCOME\\Desktop\\SymSpell\\SpellCheckProject\\src\\Pictures\\final_" + i + ".png";
                    try {
                        picture_panel.setOpaque(true);
                        //picture_panel.setBackground(Color.WHITE);
                        picture_panel.setBackground(ImageIO.read(new File(image_path)));
                        System.out.println("changed...");
                        Thread.sleep(1000);
                        bottom_panel.remove(picture_panel);
                        picture_panel = new ImagePanel();
                        picture_panel.setLayout(new GridLayout(1, 1));
                        bottom_panel.add(picture_panel);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class HoverListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            new TextEnter();
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            try {
                JTextPane pane = (JTextPane)evt.getSource();
                SimpleAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setBold(set, true);
                StyleConstants.setAlignment(set, StyleConstants.ALIGN_CENTER);
                StyleConstants.setFontSize(set, 25);
                StyleConstants.setItalic(set, true);
                StyleConstants.setForeground(set, Color.GREEN);

                Document doc_2 = pane.getStyledDocument();
                String text = pane.getText();
                doc_2.remove(0, text.length());
                doc_2.insertString(0, text, set);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            try {
                JTextPane pane = (JTextPane)evt.getSource();
                SimpleAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setBold(set, true);
                StyleConstants.setAlignment(set, StyleConstants.ALIGN_CENTER);
                StyleConstants.setFontSize(set, 15);

                Document doc_2 = pane.getStyledDocument();
                String text = pane.getText();
                doc_2.remove(0, text.length());
                doc_2.insertString(0, text, set);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class TextEnter {
        TextEnter() {
            try {
                JFrame frame = new JFrame("Enter Text");
                JPanel outer_panel = new JPanel();
                outer_panel.setLayout(new GridLayout(5, 1));

                JPanel top_panel = new JPanel();
                top_panel.setBorder(new TitledBorder(""));

                JTextPane heading_pane = new JTextPane();
                heading_pane.setEditable(false);
                SimpleAttributeSet heading_attr_set = new SimpleAttributeSet();
                StyleConstants.setBold(heading_attr_set, true);
                StyleConstants.setAlignment(heading_attr_set, StyleConstants.ALIGN_CENTER);
                StyleConstants.setFontSize(heading_attr_set, 20);

                JPanel bottom_panel = new JPanel();
                bottom_panel.setLayout(new GridLayout(1, 1));
                JTextArea area = new JTextArea();
                Font font = new Font("Verdana", Font.BOLD, 15);
                area.setFont(font);
                bottom_panel.add(area);

                Document doc = heading_pane.getStyledDocument();
                doc.insertString(doc.getLength(), "ENTER THE TEXT TO CHECK SPELLING", heading_attr_set);
                top_panel.add(heading_pane);

                JTextArea res = new JTextArea();
                res.setEditable(false);

                JButton button = new JButton("Correct Spelling");
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String text = area.getText();
                        String res = new Dym().getDidYoutMean(text);
                        area.setText(res.equals("") ? "Could not find a match" : res);
                    }
                });

                outer_panel.add(top_panel);
                outer_panel.add(bottom_panel);
                outer_panel.add(button);
                frame.setPreferredSize(new Dimension(600, 800));
                frame.add(outer_panel);
                frame.pack();
                frame.setVisible(true);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}