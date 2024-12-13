package ej;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class miControlador implements Initializable {

    private Configuracion configuracion = new Configuracion();
    @FXML
    private TableColumn<Empleado, String> apellidos;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnExportarJSON;

    @FXML
    private Button btnExportarXML;

    @FXML
    private Button btnImportarJSON;

    @FXML
    private Button btnImportarXML;

    @FXML
    private Button btnInsertar;

    @FXML
    private TableColumn<Empleado, String> depart;

    @FXML
    private TableColumn<Empleado, Integer> id;

    @FXML
    private MenuItem menuExportar;

    @FXML
    private MenuItem menuImportar;

    @FXML
    private TableColumn<Empleado, String> nombre;

    @FXML
    private TableColumn<Empleado, Double> sueldo;

    @FXML
    private TableView<Empleado> tableView;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtDepartamento;

    @FXML
    private Label txtInfo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtSueldo;


    @FXML
    void actualizarEmpleado() {
        Empleado empleadoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Por favor, selecciona un empleado para actualizar.");
            return;
        }

        if (txtNombre.getText().isEmpty() ||
                txtApellidos.getText().isEmpty() ||
                txtDepartamento.getText().isEmpty() ||
                txtSueldo.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Completa todos los campos e inténtalo de nuevo.");
            return;
        }

        try {
            String nombre = txtNombre.getText();
            String apellidos = txtApellidos.getText();
            String departamento = txtDepartamento.getText();
            Double sueldo = Double.parseDouble(txtSueldo.getText());

            if (!validateCampos(nombre, apellidos, departamento, sueldo)) {
                return;
            }

            // Crear un nuevo empleado con los datos actualizados
            Empleado nuevoEmpleado = new Empleado(empleadoSeleccionado.getId(), nombre, apellidos, departamento, sueldo);
            int index = tableView.getItems().indexOf(empleadoSeleccionado);
            tableView.getItems().set(index, nuevoEmpleado);

            // Guardar cambios en el archivo binario
            guardarEmpleados();

            // Limpiar campos
            txtNombre.clear();
            txtApellidos.clear();
            txtDepartamento.clear();
            txtSueldo.clear();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización Exitosa", "El empleado ha sido actualizado correctamente.");

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Sueldo debe ser un número válido.");
        }
    }



    @FXML
    void borrarEmpleado() {
        Empleado empleadoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Por favor, selecciona un empleado para borrar.");
            return;
        }

        int index = tableView.getItems().indexOf(empleadoSeleccionado);
        tableView.getItems().remove(index);

        // Guardar cambios en el archivo binario
        guardarEmpleados();

        // Limpiar campos
        txtNombre.clear();
        txtApellidos.clear();
        txtDepartamento.clear();
        txtSueldo.clear();

        mostrarAlerta(Alert.AlertType.INFORMATION, "Borrado Exitoso", "El empleado ha sido eliminado correctamente.");
    }



    @FXML
    void exportarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Archivo");
        fileChooser.setInitialFileName("archivo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos JSON", "*.json"),
                new FileChooser.ExtensionFilter("Archivos XML", "*.xml")
        );
        File file = fileChooser.showSaveDialog(btnInsertar.getScene().getWindow());
        if (file != null) {
            mostrarAlerta(AlertType.INFORMATION, "Archivo guardado", "El archivo ha sido guardado en:\n" + file.getAbsolutePath());
        }
    }

    @FXML
    void exportarJSON() {
        File file = new File(configuracion.getFicheroJson());

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new Gson();
            String json = gson.toJson(tableView.getItems());
            writer.write(json);
            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Empleados exportados a JSON correctamente.");
        } catch (IOException e) {
            mostrarAlerta(AlertType.ERROR, "ERROR", "Error al exportar a JSON: " + e.getMessage());
        }
    }



    @FXML
    void exportarXML() {
        File file = new File(configuracion.getFicheroXml());

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Empleados");
            doc.appendChild(rootElement);

            for (Empleado e : tableView.getItems()) {
                Element empleado = doc.createElement("Empleado");
                empleado.setAttribute("id", String.valueOf(e.getId()));
                empleado.appendChild(createElement(doc, "Nombre", e.getNombre()));
                empleado.appendChild(createElement(doc, "Apellidos", e.getApellidos()));
                empleado.appendChild(createElement(doc, "Departamento", e.getDepartamento()));
                empleado.appendChild(createElement(doc, "Sueldo", String.valueOf(e.getSueldo())));
                rootElement.appendChild(empleado);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Empleados exportados a XML correctamente.");
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "ERROR", "Error al exportar a XML: " + e.getMessage());
        }
    }

    private org.w3c.dom.Element createElement(org.w3c.dom.Document doc, String name, String value) {
        org.w3c.dom.Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        return element;
    }


    @FXML
    void importarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Archivo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos JSON", "*.json"),
                new FileChooser.ExtensionFilter("Archivos XML", "*.xml")
        );
        File file = fileChooser.showOpenDialog(btnInsertar.getScene().getWindow());
        if (file != null) {
            mostrarAlerta(AlertType.INFORMATION, "Archivo seleccionado", "El archivo seleccionado es:\n" + file.getAbsolutePath());
        }
    }

    @FXML
    void importarJSON() {
        File file = new File(configuracion.getFicheroJson());

        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Empleado[] empleadosArray = gson.fromJson(reader, Empleado[].class);
            ObservableList<Empleado> empleadosList = FXCollections.observableArrayList(empleadosArray);
            tableView.setItems(empleadosList); // Reemplaza la lista existente

            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Empleados importados desde JSON correctamente.");
        } catch (IOException e) {
            mostrarAlerta(AlertType.ERROR, "ERROR", "Error al importar desde JSON: " + e.getMessage());
        }
    }



    @FXML
    void importarXML() {
        File file = new File(configuracion.getFicheroXml());

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Empleado");
            ObservableList<Empleado> empleadosList = FXCollections.observableArrayList();

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    int id = Integer.parseInt(element.getAttribute("id"));
                    String nombre = element.getElementsByTagName("Nombre").item(0).getTextContent();
                    String apellidos = element.getElementsByTagName("Apellidos").item(0).getTextContent();
                    String departamento = element.getElementsByTagName("Departamento").item(0).getTextContent();
                    double sueldo = Double.parseDouble(element.getElementsByTagName("Sueldo").item(0).getTextContent());
                    empleadosList.add(new Empleado(id, nombre, apellidos, departamento, sueldo));
                }
            }

            tableView.setItems(empleadosList);
            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Empleados importados desde XML correctamente.");
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "ERROR", "Error al importar desde XML: " + e.getMessage());
        }
    }


    @FXML
    void insertarEmpleado() {
        if (txtNombre.getText().isEmpty() ||
                txtApellidos.getText().isEmpty() ||
                txtDepartamento.getText().isEmpty() ||
                txtSueldo.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "¡Faltan datos! Completa todos los campos e inténtalo de nuevo.");
            return;
        }

        try {
            String nombre = txtNombre.getText();
            String apellidos = txtApellidos.getText();
            String departamento = txtDepartamento.getText();
            Double sueldo = Double.parseDouble(txtSueldo.getText());

            if (!validateCampos(nombre, apellidos, departamento, sueldo)) {
                return;
            }

            int ultimoId = configuracion.getIdEmpleado();
            Empleado nuevoEmpleado = new Empleado(ultimoId + 1, nombre, apellidos, departamento, sueldo);
            tableView.getItems().add(nuevoEmpleado);

            // Actualizar el último ID en la configuración
            configuracion.setIdEmpleado(ultimoId + 1);
            actualizarUltimoId(ultimoId + 1);


            guardarEmpleados();
            // Limpiar campos
            txtNombre.clear();
            txtApellidos.clear();
            txtDepartamento.clear();
            txtSueldo.clear();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Sueldo debe ser un número válido.");
        }
    }




    private boolean validateCampos(String nombre, String apellidos, String departamento,Double sueldo) {

        if (nombre.length() > 30) {
            mostrarAlerta(AlertType.ERROR,"ERROR","El nombre no puede tener más de 30 caracteres.");
            txtInfo.setText("Info: El nombre no puede tener más de 30 caracteres.");
            return false;
        }

        if (apellidos.length() > 60) {
            mostrarAlerta(AlertType.ERROR,"ERROR", "Los apellidos no pueden tener más de 60 caracteres.");
            txtInfo.setText("Info: Los apellidos no pueden tener más de 60 caracteres.");
            return false;
        }

        if (departamento.length() > 30) {
            mostrarAlerta(AlertType.ERROR,"ERROR","El departamento no puede tener más de 30 caracteres.");
            txtInfo.setText("Info: El departamento no puede tener más de 30 caracteres.");
            return false;
        }

        try {
            if (sueldo < 0 || sueldo > 99999.99) {
                mostrarAlerta(AlertType.ERROR,"ERROR","El sueldo debe estar entre 0 y 99,999.99.");
                txtInfo.setText("Info: El sueldo debe estar entre 0 y 99,999.99.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(AlertType.ERROR,"ERROR","El sueldo debe ser un número válido.");
            txtInfo.setText("Info: El sueldo debe ser un número válido.");
            return false;
        }

        txtInfo.setText("Info:");
        return true;

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Vista FXML Cargada");

        ObservableList<Empleado> empleados = FXCollections.observableArrayList();
        ArchivoBinario archivoBinario = new ArchivoBinario();
        empleados.addAll(archivoBinario.leerEmpleados(configuracion.getFicheroBinario()));

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        depart.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        sueldo.setCellValueFactory(new PropertyValueFactory<>("sueldo"));

        tableView.setItems(empleados);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtNombre.setText(newValue.getNombre());
                txtApellidos.setText(newValue.getApellidos());
                txtDepartamento.setText(newValue.getDepartamento());
                txtSueldo.setText(String.valueOf(newValue.getSueldo()));
            }
        });
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    private void actualizarUltimoId(int nuevoId) {
        configuracion.setIdEmpleado(nuevoId);

        Properties properties = new Properties();
        properties.setProperty("fichero_binario", configuracion.getFicheroBinario());
        properties.setProperty("fichero_xml", configuracion.getFicheroXml());
        properties.setProperty("fichero_json", configuracion.getFicheroJson());
        properties.setProperty("id_empleado", String.valueOf(nuevoId));

        String rutaRelativa = System.getProperty("user.dir") + "/config.properties";
        try (OutputStream output = new FileOutputStream(rutaRelativa)) {
            properties.store(output, null);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Error al actualizar el archivo de configuración: " + e.getMessage());
        }
    }

    public void guardarEmpleados() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("empleados.dat"))) {
            // Obtén la lista de empleados de la TableView
            List<Empleado> empleados = new ArrayList<>(tableView.getItems());
            out.writeObject(empleados);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "No se pudo guardar los empleados.");
        }
    }




}