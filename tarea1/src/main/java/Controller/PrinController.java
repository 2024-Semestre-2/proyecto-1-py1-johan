package Controller;

import View.Principal;
import View.SettingsDialog;
import Model.LeerArchivos;
import Model.BCP;
import Model.DiskMemory;
import Model.FileInfo;
import Model.Memory;
import Model.ProcessStatistics;
import Model.SystemConfig;
import View.ConsolePanel;
//import View.StatisticsDialog;
import java.util.Random;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PrinController {
    private Principal view;
    private LeerArchivos fileReader;
    private List<FileInfo> fileInfoList;
    private SettingsDialog view2;
    private SystemConfig config;
    private int currentFileIndex = 0;
    private Memory memory;
    private int currentInstructionIndex = 0;
    private Map<String, Integer> processRowMap = new HashMap<>();
    private ConsolePanel consolePanel;
    private DiskMemory diskMemory;
    private ProcessStatistics processStats = new ProcessStatistics();
    private AtomicBoolean isExecuting;
    private Thread executionThread;
    private int globalDiskIndex = 1;

    public PrinController(Principal view, LeerArchivos model) {
        this.view = view;
        this.fileReader = fileReader;       
        this.config = new SystemConfig();
        this.memory = new Memory(config);
        this.fileInfoList = new ArrayList<>();
        this.consolePanel = view.getConsolePanel();
        this.diskMemory = new DiskMemory();
        this.isExecuting = new AtomicBoolean(false);

        this.view.getSearch().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFiles();
            }
        });

        // Listener para avanzar al siguiente paso del BCP
        this.view.getNextButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeNextInstruction();
            }
        });

        this.view.getSettings().addActionListener(e -> {
            boolean canConfigureMemory = fileInfoList.isEmpty();
            SettingsDialog settingsDialog = new SettingsDialog(view, config.getMemorySize(), canConfigureMemory);
            settingsDialog.addSaveListener(event -> {
                if (canConfigureMemory) {
                    int newSize = settingsDialog.getMemorySize();
                    config = new SystemConfig(newSize);
                    memory = new Memory(config);
                    settingsDialog.dispose();
                }
            });
            settingsDialog.setVisible(true);
        });
        
        this.view.getExecuteButton().addActionListener(e -> {
            if (!isExecuting.get()) {
                startAutomaticExecution();
            } else {
                stopAutomaticExecution();
            }
        });
        
        this.view.getReset().addActionListener(e -> resetSystem());
        
    }
    
    private void loadFiles(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos ASM (*.asm)", "asm");
        fileChooser.setFileFilter(filter);
        
        LeerArchivos fileReader = new LeerArchivos();
        int resultado = fileChooser.showOpenDialog(view);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File[] archivosSeleccionados = fileChooser.getSelectedFiles();
            for (File archivo : archivosSeleccionados) {
                try {
                    LeerArchivos.ASMFileContent fileContent = fileReader.readFile(archivo);
                    FileInfo fileInfo = createFileInfo(fileContent);
                    fileInfoList.add(fileInfo);
                    
                    // memoria principal
                    Map<Integer, String> allocatedMemory = memory.loadInstructions(fileInfo.getfileContent());
                    fileInfo.getProcessBCP().setBaseAddress(allocatedMemory.keySet().iterator().next());
                    
                    //Estadisticas
                    //processStats.addProcess(fileInfo.getFileName());
                    //statisticsDialog = new StatisticsDialog(view);
                    
                    //Memoria disco
                    Map<Integer, String> allocatedDisk = diskMemory.loadInstructions(fileInfo.getfileContent());
                    
                    //updateMemoryView(allocatedMemory);
                    //updateDiskView(allocatedDisk);
                    
                    int rowIndex = processRowMap.size();
                    processRowMap.put(fileInfo.getFileName(), rowIndex);
                    
                    view.addProcess(fileInfo.getFileName(), fileInfo.getProcessBCP().getState().toString());

                } catch (IOException ex) {
                    view.showErrorMessage("Error al leer el archivo: " + archivo.getName());
                }
                
                if (!fileInfoList.isEmpty()) {
                    FileInfo firstFile = fileInfoList.get(0);
                    updateProcessState(firstFile, BCP.ProcessState.READY);
                }
            }
        }
    }
    
    private void startAutomaticExecution() {
        isExecuting.set(true);
        view.getExecuteButton().setText("Stop");
        
        executionThread = new Thread(() -> {
            while (isExecuting.get()) {
                SwingUtilities.invokeLater(() -> {
                    executeNextInstruction();
                });
                
                try {
                    Thread.sleep(1000); // 2 second delay
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
                
                // Check if all processes are completed
                boolean allCompleted = true;
                for (FileInfo fileInfo : fileInfoList) {
                    if (fileInfo.getProcessBCP().getState() != BCP.ProcessState.TERMINATED) {
                        allCompleted = false;
                        break;
                    }
                }
                
                if (allCompleted) {
                    SwingUtilities.invokeLater(() -> {
                        stopAutomaticExecution();
                        view.showErrorMessage("All processes have been completed.");
                    });
                    break;
                }
            }
        });
        
        executionThread.start();
    }
    
    private void stopAutomaticExecution() {
        isExecuting.set(false);
        if (executionThread != null) {
            executionThread.interrupt();
        }
        view.getExecuteButton().setText("Execute");
    }
    
    private FileInfo createFileInfo(LeerArchivos.ASMFileContent fileContent){
        List<String> fileContent1 = new ArrayList<>(fileContent.getInstructions());
        int size = fileContent.getInstructions().size();

        FileInfo fileInfo = new FileInfo(fileContent.getFileName(), fileContent1, size);
        // Inicializar el BCP para este archivo
        BCP bcp = fileInfo.getProcessBCP();
        bcp.setID(fileInfoList.size() + 1);
        bcp.setState(BCP.ProcessState.NEW);
        bcp.setStartTime(System.currentTimeMillis());
        bcp.setProcessSize(size);
        
        return fileInfo;
    }
    
    private void showStatistics() {
        String[][] statsData = processStats.getStatisticsData();
        //statisticsDialog.updateStatistics(statsData);
        //statisticsDialog.setVisible(true);
    }
    /*
    private void updateMemoryView(Map<Integer, String> memoryContent) {
        view.clearMemoryTable();
        for (Map.Entry<Integer, String> entry : memoryContent.entrySet()) {
            view.addMemoryEntry(String.valueOf(entry.getKey()), entry.getValue());
        }
    }*/
    
    private void highlightMemoryPosition(int position) {
        view.highlightMemoryEntry(position);
    }
    
    private void handleProcessCompletion(FileInfo currentFile) {
        updateProcessState(currentFile, BCP.ProcessState.TERMINATED);
        currentFile.getProcessBCP().setEndTime(System.currentTimeMillis());
        updateBCPDisplay(currentFile.getProcessBCP(), "Proceso terminado");
        
        
        if (currentFileIndex < fileInfoList.size() - 1) {
            currentFileIndex++;
            currentInstructionIndex = 0;
            FileInfo nextFile = fileInfoList.get(currentFileIndex);
            updateProcessState(nextFile, BCP.ProcessState.READY);
            view.clearMemoryTable();
         } else {
            // All processes have been completed
            if (isExecuting.get()) {
                stopAutomaticExecution();
                view.showErrorMessage("All processes have been completed.");
            }
        }
    }
    
    public void resetSystem() {
        fileInfoList.clear();
        currentFileIndex = 0;
        currentInstructionIndex = 0;
        globalDiskIndex = 1;
        processRowMap.clear();
        memory.clearMemory();
        diskMemory.clearMemory();
        processStats.clear();
        //statisticsDialog.dispose();

        // Limpiar todas las vistas
        view.clearMemoryTable();
        view.clearDiskTable();
        view.clearProcessTable();
        view.clearBCPTable();
        //statisticsDialog.clearStatisticsTable();

        // Restablecer la consola
        consolePanel.clear();
    }
    
    private void updateProcessState(FileInfo fileInfo, BCP.ProcessState newState) {
        BCP bcp = fileInfo.getProcessBCP();
        bcp.setState(newState);
        
        // Actualizar el estado en la vista
        Integer rowIndex = processRowMap.get(fileInfo.getFileName());
        if (rowIndex != null) {
            view.updateProcessState(rowIndex, newState.toString());
        }
    }
    
    private void executeNextInstruction() {
        if (fileInfoList.isEmpty()) {
            view.showErrorMessage("No hay procesos cargados.");
            return;
        }
        
        FileInfo currentFile = fileInfoList.get(currentFileIndex);
        BCP currentBCP = currentFile.getProcessBCP();
        List<String> instructions = currentFile.getfileContent();
        
        int baseAddress = currentBCP.getBaseAddress();
        
        if (currentInstructionIndex == 0) {
            updateProcessState(currentFile, BCP.ProcessState.RUNNING);
            
            // Cambiar los procesos NEW a READY
            for (FileInfo file : fileInfoList) {
                if (file.getProcessBCP().getState() == BCP.ProcessState.NEW) {
                    updateProcessState(file, BCP.ProcessState.READY);
                }
            }
        }
        
        if (currentInstructionIndex < instructions.size()) {
            int currentMemoryPosition = baseAddress + currentInstructionIndex;
            String currentInstruction = instructions.get(currentInstructionIndex);
            
            updateMemoryView(currentMemoryPosition, currentInstruction);
            updateDiskView(currentInstruction);
            
            executeInstruction(currentBCP, currentInstruction, currentMemoryPosition);
            updateBCPDisplay(currentBCP, currentInstruction);
            highlightMemoryPosition(currentMemoryPosition);
            
            currentInstructionIndex++;
        } else {
            handleProcessCompletion(currentFile);
        }
    }
    
    private void updateMemoryView(int position, String instruction) {
        view.clearMemoryTable();
        Map<Integer, String> memorySnapshot = memory.getMemorySnapshot();
        for (Map.Entry<Integer, String> entry : memorySnapshot.entrySet()) {
            //if (entry.getKey() <= position) {
                view.addMemoryEntry(String.valueOf(entry.getKey()), entry.getValue());
            //}
        }
        view.highlightMemoryEntry(position);
    }

    private void updateDiskView(String instruction) {
        // Add the new instruction to the disk view
        view.addDiskEntry(String.valueOf(globalDiskIndex), instruction);
        globalDiskIndex++;
    }
    
    private void executeInstruction(BCP bcp, String instruction, int memoryPosition) {
        bcp.setIR(instruction);
        bcp.setPC(memoryPosition);

        String[] parts = instruction.trim().split("\\s+");
        if (parts.length > 0) {
            String operation = parts[0].toUpperCase();

            try {
                switch (operation) {
                    case "LOAD":
                        if (parts.length == 2) {
                            String register = parts[1];
                            bcp.setAC(bcp.getRegister(register));
                        }
                        break;

                    case "STORE":
                        if (parts.length == 2) {
                            String register = parts[1];
                            bcp.setRegister(register, bcp.getAC());
                        }
                        break;

                    case "MOV":
                        if (parts.length == 3) {
                            String destReg = parts[1].replace(",", "");
                            String sourceReg = parts[2];

                            try {
                                // Check if source is a number
                                int value = Integer.parseInt(sourceReg);
                                bcp.setRegister(destReg, value);
                            } catch (NumberFormatException e) {
                                // Source is a register
                                int value = bcp.getRegister(sourceReg);
                                bcp.setRegister(destReg, value);
                            }
                        }
                        break;

                    case "ADD":
                        if (parts.length == 2) {
                            String register = parts[1];
                            int value = bcp.getRegister(register);
                            bcp.setAC(bcp.getAC() + value);
                        }
                        break;

                    case "SUB":
                        if (parts.length == 2) {
                            String register = parts[1];
                            int value = bcp.getRegister(register);
                            bcp.setAC(bcp.getAC() - value);
                        }
                        break;

                    case "INC":
                        if (parts.length == 1) {
                            bcp.setAC(bcp.getAC() + 1);
                        } else if (parts.length == 2) {
                            String register = parts[1];
                            int value = bcp.getRegister(register);
                            bcp.setRegister(register, value + 1);
                        }
                        break;

                    case "DEC":
                        if (parts.length == 1) {
                            bcp.setAC(bcp.getAC() - 1);
                        } else if (parts.length == 2) {
                            String register = parts[1];
                            int value = bcp.getRegister(register);
                            bcp.setRegister(register, value - 1);
                        }
                        break;

                    case "SWAP":
                        if (parts.length == 3) {
                            String reg1 = parts[1].replace(",", "");
                            String reg2 = parts[2];
                            int temp = bcp.getRegister(reg1);
                            bcp.setRegister(reg1, bcp.getRegister(reg2));
                            bcp.setRegister(reg2, temp);
                        }
                        break;

                    case "INT":
                        handleInterrupt(bcp, parts);
                        break;

                    case "JMP":
                        if (parts.length == 2) {
                            int displacement = Integer.parseInt(parts[1]);
                            bcp.setPC(bcp.getPC() + displacement);
                        }
                        break;

                    case "CMP":
                        if (parts.length == 3) {
                            String reg1 = parts[1].replace(",", "");
                            String reg2 = parts[2];
                            int val1 = bcp.getRegister(reg1);
                            int val2 = bcp.getRegister(reg2);
                            bcp.setAC(val1 - val2); // Result used for conditional jumps
                        }
                        break;

                    case "JE":
                    case "JNE":
                        handleConditionalJump(bcp, operation, parts);
                        break;

                    case "PARAM":
                        handleParameters(bcp, parts);
                        break;

                    case "PUSH":
                        if (parts.length == 2) {
                            String register = parts[1];
                            int value = bcp.getRegister(register);
                            pushToStack(bcp, value);
                        }
                        break;

                    case "POP":
                        if (parts.length == 2) {
                            String register = parts[1];
                            int value = popFromStack(bcp);
                            bcp.setRegister(register, value);
                        }
                        break;
                }
            } catch (Exception e) {
                // Handle any execution errors
                System.err.println("Error executing instruction: " + instruction);
                e.printStackTrace();
            }
        }
    }
    
    private FileInfo getCurrentFile() {
        return fileInfoList.get(currentFileIndex);
    }
    
    private void handleInterrupt(BCP bcp, String[] parts) {
        if (parts.length >= 2) {
            String interruptType = parts[1].toUpperCase();
            switch (interruptType) {
                case "20H":
                    bcp.setState(BCP.ProcessState.TERMINATED);
                    consolePanel.printToConsole("Process " + bcp.getID() + " terminated");
                    handleProcessCompletion(getCurrentFile());
                    break;

                case "10H":
                    int outputValue = bcp.getRegister("DX");
                    consolePanel.printToConsole("Output: " + outputValue);
                    break;

                case "09H":
                    SwingUtilities.invokeLater(() -> {
                        consolePanel.printToConsole("Please enter a number (0-255):");

                        // Uso de waitForInput con un callback
                        consolePanel.waitForInput(input -> {
                            try {
                                int value = Integer.parseInt(input);
                                if (value >= 0 && value <= 255) {
                                    bcp.setRegister("DX", value); // Guardar el valor en el registro DX
                                    consolePanel.printToConsole("Input received: " + value);
                                } else {
                                    consolePanel.printToConsole("Invalid input. Number must be between 0 and 255.");
                                }
                            } catch (NumberFormatException e) {
                                consolePanel.printToConsole("Invalid input. Please enter a valid number.");
                            }
                        });
                    });
                    break;

                case "21H":
                    handleFileOperation(bcp);
                    break;
            }
        }
    }
    
    private void handleFileOperation(BCP bcp) {
        int ah = bcp.getRegister("AH");
        String fileName = getStringFromDX(bcp); // Implementar este método
        
        switch (ah) {
            case 0x3C: // Create file
                consolePanel.printToConsole("Creating file: " + fileName);
                // Implementar creación de archivo
                break;
                
            case 0x3D: // Open file
                consolePanel.printToConsole("Opening file: " + fileName);
                // Implementar apertura de archivo
                break;
                
            case 0x4D: // Read file
                consolePanel.printToConsole("Reading from file: " + fileName);
                // Implementar lectura de archivo
                break;
                
            case 0x40: // Write file
                int dataToWrite = bcp.getRegister("AL");
                consolePanel.printToConsole("Writing to file: " + fileName + ", Data: " + dataToWrite);
                // Implementar escritura de archivo
                break;
                
            case 0x41: // Delete file
                consolePanel.printToConsole("Deleting file: " + fileName);
                // Implementar eliminación de archivo
                break;
        }
    }
    
    private void updateDiskView(Map<Integer, String> diskContent) {
        for (Map.Entry<Integer, String> entry : diskContent.entrySet()) {
            view.addDiskEntry(String.valueOf(entry.getKey()), entry.getValue());
        }
    }
    
    // Método auxiliar para obtener el nombre del archivo desde DX
    private String getStringFromDX(BCP bcp) {
        // Esta es una implementación simplificada
        return "file_" + bcp.getRegister("DX") + ".txt";
    }
    
    private void handleConditionalJump(BCP bcp, String operation, String[] parts) {
        if (parts.length == 2) {
            int displacement = Integer.parseInt(parts[1]);
            boolean shouldJump = false;

            if (operation.equals("JE") && bcp.getAC() == 0) {
                shouldJump = true;
            } else if (operation.equals("JNE") && bcp.getAC() != 0) {
                shouldJump = true;
            }

            if (shouldJump) {
                bcp.setPC(bcp.getPC() + displacement);
            }
        }
    }
    
    private void handleParameters(BCP bcp, String[] parts) {
        if (parts.length > 1) {
            for (int i = 1; i < Math.min(parts.length, 4); i++) { // Max 3 parameters
                try {
                    int value = Integer.parseInt(parts[i].replace(",", ""));
                    pushToStack(bcp, value);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid parameter: " + parts[i]);
                }
            }
        }
    }
    
    private void pushToStack(BCP bcp, int value) {
        ArrayList<Integer> stack = bcp.getStack();
        if (stack.size() < 5) {
            stack.add(value);
        }     
    }

    private int popFromStack(BCP bcp) {
        ArrayList<Integer> stack = bcp.getStack();
        if (stack.isEmpty()) {
            throw new RuntimeException("Stack underflow - attempted to pop from empty stack");
        }
        return stack.remove(stack.size() - 1);
    }
    
    private void updateBCPDisplay(BCP bcp, String currentInstruction) {
        String[][] bcpData = {
            {"ID", String.valueOf(bcp.getID())},
            {"Estado", bcp.getState().toString()},
            {"Contador de Programa", String.valueOf(bcp.getPC())},
            {"Instrucción Actual", bcp.getIR()},
            {"Pila", bcp.getStackAsString()},
            {"Registro AC", String.valueOf(bcp.getAC())},
            {"Registro AX", String.valueOf(bcp.getRegister("AX"))},
            {"Registro BX", String.valueOf(bcp.getRegister("BX"))},
            {"Registro CX", String.valueOf(bcp.getRegister("CX"))},
            {"Registro DX", String.valueOf(bcp.getRegister("DX"))},
            {"Tiempo de inicio", bcp.getStartTime()},
            {"Tiempo de final", bcp.getEndTime()},
            {"Tamano del proceso", String.valueOf(bcp.getProcessSize())},
            {"Tiempo total", bcp.getTotalTime()}
        };
        System.out.println(bcp.getID());
        
        view.updateBCPTable(bcpData);
        view.SetNext(currentInstruction);
    }

    public void mostrarVista() {
        view.setVisible(true);
    }
    
    /**
     * Obtiene la lista de todos los FileInfo
     * @return Lista inmutable de FileInfo
     */
    public List<FileInfo> getFileInfoList() {
        return Collections.unmodifiableList(fileInfoList);
    }

    /**
     * Libera la memoria asociada a un FileInfo
     * @param fileInfo El FileInfo cuya memoria se debe liberar
     */
    public void removeFileInfo(FileInfo fileInfo) {
        //memory.freeMemory(fileInfo.getMemoryAddress());
        fileInfoList.remove(fileInfo);
    }
}
