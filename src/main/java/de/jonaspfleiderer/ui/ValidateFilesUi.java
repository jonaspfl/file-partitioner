package de.jonaspfleiderer.ui;

import de.jonaspfleiderer.Main;
import de.jonaspfleiderer.util.FontUtils;
import de.jonaspfleiderer.util.HashUtils;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;

public class ValidateFilesUi extends JFrame implements ActionListener, KeyListener {
    private final List<String> decodedFiles;
    private final JButton buttonComputeHash;
    private final JButton buttonClose;
    private final JButton buttonCopy;
    private final JButton buttonPaste;
    private final JTextField textFieldComputedHash;
    private final JTextField textFieldPastedHash;
    private final JLabel labelHashesEqual;
    private final Color darkGreen;
    private final Color darkRed;
    private final JFrame parent;

    public ValidateFilesUi(int width, int height, JFrame parent, List<String> fileNames) {
        decodedFiles = fileNames;
        darkGreen = new Color(0, 153, 0);
        darkRed = new Color(204, 0, 0);
        this.parent = parent;

        setTitle("FilePartitioner");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        JLabel labelHeader = new JLabel("Validate files");
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
        textPane.setText("You can use this tool to compute the hash of the extracted files and compare this to the hash that got generated when the files where encoded.");
        documentStyle.setParagraphAttributes(0, documentStyle.getLength(), centerAttribute, false);
        panel.add(textPane);

        //  setup buttons
        int buttonWidth = 150;
        int buttonHeight = 30;
        buttonComputeHash = new JButton("Compute Hash");
        buttonComputeHash.setFont(FontUtils.getNormalFont());
        buttonComputeHash.setBounds(width / 2 - buttonWidth / 2, 270, buttonWidth, buttonHeight);
        buttonComputeHash.addActionListener(this);
        panel.add(buttonComputeHash);

        buttonCopy = new JButton("Copy");
        buttonCopy.setFont(FontUtils.getNormalFont());
        buttonCopy.setBounds(width - 80, 200, 75, buttonHeight);
        buttonCopy.setEnabled(false);
        buttonCopy.addActionListener(this);
        panel.add(buttonCopy);

        buttonPaste = new JButton("Paste");
        buttonPaste.setFont(FontUtils.getNormalFont());
        buttonPaste.setBounds(width - 80, 150, 75, buttonHeight);
        buttonPaste.addActionListener(this);
        panel.add(buttonPaste);

        buttonClose = new JButton("Close");
        buttonClose.setFont(FontUtils.getNormalFont());
        buttonClose.setBounds(width / 2 - buttonWidth / 2, 310, buttonWidth, buttonHeight);
        buttonClose.addActionListener(this);
        panel.add(buttonClose);

        //  setup labels
        JLabel labelPastedHash = new JLabel("Paste the hash here:");
        labelPastedHash.setFont(FontUtils.getNormalFont());
        labelPastedHash.setBounds(5, 128, width - 10, 20);
        panel.add(labelPastedHash);

        JLabel labelGeneratedHash = new JLabel("Generated MD5 hash:");
        labelGeneratedHash.setFont(FontUtils.getNormalFont());
        labelGeneratedHash.setBounds(5, 178, width - 10, 20);
        panel.add(labelGeneratedHash);

        labelHashesEqual = new JLabel();
        labelHashesEqual.setFont(FontUtils.getNormalFont());
        labelHashesEqual.setBounds(5, 240, width - 10, 20);
        labelHashesEqual.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelHashesEqual);

        //  setup text fields
        textFieldComputedHash = new JTextField();
        textFieldComputedHash.setFont(FontUtils.getMonoSpaceFont().deriveFont(15f));
        textFieldComputedHash.setBounds(5, 200, width - 10 - 85, 25);
        textFieldComputedHash.setEditable(false);
        textFieldComputedHash.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(textFieldComputedHash);

        textFieldPastedHash = new JTextField();
        textFieldPastedHash.setFont(FontUtils.getMonoSpaceFont().deriveFont(15f));
        textFieldPastedHash.setBounds(5, 150, width - 10 - 85, 25);
        textFieldPastedHash.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldPastedHash.addKeyListener(this);
        panel.add(textFieldPastedHash);
    }

    private void checkHashesEqual() {
        String hash0 = textFieldComputedHash.getText();
        String hash1 = textFieldPastedHash.getText();

        if (hash0.equals(hash1)) {
            labelHashesEqual.setForeground(darkGreen);
            labelHashesEqual.setText("Looks good, both hashes are equal!");
        } else {
            labelHashesEqual.setForeground(darkRed);
            labelHashesEqual.setText("Warning! The hashes are not equal.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonComputeHash) {
            buttonComputeHash.setEnabled(false);
            Thread t = new Thread(
                    () -> {
                        buttonComputeHash.setText("hashing...");

                        textFieldComputedHash.setText(HashUtils.getMD5FilesHash(decodedFiles));

                        buttonCopy.setEnabled(true);
                        buttonComputeHash.setText("Compute Hash");
                        buttonComputeHash.setEnabled(true);

                        checkHashesEqual();
                    });
            t.start();
        }
        if (e.getSource() == buttonCopy) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(textFieldComputedHash.getText()), null);
        }
        if (e.getSource() == buttonPaste) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            boolean isString =
                    (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);

            if (!isString) return;

            try {
                String content = (String) contents.getTransferData(DataFlavor.stringFlavor);
                textFieldPastedHash.setText(content);
            } catch (UnsupportedFlavorException | IOException ex) {
                Main.getLogger().logError("[ValidateFilesUi] " + ex.getMessage());
            }

            checkHashesEqual();
        }
        if (e.getSource() == buttonClose) {
            parent.setVisible(true);
            dispose();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        checkHashesEqual();
    }
}
