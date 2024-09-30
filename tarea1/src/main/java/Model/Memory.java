package Model;

public class Memory {
    private byte[] memory;
    private int osSize;
    private int userSize;

    public Memory(int totalSize, int osSize, int userSize) {
        if (osSize + userSize > totalSize) {
            throw new IllegalArgumentException("El tamaño total de la memoria no es suficiente.");
        }
        this.memory = new byte[totalSize];
        this.osSize = osSize;
        this.userSize = userSize;
    }

    public void loadProgram(int startAddress, byte[] program) {
        if (startAddress + program.length > osSize + userSize) {
            throw new IllegalArgumentException("El programa excede el tamaño de la memoria.");
        }
        System.arraycopy(program, 0, memory, startAddress, program.length);
    }

    public byte[] readMemory(int startAddress, int length) {
        if (startAddress + length > memory.length) {
            throw new IllegalArgumentException("Se intenta leer fuera del rango de memoria.");
        }
        byte[] data = new byte[length];
        System.arraycopy(memory, startAddress, data, 0, length);
        return data;
    }

    public void clearMemory() {
        for (int i = 0; i < memory.length; i++) {
            memory[i] = 0;
        }
    }

    public String displayMemory() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < memory.length; i++) {
            sb.append(String.format("%02X ", memory[i]));
            if ((i + 1) % 16 == 0) sb.append("\n");
        }
        return sb.toString();
    }
}