package de.jonaspfleiderer.ui;

import de.jonaspfleiderer.util.FontUtils;

import javax.swing.*;
import java.awt.*;

public class ErrorUi extends JFrame {

    public ErrorUi(int width, int height, String errorMessage) {
        //  setup frame
        setTitle("FilePartitioner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //  position frame centered
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int posX = screenWidth / 2 - width / 2;
        int posY = screenHeight / 2 - height / 2;
        if (posX < 0) posX = 0;
        if (posY < 0) posY = 0;
        setBounds(posX, posY, width, height);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel label = new JLabel("An error occurred.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(0, 5, width, 20);
        label.setFont(FontUtils.getHeaderFont());
        panel.add(label);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(FontUtils.getNormalFont());
        textArea.setText(errorMessage);
        textArea.setBounds(5, 35, width - 10, height - (35 + 40));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(getBackground());
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(textArea.getBounds());
        panel.add(scrollPane);
    }
}
