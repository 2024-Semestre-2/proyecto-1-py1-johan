/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import Controller.PrinController;
import Model.LeerArchivos;
import static Model.LeerArchivos.readAndProcessFile;
import View.Principal;

import java.util.List;

/**
 *
 * @author johan
 */
public class App {
    public static void main(String[] args) {
        LeerArchivos Model = new LeerArchivos();
        String currentDir = System.getProperty("user.dir");
        String filePath = currentDir + "\\src\\main\\java\\App\\Memoria.txt";
        List<String> result = readAndProcessFile(filePath);
        
        Principal View = new Principal();
        PrinController Controller = new PrinController(View, Model);

        Controller.mostrarVista();
    }
}
