package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author johan
 */
public class LeerArchivos {
    private String filePath;
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String leerArchivo() throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    private void validarLinea(String line) throws IOException {
        Pattern movPattern = Pattern.compile("^MOV\\s+(AX|BX|CX|DX),\\s*(-?\\d+|AX|BX|CX|DX)$");
        Pattern addSubPattern = Pattern.compile("^(ADD|SUB)\\s+(AX|BX|CX|DX)$");
        Pattern loadStorePattern = Pattern.compile("^(LOAD|STORE)\\s+(AX|BX|CX|DX)$");

        if (!movPattern.matcher(line).matches() && 
            !addSubPattern.matcher(line).matches() && 
            !loadStorePattern.matcher(line).matches()) {
            throw new IOException("Error en la sintaxis de la línea: " + line);
        }
    }
    
    public static List<String> readAndProcessFile(String filePath) {
        List<String> resultList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Dividimos la línea por comas
                String[] values = line.split(",");
                for (String value : values) {
                    // Añadimos los valores con trim a la lista
                    resultList.add(value.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return resultList;
    }
}
