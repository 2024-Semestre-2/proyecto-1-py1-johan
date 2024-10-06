/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import Controller.PrinController;
import Model.LeerArchivos;
import View.Principal;
import java.io.IOException;

import java.util.List;

/**
 *
 * @author johan
 */
public class App {
    public static void main(String[] args) {
            // Crear instancia del modelo LeerArchivos
            LeerArchivos model = new LeerArchivos();
            
            // Obtener el directorio actual y el archivo de configuración de memoria
            
            
            // Cargar la configuración de memoria y disco
            
            // Inicializar la vista y el controlador
            Principal view = new Principal();
            PrinController controller = new PrinController(view, model);
            
            // Mostrar la vista
            controller.mostrarVista();
    }
}
