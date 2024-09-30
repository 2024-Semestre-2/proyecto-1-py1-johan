package Model;

import java.util.ArrayList;
import java.util.List;

public class BCP {
    // Estados posibles de un proceso
    public enum Estado {
        NUEVO, PREPARADO, EJECUCION, ESPERA, FINALIZADO
    }

    private String nombreProceso; // Nombre del archivo asociado
    private Estado estado; // Estado del proceso
    private int programCounter; // Contador del programa
    private int[] registros = new int[5]; // Registros AC, AX, BX, CX, DX
    private int[] pila = new int[5]; // Pila del proceso
    private int pilaTop = -1; // Apunta al tope de la pila
    private long tiempoInicio; // Marca de tiempo de inicio
    private long tiempoEmpleando; // Tiempo empleado
    private List<BCP> procesos; // Lista de procesos
    private int accumulator;

    // Constructor
    public BCP(String nombreProceso) {
        this.nombreProceso = nombreProceso;
        this.estado = Estado.NUEVO; // Estado inicial
        this.tiempoInicio = System.currentTimeMillis(); // Marca de tiempo de inicio
        this.procesos = new ArrayList<>(); // Inicializar la lista de procesos
    }

    // Métodos para manejar la pila
    public void push(int valor) throws Exception {
        if (pilaTop >= pila.length - 1) {
            throw new Exception("Desbordamiento de pila");
        }
        pila[++pilaTop] = valor;
    }

    public int pop() throws Exception {
        if (pilaTop < 0) {
            throw new Exception("Pila vacía");
        }
        return pila[pilaTop--];
    }

    // Métodos para actualizar el estado
    public void actualizarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
    }

    // Getters
    public String getNombreProceso() {
        return nombreProceso;
    }

    public Estado getEstado() {
        return estado;
    }

    public long calcularTiempoEjecucion() {
        return System.currentTimeMillis() - this.tiempoInicio;
    }

    public List<BCP> getProcesos() {
        return procesos;
    }

    // Agregar un nuevo proceso
    public void agregarProceso(BCP bcp) {
        procesos.add(bcp);
    }
    
    public int getProgramCounter() {
        return programCounter;
    }

    public int getAccumulator() {
        return accumulator;
    }

    @Override
    public String toString() {
        return "Proceso: " + nombreProceso + ", Estado: " + estado;
    }
}
