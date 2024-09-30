package Controller;

import View.Principal;
import View.SettingsDialog;
import Model.LeerArchivos;
import Model.BCP;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author johan
 */
public class PrinController {
    private Principal view;
    private LeerArchivos model;
    private SettingsDialog view2;
    private List<BCP> bcpList;
    private List<String> lines;
    private int currentIndex = 0;

    public PrinController(Principal view, LeerArchivos model) {
        this.view = view;
        this.model = model;
        this.bcpList = new ArrayList<>();

        // Aquí es donde se agrega el ActionListener al botón de búsqueda
        this.view.getSearch().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos ASM (*.asm)", "asm");
                fileChooser.setFileFilter(filter);
        
                int resultado = fileChooser.showOpenDialog(view);
                if (resultado == JFileChooser.APPROVE_OPTION) {
                    File[] archivosSeleccionados = fileChooser.getSelectedFiles();

                    for (File archivo : archivosSeleccionados) {
                        BCP bcp = new BCP(archivo.getName());
                        bcpList.add(bcp);
                        view.addProcess(bcp.getNombreProceso(), bcp.getEstado().toString()); // Agregar proceso a la tabla
                    }
                } else {
                    view.showErrorMessage("No se seleccionó ningún archivo.");
                }
            }
        });
            
    }
    public void mostrarVista() {
        view.setVisible(true);
    }
    
    private void mostrarSiguienteLinea() {
        if (lines == null || lines.isEmpty()) {
            view.showErrorMessage("No hay contenido cargado.");
            return;
        }

        if (currentIndex < lines.size()) {
            String currentLine = lines.get(currentIndex);
            view.SetNext(currentLine);

            if (!bcpList.isEmpty()) {
                bcpList.get(currentIndex).actualizarEstado(BCP.Estado.EJECUCION);
                view.updateProcessState(currentIndex, bcpList.get(currentIndex).getEstado().toString()); // Actualizar estado en la tabla
            }

            currentIndex++;
        } else {
            view.showErrorMessage("Se ha llegado a la última línea.");
        }
    }
        
        /*
        
        this.view.getReset().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetearTodo();
            }
        });
        
        this.view.getSettings().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SettingsDialog settingsDialog = new SettingsDialog(view); // 'view' es la ventana principal
                settingsDialog.setVisible(true);
            }
        });
        
        this.view.getNextButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarSiguienteLinea();
            }
        });
    }
    
     private void mostrarSiguienteLinea() {
        if (lines == null || lines.isEmpty()) {
            view.showErrorMessage("No hay contenido cargado.");
            return;
        }

        if (currentIndex < lines.size()) {
            String currentLine = lines.get(currentIndex);
            view.SetNext(currentLine);

            // Parsear y actualizar BCP
            actualizarBCP(currentLine);
        } else {
            view.showErrorMessage("Se ha llegado a la última línea.");
            currentIndex--; // Mantener el índice en la última línea para evitar desbordamiento
        }
        currentIndex++;
    }
     
    private void actualizarBCP(String currentLine) {
        // Ejemplo de parseo simple, asumiendo que la línea tiene el formato "MOV DX, 12"
        String[] parts = currentLine.split(" ");
        String instructionName = parts[0];
        String register = parts[1].replace(",", "");
        int value = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        bcp.executeInstruction(currentLine, currentIndex + 1);
        view.SetBCP(bcp.toString()); // Mostrar el estado del BCP en la vista
    }

    
    private void resetearTodo() {
        view.ResetPath();
        view.ResetContent();
        view.ResetNext();
        bcp = new BCP(); // Reiniciar el BCP
        view.SetBCP(bcp.toString()); // Mostrar el estado inicial del BCP
        currentIndex = 0;
    }

    
    
    

*/
}
