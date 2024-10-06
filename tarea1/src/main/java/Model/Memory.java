package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Memory {
    private TreeMap<Integer, String> mainMemory;
    private SystemConfig config;
    private Random random;
    private int nextPosition;

    public Memory(SystemConfig config) {
        this.config = config;
        this.mainMemory = new TreeMap<>();
        this.random = new Random();
        this.nextPosition = 0;
    }

    public Map<Integer, String> loadInstructions(List<String> instructions) {
        int size = instructions.size();
        Map<Integer, String> allocatedPositions = new TreeMap<>();
        
        boolean allocated = false;
        while (!allocated) {
            int basePosition = generateRandomPosition();
            if (isSpaceAvailable(basePosition, size)) {
                // Almacenar instrucciones en posiciones consecutivas
                for (int i = 0; i < size; i++) {
                    int position = basePosition + i;
                    mainMemory.put(position, instructions.get(i));
                    allocatedPositions.put(position, instructions.get(i));
                }
                allocated = true;
            }
        }
        
        return allocatedPositions;
    }

    private int generateRandomPosition() {
        return random.nextInt(config.getMemorySize());
    }

    private boolean isSpaceAvailable(int basePosition, int size) {
        // Verificar que hay espacio suficiente desde la posición base
        if (basePosition + size > config.getMemorySize()) {
            return false;
        }

        // Verificar que todas las posiciones necesarias están libres
        for (int i = 0; i < size; i++) {
            if (mainMemory.containsKey(basePosition + i)) {
                return false;
            }
        }
        
        return true;
    }

    public String getInstruction(int position) {
        return mainMemory.get(position);
    }

    public Map<Integer, String> getMemorySnapshot() {
        return new TreeMap<>(mainMemory);
    }
    
    public void clearMemory() {
        mainMemory.clear();
    }
}