package de.jonaspfleiderer.ui;

import de.jonaspfleiderer.Main;
import de.jonaspfleiderer.util.ExtentionlessFileFilter;
import de.jonaspfleiderer.util.FontUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

public class EncodeUi extends JFrame implements ActionListener, ListSelectionListener, KeyListener {
    private final DefaultListModel<String> listModel;
    private final JList<String> fileList;
    private final JFileChooser fileChooserIn;
    private final JFileChooser fileChooserOut;
    private final JButton buttonAddFile;
    private final JButton buttonRemoveSelected;
    private final JButton buttonStartEncoding;
    private final JButton buttonSelectOutput;
    private final JTextField textFieldMaxSize;
    private final JComboBox<String> comboBoxUnit;
    private final JTextArea textOutputFile;

    private String outputFilePath;

    public EncodeUi(int width, int height) {
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
        int fileListHeight = (int) (0.5 * height);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setFont(FontUtils.getSmallFont());
        fileList.setBounds(width / 2 - (fileListWidth / 2) - 5, 225, fileListWidth, fileListHeight);
        fileList.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBounds(fileList.getBounds());
        panel.add(scrollPane);

        //  setup textfield
        textFieldMaxSize = new JTextField();
        int textFieldWidth = 80;
        int textFieldHeight = 25;
        textFieldMaxSize.setBounds(maxFileSizeLabelWidth, 150, textFieldWidth, textFieldHeight);
        textFieldMaxSize.setFont(FontUtils.getNormalFont());
        textFieldMaxSize.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textFieldMaxSize.addKeyListener(this);
        panel.add(textFieldMaxSize);

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
        int buttonWidth = 170;
        int buttonHeight = 25;
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonAddFile) {
            if (fileChooserIn.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (listModel.contains(fileChooserIn.getSelectedFile().getAbsolutePath())) return;
                for (File f : fileChooserIn.getSelectedFiles()) {
                    listModel.addElement(f.getAbsolutePath());
                }

                if (outputFilePath != null) {
                    buttonStartEncoding.setEnabled(true);
                }
            }
        }

        if (e.getSource() == buttonRemoveSelected) {
            if (fileList.getSelectedValue() == null) return;
            listModel.removeElement(fileList.getSelectedValue());

            if (listModel.isEmpty()) {
                buttonStartEncoding.setEnabled(false);
            }
        }

        if (e.getSource() == buttonSelectOutput) {
            if (fileChooserOut.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                outputFilePath = fileChooserOut.getSelectedFile().getAbsolutePath();
                textOutputFile.setText(outputFilePath);

                if (!listModel.isEmpty()) {
                    buttonStartEncoding.setEnabled(true);
                }
            }
        }

        if (e.getSource() == buttonStartEncoding) {
            int maxSize = textFieldMaxSize.getText().isEmpty() ? 0 : Integer.parseInt(textFieldMaxSize.getText());
            String unit = maxSize == 0 ? "" : comboBoxUnit.getSelectedIndex() == 0 ? "K" :
                            comboBoxUnit.getSelectedIndex() == 1 ? "M" : "G";

            StringBuilder cmd = new StringBuilder("./" + Main.getParserName() + " encode " + maxSize + unit + " \"" + outputFilePath + "\"");
            for (int i = 0; i < listModel.getSize(); i++) {
                cmd.append(" \"").append(listModel.getElementAt(i)).append("\"");
            }

            ParserRunningUi parser = new ParserRunningUi(500, 500, this, false);
            setVisible(false);
            parser.setVisible(true);
            parser.startParsing(cmd.toString());
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
