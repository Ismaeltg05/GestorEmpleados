package ej;
import java.io.Serializable;

public class Empleado implements Serializable {
    private static final long serialVersionUID = 1L; // ID de versión para la serialización
    private int id;
    private String nombre;
    private String apellidos;
    private String departamento;
    private double sueldo;

    public Empleado(int id, String nombre, String apellidos, String departamento, double sueldo) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.departamento = departamento;
        this.sueldo = sueldo;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getDepartamento() { return departamento; }
    public double getSueldo() { return sueldo; }
}
