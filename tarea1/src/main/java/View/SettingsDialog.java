package View;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {
    private JSpinner osSizeSpinner;
    private JSpinner userSizeSpinner;
    private JButton saveButton;

    public SettingsDialog(JFrame parent) {
        super(parent, "Settings", true);
        setLayout(new GridLayout(3, 2, 10, 10));

        osSizeSpinner = new JSpinner(new SpinnerNumberModel(1024, 512, 8192, 128)); // Ejemplo: de 512MB a 8GB
        userSizeSpinner = new JSpinner(new SpinnerNumberModel(1024, 512, 8192, 128)); // Ejemplo: de 512MB a 8GB
        saveButton = new JButton("Save");

        add(new JLabel("OS Size (MB):"));
        add(osSizeSpinner);
        add(new JLabel("User Size (MB):"));
        add(userSizeSpinner);
        add(saveButton);

        pack();
        setLocationRelativeTo(parent);

        saveButton.addActionListener(e -> {
            saveSettings();
            setVisible(false); // Cierra el diálogo después de guardar
        });
    }

    private void saveSettings() {
        int osSize = (int) osSizeSpinner.getValue();
        int userSize = (int) userSizeSpinner.getValue();

        // Aquí puedes guardar las configuraciones, por ejemplo, en un archivo de configuración o en variables globales
        System.out.println("OS Size: " + osSize + " MB");
        System.out.println("User Size: " + userSize + " MB");

        // También puedes invocar métodos en el controlador para aplicar estas configuraciones
    }

    public int getOsSize() {
        return (int) osSizeSpinner.getValue();
    }

    public int getUserSize() {
        return (int) userSizeSpinner.getValue();
    }
}

