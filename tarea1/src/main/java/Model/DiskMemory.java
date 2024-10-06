package Model;

import java.util.TreeMap;
import java.util.List;
import java.util.Map;

public class DiskMemory {
    private TreeMap<Integer, String> diskMemory;
    private int nextPosition;

    public DiskMemory() {
        this.diskMemory = new TreeMap<>();
        this.nextPosition = 1;
    }

    public Map<Integer, String> loadInstructions(List<String> instructions) {
        Map<Integer, String> allocatedPositions = new TreeMap<>();
        
        for (String instruction : instructions) {
            diskMemory.put(nextPosition, instruction);
            allocatedPositions.put(nextPosition, instruction);
            nextPosition++;
        }
        
        return allocatedPositions;
    }

    public String getInstruction(int position) {
        return diskMemory.get(position);
    }

    public Map<Integer, String> getDiskSnapshot() {
        return new TreeMap<>(diskMemory);
    }
    
    public void clearMemory() {
        diskMemory.clear();
    }
}
