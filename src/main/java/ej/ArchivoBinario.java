package ej;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona la escritura y lectura de empleados en un archivo binario.
 */
public class ArchivoBinario {

    /**
     * Escribe una lista de empleados en un archivo binario.
     *
     * @param ruta      la ruta del archivo donde se escribirán los empleados
     * @param empleados la lista de empleados a escribir en el archivo
     */
    public void escribirEmpleados(String ruta, List<Empleado> empleados) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            for (Empleado empleado : empleados) {
                oos.writeObject(empleado);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lee empleados desde un archivo binario y devuelve una lista de ellos.
     *
     * @param archivo la ruta del archivo desde donde se leerán los empleados
     * @return una lista de empleados leídos del archivo
     */
    public ArrayList<Empleado> leerEmpleados(String archivo) {
        ArrayList<Empleado> empleados = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            empleados = (ArrayList<Empleado>) ois.readObject();  // Castear a ArrayList<Empleado>
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return empleados;
    }

}
