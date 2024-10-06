package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author johan
 */
public class LeerArchivos {

    public static class ASMFileContent {
        private List<String> instructions;
        private String fileName;
        
        public ASMFileContent(String fileName, List<String> instructions) {
            this.fileName = fileName;
            this.instructions = instructions;
        }
        
        public List<String> getInstructions() { return instructions; }
        public String getFileName() { return fileName; }
    }
    
    public ASMFileContent readFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        String fileName = file.getName().replaceFirst("[.][^.]+$", ""); // Elimina la extensión
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }
        
        return new ASMFileContent(fileName, lines);
    }
    /*
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
    */
    public boolean validateInstructions(List<String> instructions) {
        // Aquí puedes agregar la validación de las instrucciones
        // Por ahora retorna true, pero deberías implementar la lógica de validación
        return true;
    }
}
