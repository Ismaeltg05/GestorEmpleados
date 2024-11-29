package ej;
import java.io.Serializable;

public class Empleado implements Serializable {
    private static final long serialVersionUID = 1L; // ID de versión para la serialización
    private int id;
    private String name;
    private String surname;
    private String departamento;
    private double sueldo;

    public Empleado(int id, String nombre, String apellidos, String departamento, double sueldo) {
        this.id = id;
        this.name = nombre;
        this.surname = apellidos;
        this.departamento = departamento;
        this.sueldo = sueldo;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getDepartamento() { return departamento; }
    public double getSueldo() { return sueldo; }
}
