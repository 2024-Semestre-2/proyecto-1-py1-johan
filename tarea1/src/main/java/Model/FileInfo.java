package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileInfo {
    private String fileName;
    private List<String> fileContent;
    private BCP processBCP;
    
    public FileInfo(String fileName, List<String> fileContent, int size) {
        this.fileName = fileName;
        setFileContent(fileContent);
        this.processBCP = new BCP(size);
    }
    
    // Getters and setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public BCP getProcessBCP() { return processBCP; }
    public void setProcessBCP(BCP processBCP) { this.processBCP = processBCP; }
    
    @Override
    public String toString() {
        return "File: " + fileName + 
               ", File Content: " + fileContent + 
               ", State: " + processBCP.getState();
    }
    
    public List<String> getfileContent() { return fileContent; }
    public void setFileContent(List<String> fileContent) {this.fileContent = fileContent;}
}