package de.jonaspfleiderer.ui;

import de.jonaspfleiderer.Main;
import de.jonaspfleiderer.util.ExtentionlessFileFilter;
import de.jonaspfleiderer.util.FontUtils;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DecodeUi extends JFrame implements ActionListener {
    private final JButton buttonSelectFile;
    private final JButton buttonStartDecode;
    private final JFileChooser fileChooser;
    private final JTextArea textInputFile;
    private String filePath;

    public DecodeUi(int width, int height) {
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

        //  setup header label
        JLabel labelHeader = new JLabel("Decode files");
        labelHeader.setFont(FontUtils.getHeaderFontBold());
        labelHeader.setBounds(0, 10, width, 20);
        labelHeader.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelHeader);
        //  information
        JTextPane textPane = new JTextPane();
        textPane.setBounds(15, 50, width - 30, 70);
        textPane.setFont(FontUtils.getNormalFont());
        StyledDocument documentStyle = textPane.getStyledDocument();
        SimpleAttributeSet centerAttribute = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerAttribute, StyleConstants.ALIGN_CENTER);
        textPane.setEditable(false);
        textPane.setBackground(getBackground());
        textPane.setText("Select the file you want to decode. Only select the parser file, not the '_data' files! All original files will be decoded exactly like they were encoded before.");
        documentStyle.setParagraphAttributes(0, documentStyle.getLength(), centerAttribute, false);
        panel.add(textPane);

        //  setup buttons
        int buttonWidth = 140;
        int buttonHeight = 30;
        buttonSelectFile = new JButton("Select File");
        buttonSelectFile.setFont(FontUtils.getNormalFont());
        buttonSelectFile.setBounds(width / 2 - buttonWidth / 2, 180, buttonWidth, buttonHeight);
        buttonSelectFile.addActionListener(this);
        panel.add(buttonSelectFile);

        buttonStartDecode = new JButton("Start Decode");
        buttonStartDecode.setFont(FontUtils.getNormalFont());
        buttonStartDecode.setBounds(width / 2 - buttonWidth / 2, 215, buttonWidth, buttonHeight);
        buttonStartDecode.addActionListener(this);
        buttonStartDecode.setEnabled(false);
        panel.add(buttonStartDecode);

        //  setup file label
        textInputFile = new JTextArea();
        textInputFile.setFont(FontUtils.getNormalFont());
        textInputFile.setBounds(5, 130, width - 25, 15);
        textInputFile.setEditable(false);
        textInputFile.setBackground(getBackground());
        JScrollPane scrollPane = new JScrollPane(textInputFile);
        scrollPane.setBounds(5, 130, width - 25, 45);
        panel.add(scrollPane);

        //  setup file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select the file you want to decode.");
        fileChooser.addChoosableFileFilter(new ExtentionlessFileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        panel.add(fileChooser);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonSelectFile) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
                textInputFile.setText(filePath);

                Main.getLogger().log("[DecodeUi] Input file set to '" + filePath + "'");

                buttonStartDecode.setEnabled(true);
            }
        }

        if (e.getSource() == buttonStartDecode) {
            if (filePath == null) return;

            String cmd = "./" + Main.getParserName() + " decode " + filePath;

            ParserRunningUi parser = new ParserRunningUi(500, 500, this, true);
            setVisible(false);
            parser.setVisible(true);
            parser.startParsing(cmd);
        }
    }
}
