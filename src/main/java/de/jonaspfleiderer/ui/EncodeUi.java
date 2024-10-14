package de.jonaspfleiderer.ui;

import de.jonaspfleiderer.Main;
import de.jonaspfleiderer.util.ExtentionlessFileFilter;
import de.jonaspfleiderer.util.FontUtils;
import de.jonaspfleiderer.util.HashUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EncodeUi extends JFrame implements ActionListener, ListSelectionListener, KeyListener {
    private final DefaultListModel<String> listModel;
    private final JList<String> fileList;
    private final JFileChooser fileChooserIn;
    private final JFileChooser fileChooserOut;
    private final JButton buttonAddFile;
    private final JButton buttonRemoveSelected;
    private final JButton buttonStartEncoding;
    private final JButton buttonSelectOutput;
    private final JButton buttonCopy;
    private final JButton buttonClose;
    private final JTextField textFieldMaxSize;
    private final JTextField textFieldFileHash;
    private final JComboBox<String> comboBoxUnit;
    private final JTextArea textOutputFile;

    private String outputFilePath;

    public EncodeUi(int width, int height) {
        //  setup frame
        setTitle("FilePartitioner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        int buttonWidth = 170;
        int buttonHeight = 25;

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
        JLabel labelHeader = new JLabel("Encode files");
        labelHeader.setFont(FontUtils.getHeaderFontBold());
        labelHeader.setBounds(0, 10, width, 20);
        labelHeader.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelHeader);
        //  information
        JTextPane textPane = new JTextPane();
        textPane.setBounds(15, 50, width - 30, 90);
        textPane.setFont(FontUtils.getNormalFont());
        StyledDocument documentStyle = textPane.getStyledDocument();
        SimpleAttributeSet centerAttribute = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerAttribute, StyleConstants.ALIGN_CENTER);
        textPane.setEditable(false);
        textPane.setBackground(getBackground());
        textPane.setText("All selected files will be encoded and partitioned according to the maximum file size. If no maximum file size is entered, no partitioning will be done and all files will be encoded into one single file.");
        documentStyle.setParagraphAttributes(0, documentStyle.getLength(), centerAttribute, false);
        panel.add(textPane);
        //  max file size
        JLabel labelMaxSize = new JLabel("Maximum file size:");
        int maxFileSizeLabelWidth = 150;
        labelMaxSize.setFont(FontUtils.getNormalFont());
        labelMaxSize.setBounds(15, 153, maxFileSizeLabelWidth, 20);
        panel.add(labelMaxSize);

        //  setup file list
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        int fileListWidth = (int) (0.8 * width);
        int fileListHeight = (int) (0.3 * height);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setFont(FontUtils.getSmallFont());
        fileList.setBounds(width / 2 - (fileListWidth / 2) - 5, 225, fileListWidth, fileListHeight);
        fileList.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBounds(fileList.getBounds());
        panel.add(scrollPane);

        JLabel labelFileHash = new JLabel("MD5 hash-value representing the encoded files:");
        labelFileHash.setFont(FontUtils.getNormalFont());
        labelFileHash.setBounds(5, 230 + fileListHeight + 30 + buttonHeight * 3 + 45, width - 10, 20);
        labelFileHash.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelFileHash);

        //  setup textfield
        textFieldMaxSize = new JTextField();
        int textFieldWidth = 80;
        int textFieldHeight = 25;
        textFieldMaxSize.setBounds(maxFileSizeLabelWidth, 150, textFieldWidth, textFieldHeight);
        textFieldMaxSize.setFont(FontUtils.getNormalFont());
        textFieldMaxSize.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textFieldMaxSize.addKeyListener(this);
        panel.add(textFieldMaxSize);

        textFieldFileHash = new JTextField();
        textFieldFileHash.setBounds(5, labelFileHash.getY() + 25, width - 10 - 85, 25);
        textFieldFileHash.setFont(FontUtils.getMonoSpaceFont().deriveFont(15f));
        textFieldFileHash.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldFileHash.addKeyListener(this);
        textFieldFileHash.setEditable(false);
        panel.add(textFieldFileHash);

        //  setup combobox
        comboBoxUnit = new JComboBox<>();
        int comboBoxWidth = 90;
        int comboBoxHeight = 30;
        comboBoxUnit.setBounds(maxFileSizeLabelWidth + textFieldWidth + 5, 148, comboBoxWidth, comboBoxHeight);
        comboBoxUnit.setFont(FontUtils.getNormalFont());
        comboBoxUnit.addItem("KiB");
        comboBoxUnit.addItem("MiB");
        comboBoxUnit.addItem("GiB");
        comboBoxUnit.setSelectedIndex(1);
        panel.add(comboBoxUnit);

        //  setup file chooser
        fileChooserIn = new JFileChooser();
        fileChooserIn.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooserIn.setMultiSelectionEnabled(true);
        fileChooserIn.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooserIn.setDialogTitle("Select the file you want to add.");

        fileChooserOut = new JFileChooser();
        fileChooserOut.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooserOut.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserOut.setFileFilter(new ExtentionlessFileFilter());
        fileChooserOut.setAcceptAllFileFilterUsed(false);
        fileChooserOut.setDialogTitle("Select the output file.");

        //  setup add file button
        buttonAddFile = new JButton("Add File");
        buttonAddFile.setBounds(width / 2 - (buttonWidth / 2), 195, buttonWidth, buttonHeight);
        buttonAddFile.setFont(FontUtils.getNormalFont());
        buttonAddFile.addActionListener(this);
        panel.add(buttonAddFile);

        //  setup remove selected button
        buttonRemoveSelected = new JButton("Remove Selected");
        buttonRemoveSelected.setBounds(width / 2 - (buttonWidth / 2), 225 + fileListHeight + 5, buttonWidth, buttonHeight);
        buttonRemoveSelected.setFont(FontUtils.getNormalFont());
        buttonRemoveSelected.setEnabled(false);
        buttonRemoveSelected.addActionListener(this);
        panel.add(buttonRemoveSelected);

        //  setup select output button
        buttonSelectOutput = new JButton("Select Output File");
        buttonSelectOutput.setBounds(width / 2 - (buttonWidth / 2), 230 + fileListHeight + 5 + buttonHeight, buttonWidth, buttonHeight);
        buttonSelectOutput.setFont(FontUtils.getNormalFont());
        buttonSelectOutput.addActionListener(this);
        panel.add(buttonSelectOutput);

        buttonCopy = new JButton("Copy");
        buttonCopy.setFont(FontUtils.getNormalFont());
        buttonCopy.setBounds(width - 80, textFieldFileHash.getY(), 75, buttonHeight);
        buttonCopy.setEnabled(false);
        buttonCopy.addActionListener(this);
        panel.add(buttonCopy);

        //  setup output file text
        textOutputFile = new JTextArea();
        textOutputFile.setFont(FontUtils.getNormalFont());
        textOutputFile.setBounds(5, 230 + fileListHeight + 10 + buttonHeight * 2, width - 25, 15);
        textOutputFile.setEditable(false);
        textOutputFile.setBackground(getBackground());
        textOutputFile.setLineWrap(false);
        JScrollPane scrollPaneLabel = new JScrollPane(textOutputFile);
        scrollPaneLabel.setBounds(5, 230 + fileListHeight + 10 + buttonHeight * 2, width - 25, 40);
        panel.add(scrollPaneLabel);

        //  setup start encoding button
        buttonStartEncoding = new JButton("Start Encoding");
        buttonStartEncoding.setBounds(width / 2 - (buttonWidth / 2), 230 + fileListHeight + 30 + buttonHeight * 3, buttonWidth, buttonHeight);
        buttonStartEncoding.setFont(FontUtils.getNormalFont());
        buttonStartEncoding.setEnabled(false);
        buttonStartEncoding.addActionListener(this);
        panel.add(buttonStartEncoding);

        buttonClose = new JButton("Close");
        buttonClose.setFont(FontUtils.getNormalFont());
        buttonClose.setBounds(width / 2 - (buttonWidth / 2), buttonCopy.getY() + 35, buttonWidth, buttonHeight);
        buttonClose.addActionListener(this);
        panel.add(buttonClose);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonAddFile) {
            if (fileChooserIn.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (listModel.contains(fileChooserIn.getSelectedFile().getAbsolutePath())) return;
                for (File f : fileChooserIn.getSelectedFiles()) {
                    listModel.addElement(f.getAbsolutePath());
                    Main.getLogger().log("[EncodeUi] Added file '" + f.getAbsolutePath() + "' to encode list.");
                }

                if (outputFilePath != null) {
                    buttonStartEncoding.setEnabled(true);
                }
            }
        }

        if (e.getSource() == buttonRemoveSelected) {
            if (fileList.getSelectedValue() == null) return;
            Main.getLogger().log("[EncodeUi] Removed file '" + fileList.getSelectedValue() + "' from encode list.");
            listModel.removeElement(fileList.getSelectedValue());

            if (listModel.isEmpty()) {
                buttonStartEncoding.setEnabled(false);
            }
        }

        if (e.getSource() == buttonSelectOutput) {
            if (fileChooserOut.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                outputFilePath = fileChooserOut.getSelectedFile().getAbsolutePath();
                textOutputFile.setText(outputFilePath);

                Main.getLogger().log("[EncodeUi] Selected output file '" + outputFilePath + "'.");

                if (!listModel.isEmpty()) {
                    buttonStartEncoding.setEnabled(true);
                }
            }
        }

        if (e.getSource() == buttonStartEncoding) {
            buttonStartEncoding.setEnabled(false);

            new Thread(() -> {
                int maxSize = textFieldMaxSize.getText().isEmpty() ? 0 : Integer.parseInt(textFieldMaxSize.getText());
                String unit = maxSize == 0 ? "" : comboBoxUnit.getSelectedIndex() == 0 ? "K" :
                        comboBoxUnit.getSelectedIndex() == 1 ? "M" : "G";

                int cmdLength = listModel.getSize() + 4;
                String[] cmd = new String[cmdLength];
                cmd[0] = "./" + Main.getParserName();
                cmd[1] = "encode";
                cmd[2] = maxSize + unit;
                cmd[3] = outputFilePath;

                buttonStartEncoding.setText("hashing...");

                List<String> files = new ArrayList<>();
                for (int i = 0; i < listModel.getSize(); i++) {
                    cmd[4 + i] = listModel.getElementAt(i);
                    files.add(listModel.getElementAt(i));
                }

                textFieldFileHash.setText(HashUtils.getMD5FilesHash(files));
                buttonCopy.setEnabled(true);

                buttonStartEncoding.setText("encoding...");

                ParserRunningUi parser = new ParserRunningUi(500, 500, this, false);
                setVisible(false);
                parser.setVisible(true);
                parser.startParsing(cmd);

                buttonStartEncoding.setEnabled(true);
                buttonStartEncoding.setText("Start Encoding");
            }).start();
        }

        if (e.getSource() == buttonCopy) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(textFieldFileHash.getText()), null);
        }

        if (e.getSource() == buttonClose) {
            System.exit(0);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == fileList) {
            buttonRemoveSelected.setEnabled(fileList.getSelectedValue() != null);
        }
    }

    private String removeNoneNumeric(String s) {
        StringBuilder res = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) continue;
            res.append(c);
        }
        return res.toString();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == textFieldMaxSize) {
            String text = removeNoneNumeric(textFieldMaxSize.getText());
            if (text.equals(textFieldMaxSize.getText())) return;
            textFieldMaxSize.setText(text);
        }
    }
}
