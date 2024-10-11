package de.jonaspfleiderer.ui;

import de.jonaspfleiderer.util.FontUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ParserRunningUi extends JFrame implements ActionListener {
    private final JLabel labelHeader;
    private final JTextArea textArea;
    private final JButton buttonClose;
    private final JButton buttonFontBigger;
    private final JButton buttonFontSmaller;
    private final JFrame parent;
    private final boolean decode;

    public ParserRunningUi(int width, int height, JFrame parent, boolean decode) {
        this.parent = parent;
        this.decode = decode;

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

    public void startParsing(String cmd) {
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
                ErrorUi ui = new ErrorUi(500, 250, e.getMessage());
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
                } catch (IOException e) {
                    ErrorUi ui = new ErrorUi(500, 250, e.getMessage());
                    ui.setVisible(true);
                }
            }
        };
        new Thread(runnable).start();
    }

    private void moveDecodedFiles() {
        List<String> moveFailed  = new ArrayList<>();

        File log = new File("parser.log");
        if (!log.exists()) return;
        File outputDirectory = new File("output");
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdir()) return;
        }
        try {
            Scanner sc = new Scanner(new InputStreamReader(new FileInputStream(log)));
            while (sc.hasNextLine()) {
                String fileName = sc.nextLine();
                File file = new File(fileName);
                if (!file.exists()) continue;

                if (!file.renameTo(new File(outputDirectory, fileName))) {
                    moveFailed.add(fileName);
                }
            }

            sc.close();
            log.delete();
            if (!moveFailed.isEmpty()) {
                ErrorUi ui = new ErrorUi(500, 250, "Error moving files: " + moveFailed);
                ui.setVisible(true);
            }
        } catch (FileNotFoundException e) {
            ErrorUi ui = new ErrorUi(500, 250, e.getMessage());
            ui.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonClose) {
            if (parent == null) System.exit(0);

            setVisible(false);
            parent.setVisible(true);
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
    }
}
