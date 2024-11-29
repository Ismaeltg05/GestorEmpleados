package ej;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase que gestiona la configuración de la aplicación.
 * Carga y guarda propiedades desde un archivo de configuración.
 */
public class Configuracion {
    private Properties propiedades = new Properties();
    private String ficheroBinario; // Ruta del archivo binario
    private String ficheroXml; // Ruta del archivo XML
    private String ficheroJson; // Ruta del archivo JSON
    private int idEmpleado; // Identificador del empleado

    /**
     * Constructor de la clase Configuracion.
     * Carga la configuración desde el archivo al instanciar el objeto.
     */
    public Configuracion() {
        cargarConfiguracion();
    }

    /**
     * Carga la configuración desde el archivo de propiedades.
     * Se leen las propiedades y se asignan a los atributos correspondientes.
     */
    private void cargarConfiguracion() {
        String rutaRelativa = System.getProperty("user.dir") + "/config.properties";
        try (InputStream input = new FileInputStream(rutaRelativa)) {
            propiedades.load(input);
            ficheroBinario = propiedades.getProperty("fichero_binario");
            ficheroXml = propiedades.getProperty("fichero_xml");
            ficheroJson = propiedades.getProperty("fichero_json");
            idEmpleado = Integer.parseInt(propiedades.getProperty("id_empleado"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Obtiene la ruta del archivo binario.
     *
     * @return la ruta del archivo binario
     */
    public String getFicheroBinario() {
        return ficheroBinario;
    }

    /**
     * Establece la ruta del archivo binario.
     *
     * @param ficheroBinario la nueva ruta del archivo binario
     */
    public void setFicheroBinario(String ficheroBinario) {
        this.ficheroBinario = ficheroBinario;
    }

    /**
     * Establece la ruta del archivo XML.
     *
     * @param ficheroXml la nueva ruta del archivo XML
     */
    public void setFicheroXml(String ficheroXml) {
        this.ficheroXml = ficheroXml;
    }

    /**
     * Establece la ruta del archivo JSON.
     *
     * @param ficheroJson la nueva ruta del archivo JSON
     */
    public void setFicheroJson(String ficheroJson) {
        this.ficheroJson = ficheroJson;
    }

    /**
     * Obtiene el identificador del empleado.
     *
     * @return el id del empleado
     */
    public int getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Establece el identificador del empleado.
     * También guarda el nuevo id en el archivo de configuración.
     *
     * @param idEmpleado el nuevo id del empleado
     */
    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
        // Aquí se podría guardar inmediatamente el nuevo id en el archivo de configuración
        guardarIdEmpleado();
    }

    /**
     * Guarda el identificador del empleado en el archivo de propiedades.
     */
    private void guardarIdEmpleado() {
        propiedades.setProperty("id_empleado", String.valueOf(idEmpleado));
        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            propiedades.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la ruta del archivo XML.
     *
     * @return la ruta del archivo XML
     */
    public String getFicheroXml() {
        return ficheroXml;
    }

    /**
     * Obtiene la ruta del archivo JSON.
     *
     * @return la ruta del archivo JSON
     */
    public String getFicheroJson() {
        return ficheroJson;
    }
}
