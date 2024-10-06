package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StatisticsDialog extends JDialog {
    private JTable statisticsTable;
    private DefaultTableModel tableModel;

    public StatisticsDialog(JFrame parent) {
        super(parent, "Estadísticas de Procesos", true);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 300);
        setLocationRelativeTo(getParent());

        // Crear el modelo de la tabla
        String[] columnNames = {"Proceso", "Hora Inicio", "Hora Final", "Duración (s)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Crear y configurar la tabla
        statisticsTable = new JTable(tableModel);
        statisticsTable.getTableHeader().setReorderingAllowed(false);
        
        // Configurar el ancho de las columnas
        statisticsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Proceso
        statisticsTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Hora Inicio
        statisticsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Hora Final
        statisticsTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Duración

        // Crear el panel de desplazamiento
        JScrollPane scrollPane = new JScrollPane(statisticsTable);

        // Crear el panel superior con el título
        JPanel headerPanel = new JPanel();
        JLabel titleLabel = new JLabel("Estadísticas de Ejecución de Procesos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel);

        // Crear el panel inferior con el botón de cerrar
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        // Añadir los componentes al diálogo
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateStatistics(String[][] data) {
        tableModel.setRowCount(0); // Limpiar la tabla
        for (String[] row : data) {
            tableModel.addRow(row);
        }
    }
    
    public void clearStatisticsTable() {
        tableModel.setRowCount(0);
    }
    
}