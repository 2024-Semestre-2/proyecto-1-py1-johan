/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SystemConfig {
    private static final int DEFAULT_MEMORY_SIZE = 256; // 256 KB por defecto
    private int memorySize;
    String currentDir = System.getProperty("user.dir");
    String filePath = currentDir + "\\src\\main\\java\\App\\Memoria.txt";

    public SystemConfig() {
        this(DEFAULT_MEMORY_SIZE);
    }
    
    public SystemConfig(int memorySize) {
        this.memorySize = memorySize;
    }

    private void loadConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("MEMORY_SIZE=")) {
                    String sizeStr = line.substring("MEMORY_SIZE=".length()).trim();
                    try {
                        memorySize = Integer.parseInt(sizeStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error al leer el tamaño de memoria: " + e.getMessage());
                        memorySize = DEFAULT_MEMORY_SIZE;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer el archivo de configuración: " + e.getMessage());
            memorySize = DEFAULT_MEMORY_SIZE;
        }
    }

    public int getMemorySize() {
        return memorySize > 0 ? memorySize : DEFAULT_MEMORY_SIZE;
    }
}
