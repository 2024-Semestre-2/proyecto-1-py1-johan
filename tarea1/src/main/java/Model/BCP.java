package Model;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BCP {
    public enum ProcessState {
        NEW,
        READY,
        RUNNING,
        WAITING,
        TERMINATED
    }
    private int ID;
    private ProcessState state;
    private String IR;
    private int PC;
    private int AC;
    private int AX;
    private int BX;
    private int CX;
    private int DX;
    private ArrayList<Integer> Stack;
    
    private long startTime;
    private long endTime;
    private int totalTime;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
    // Memory management
    private int baseAddress;
    private int processSize;
    private int priority;
    
    // Linked list implementation for BCP chain
    private FileInfo nextFile;
    
    public BCP(int size) 
    {
        this.state = ProcessState.NEW;
        this.ID++;
        this.PC = 0;
        this.AC = 0;
        this.AX = 0;
        this.BX = 0;
        this.CX = 0;
        this.DX = 0;
        this.IR = "";
        this.Stack = new ArrayList<Integer>();
        this.startTime = System.currentTimeMillis();
        
        this.processSize = size;
        this.priority = ID;
        this.nextFile = nextFile;
    }
    
    // Getters and setters
    public ProcessState getState() { return state; }
    public void setState(ProcessState state) { this.state = state; }
    
    public int getPC() { return PC; }
    public void setPC(int programCounter) { this.PC = programCounter; }
    
    public int getBaseAddress() { return baseAddress; }
    public void setBaseAddress(int baseAddress) { this.baseAddress = baseAddress; }
    
    public int getProcessSize() { return processSize; }
    public void setProcessSize(int processSize) { this.processSize = processSize; }
    
    public FileInfo getNextFile() { return nextFile; }
    public void setNextFile(FileInfo nextFile) { this.nextFile = nextFile; }
    
    public int getID() {return ID;}
    public void setID(int ID) {this.ID = ID;}

    public String getIR() {return IR;}
    public void setIR(String IR) {this.IR = IR;}

    public int getAC() {return AC;}
    public void setAC(int AC) {this.AC = AC;}

    public ArrayList<Integer> getStack() {return Stack;}
    public void setStack(ArrayList<Integer> Stack) {this.Stack = Stack;}
    public String getStackAsString() {return Stack.toString();}

    public String getStartTime() {return timeFormat.format(new Date(startTime));}
    public void setStartTime(long startTime) {this.startTime = startTime;}

    public void setEndTime(long endTime) {this.endTime = endTime;}
    public String getEndTime() {return endTime > 0 ? timeFormat.format(new Date(endTime)) : "N/A";}

    public String getTotalTime() {
        if (endTime > 0) {
            long diffMillis = endTime - startTime;
            long diffSeconds = diffMillis / 1000;
            return String.format("%02d:%02d:%02d", 
                diffSeconds / 3600, 
                (diffSeconds % 3600) / 60, 
                diffSeconds % 60);
        }
        return "N/A";
    }
    
    public void setTotalTime(int totalTime) {this.totalTime = totalTime;}

    public int getPriority() {return priority;}

    public void setPriority(int priority) {this.priority = priority;}
    
    // Register operations
    public void setRegister(String register, int value) {
        switch (register.toUpperCase()) {
            case "AX": AX = value; break;
            case "BX": BX = value; break;
            case "CX": CX = value; break;
            case "DX": DX = value; break;
        }
    }
    
    public int getRegister(String register) {
        switch (register.toUpperCase()) {
            case "AX": return AX;
            case "BX": return BX;
            case "CX": return CX;
            case "DX": return DX;
            default: return 0;
        }
    }
    
    public void clearRegisters() {
        setRegister("AX", 0);
        setRegister("BX", 0);
        setRegister("CX", 0);
        setRegister("DX", 0);
    }
}