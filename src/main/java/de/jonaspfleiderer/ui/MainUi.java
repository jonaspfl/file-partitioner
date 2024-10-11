package de.jonaspfleiderer.ui;

import de.jonaspfleiderer.util.FontUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUi extends JFrame implements ActionListener {
    private final JButton buttonEncode;
    private final JButton buttonDecode;

    public MainUi(int width, int height) {
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

        //  setup panel
        JPanel panel = new JPanel();
        panel.setSize(width, height);
        panel.setLayout(null);
        add(panel);

        //  setup labels
        //  header
        JLabel labelHeader = new JLabel("Welcome to FilePartitioner!");
        labelHeader.setFont(FontUtils.getHeaderFontBold());
        labelHeader.setBounds(0, 10, width, 20);
        labelHeader.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelHeader);

        //  setup buttons
        int buttonWidth = 150;
        int buttonHeight = 35;
        //  encode
        buttonEncode = new JButton("Encode");
        buttonEncode.setFont(FontUtils.getBigButtonFont());
        buttonEncode.setBounds(width / 2 - (buttonWidth / 2), 60, buttonWidth, buttonHeight);
        buttonEncode.addActionListener(this);
        panel.add(buttonEncode);
        //  decode
        buttonDecode = new JButton("Decode");
        buttonDecode.setFont(FontUtils.getBigButtonFont());
        buttonDecode.setBounds(width / 2 - (buttonWidth / 2), 100, buttonWidth, buttonHeight);
        buttonDecode.addActionListener(this);
        panel.add(buttonDecode);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonEncode) {
            EncodeUi ui = new EncodeUi(500, 805);
            setVisible(false);
            ui.setVisible(true);
        }

        if (e.getSource() == buttonDecode) {
            DecodeUi ui = new DecodeUi(500, 290);
            setVisible(false);
            ui.setVisible(true);
        }
    }
}
