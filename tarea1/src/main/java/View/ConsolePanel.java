package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.swing.text.*;

public class ConsolePanel extends JPanel {
    private JTextPane consoleOutput;
    private JTextField consoleInput;
    private JButton submitButton;
    private boolean waitingForInput;
    private String userInput;
    
    public ConsolePanel() {
        setLayout(new BorderLayout());
        
        // Console output area
        consoleOutput = new JTextPane();
        consoleOutput.setEditable(false);
        consoleOutput.setBackground(Color.BLACK);
        
        // Create and configure the document style
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet outputStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.GREEN);
        
        StyledDocument doc = consoleOutput.getStyledDocument();
        consoleOutput.setStyledDocument(doc);
        
        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        // Console input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        consoleInput = new JTextField();
        consoleInput.setBackground(Color.BLACK);
        consoleInput.setForeground(Color.GREEN);
        consoleInput.setCaretColor(Color.GREEN);
        
        submitButton = new JButton("Submit");
        submitButton.setEnabled(false);
        
        inputPanel.add(consoleInput, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        
        // Add components to panel
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        setupListeners();
    }
    
    private void setupListeners() {
        submitButton.addActionListener(e -> handleSubmit());
        
        consoleInput.addActionListener(e -> handleSubmit());
        
        // Only allow numbers and Enter key
        consoleInput.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_ENTER) {
                    e.consume();
                }
            }
        });
    }
    
    private void handleSubmit() {
        if (waitingForInput) {
            userInput = consoleInput.getText();
            try {
                int value = Integer.parseInt(userInput);
                if (value >= 0 && value <= 255) {
                    printToConsole("Input: " + userInput);
                    waitingForInput = false;
                    submitButton.setEnabled(false);
                    consoleInput.setEnabled(false);
                    consoleInput.setText("");
                } else {
                    printToConsole("Error: Input must be between 0 and 255");
                }
            } catch (NumberFormatException ex) {
                printToConsole("Error: Invalid number");
            }
        }
    }
    
    public void printToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = consoleOutput.getStyledDocument();
            try {
                doc.insertString(doc.getLength(), text + "\n", 
                    StyleContext.getDefaultStyleContext().addAttribute(
                        SimpleAttributeSet.EMPTY, 
                        StyleConstants.Foreground, 
                        Color.GREEN
                    )
                );
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void waitForInput(Consumer<String> callback) {
        CompletableFuture<String> future = new CompletableFuture<>();

        waitingForInput = true;
        consoleInput.setEnabled(true);
        submitButton.setEnabled(true);
        consoleInput.requestFocus();

        ActionListener listener = e -> {
            if (!waitingForInput) return;
            String input = consoleInput.getText();
            consoleInput.setText("");
            waitingForInput = false;
            consoleInput.setEnabled(false);
            submitButton.setEnabled(false);
            future.complete(input);
        };

        consoleInput.addActionListener(listener);
        submitButton.addActionListener(listener);

        // Cuando el futuro se complete, ejecutamos el callback de forma asíncrona
        future.thenAccept(callback);
    }
    
    public void clear() {
        consoleOutput.setText("");
    }
}
