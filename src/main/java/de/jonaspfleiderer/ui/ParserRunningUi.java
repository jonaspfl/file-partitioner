package de.jonaspfleiderer.ui;

import de.jonaspfleiderer.Main;
import de.jonaspfleiderer.util.FontUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ParserRunningUi extends JFrame implements ActionListener {
    private final JLabel labelHeader;
    private final JTextArea textArea;
    private final JButton buttonClose;
    private final JButton buttonFontBigger;
    private final JButton buttonFontSmaller;
    private final JButton buttonValidate;
    private final JFrame parent;
    private final boolean decode;
    private final List<String> decodedFiles;

    public ParserRunningUi(int width, int height, JFrame parent, boolean decode) {
        this.parent = parent;
        this.decode = decode;

        decodedFiles = new ArrayList<>();

        //  setup frame
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

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        //  setup label
        labelHeader = new JLabel("Running Parser...");
        labelHeader.setFont(FontUtils.getHeaderFontBold());
        labelHeader.setBounds(0, 10, width, 30);
        labelHeader.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelHeader);

        //  setup font scale buttons
        buttonFontSmaller = new JButton("-");
        buttonFontSmaller.setBounds(5, height - 75, 30, 30);
        buttonFontSmaller.setFont(FontUtils.getNormalFont());
        buttonFontSmaller.addActionListener(this);
        panel.add(buttonFontSmaller);

        buttonFontBigger = new JButton("+");
        buttonFontBigger.setBounds(45, height - 75, 30, 30);
        buttonFontBigger.setFont(FontUtils.getNormalFont());
        buttonFontBigger.addActionListener(this);
        panel.add(buttonFontBigger);

        buttonValidate = new JButton("Validate");
        buttonValidate.setBounds(width - 120, height - 75, 100, 30);
        buttonValidate.setFont(FontUtils.getNormalFont());
        buttonValidate.addActionListener(this);
        buttonValidate.setEnabled(false);
        if (decode) panel.add(buttonValidate);

        //  setup textarea
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setBounds(5, 45, width - 25, height - 125);
        textArea.setFont(FontUtils.getMonoSpaceFont());
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(textArea.getBounds());
        panel.add(scrollPane);

        //  setup close button
        int buttonWidth = 100;
        int buttonHeight = 30;
        buttonClose = new JButton("Close");
        buttonClose.setBounds(width / 2 - buttonWidth / 2, height - 75, buttonWidth, buttonHeight);
        buttonClose.setEnabled(false);
        buttonClose.setFont(FontUtils.getNormalFont());
        buttonClose.addActionListener(this);
        panel.add(buttonClose);
    }

    public void startParsing(String[] cmd) {
        Main.getLogger().log("[ParserUi] Started with command: " + Arrays.toString(cmd));

        Runnable runnable = () -> {
            final Runtime r = Runtime.getRuntime();
            try {
                final Process process = r.exec(cmd);
                BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                updateConsole(output);
                updateConsole(errors);

                if (process.waitFor() == 0) {
                    if (decode) moveDecodedFiles();
                    labelHeader.setText("Finished successfully!");
                } else {
                    labelHeader.setText("The parser encountered an error!");
                }
                buttonClose.setEnabled(true);
            } catch (IOException | InterruptedException e) {
                Main.getLogger().logError("[ParserUi] " + e.getMessage());
                ErrorUi ui = new ErrorUi(500, 250, e.getMessage(), WindowConstants.HIDE_ON_CLOSE);
                ui.setVisible(true);
            }
        };
        new Thread(runnable).start();
    }

    private void updateConsole(BufferedReader stream) {
        Runnable runnable = () -> {
            String line;
            while (true) {
                try {
                    if ((line = stream.readLine()) == null) break;
                    textArea.append(line + "\n");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    Main.getLogger().log("[[Parser]] " + line);
                } catch (IOException e) {
                    Main.getLogger().logError("[ParserUi] " + e.getMessage());
                    ErrorUi ui = new ErrorUi(500, 250, e.getMessage(), WindowConstants.HIDE_ON_CLOSE);
                    ui.setVisible(true);
                }
            }
        };
        new Thread(runnable).start();
    }

    private void moveDecodedFiles() {
        List<String> moveFailed  = new ArrayList<>();

        File log = new File("parser.log");
        if (!log.exists()) {
            Main.getLogger().logError("[ParserUi] Couldn't find parser log.");
            return;
        }
        File outputDirectory = new File("output");
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdir()) {
                Main.getLogger().logError("[ParserUi] Could not create output directory.");
                return;
            }
        }
        try {
            Scanner sc = new Scanner(new InputStreamReader(new FileInputStream(log)));
            while (sc.hasNextLine()) {
                String fileName = sc.nextLine();
                File file = new File(fileName);
                if (!file.exists()) {
                    Main.getLogger().logError("[ParserUi] File '" + fileName + "' contained in parser log not found.");
                    continue;
                }

                if (!file.renameTo(new File(outputDirectory, fileName))) {
                    Main.getLogger().logError("[ParserUi] File '" + fileName + "' could not be moved to output directory.");
                    moveFailed.add(fileName);
                } else {
                    decodedFiles.add(outputDirectory + "/" + fileName);
                }
            }

            sc.close();
            log.delete();
            if (!moveFailed.isEmpty()) {
                ErrorUi ui = new ErrorUi(500, 250, "Error moving files: " + moveFailed, WindowConstants.HIDE_ON_CLOSE);
                ui.setVisible(true);
            } else {
                buttonValidate.setEnabled(true);
            }
        } catch (FileNotFoundException e) {
            Main.getLogger().logError("[ParserUi] " + e.getMessage());
            ErrorUi ui = new ErrorUi(500, 250, e.getMessage(), WindowConstants.HIDE_ON_CLOSE);
            ui.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonClose) {
            if (parent == null) System.exit(0);

            parent.setVisible(true);
            dispose();
        }

        if (e.getSource() == buttonFontSmaller) {
            Font f = textArea.getFont();
            if (f.getSize() == 1) return;
            textArea.setFont(f.deriveFont((float) f.getSize() - 1));
        }

        if (e.getSource() == buttonFontBigger) {
            Font f = textArea.getFont();
            if (f.getSize() == 50) return;
            textArea.setFont(f.deriveFont((float) f.getSize() + 1));
        }

        if (e.getSource() == buttonValidate) {
            ValidateFilesUi ui = new ValidateFilesUi(500, 380, this, decodedFiles);
            ui.setVisible(true);
            setVisible(false);
        }
    }
}
