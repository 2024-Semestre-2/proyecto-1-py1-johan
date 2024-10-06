package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {
    private JSpinner memorySizeSpinner;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean isMemoryConfigurable;

    public SettingsDialog(JFrame parent, int currentMemorySize, boolean canConfigureMemory) {
        super(parent, "Memory Settings", true);
        this.isMemoryConfigurable = canConfigureMemory;
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Memory size spinner
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Memory Size (KB):"), gbc);
        
        memorySizeSpinner = new JSpinner(new SpinnerNumberModel(currentMemorySize, 256, 8192, 256));
        memorySizeSpinner.setEnabled(isMemoryConfigurable);
        gbc.gridx = 1;
        add(memorySizeSpinner, gbc);
        
        // Message when memory is not configurable
        if (!isMemoryConfigurable) {
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            add(new JLabel("Memory can only be configured when no files are loaded."), gbc);
        }
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
        
        // Default button actions
        saveButton.addActionListener(e -> dispose());
        cancelButton.addActionListener(e -> dispose());
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    public void addSaveListener(ActionListener listener) {
        // Remove any existing listeners
        for (ActionListener al : saveButton.getActionListeners()) {
            saveButton.removeActionListener(al);
        }
        saveButton.addActionListener(listener);
    }
    
    public int getMemorySize() {
        return (int) memorySizeSpinner.getValue();
    }
}