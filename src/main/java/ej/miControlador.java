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
    private TableColumn<Empleado, String> surname;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDel;

    @FXML
    private Button btnExportJSON;

    @FXML
    private Button btnExportXML;

    @FXML
    private Button btnImportJSON;

    @FXML
    private Button btnImportXML;

    @FXML
    private Button btnInsert;

    @FXML
    private TableColumn<Empleado, String> depart;

    @FXML
    private TableColumn<Empleado, Integer> id;

    @FXML
    private MenuItem menuExport;

    @FXML
    private MenuItem menuImport;

    @FXML
    private TableColumn<Empleado, String> name;

    @FXML
    private TableColumn<Empleado, Double> sueldo;

    @FXML
    private TableView<Empleado> tableView;

    @FXML
    private TextField txtSurname;

    @FXML
    private TextField txtDepartamento;

    @FXML
    private Label txtInfo;

    @FXML
    private TextField txtname;

    @FXML
    private TextField txtSueldo;


    @FXML
    void actualizarEmpleado() {
        Empleado empleadoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            showAlert(Alert.AlertType.ERROR, "ERROR", "Por favor, selecciona un empleado para actualizar.");
            return;
        }

        if (txtname.getText().isEmpty() ||
                txtSurname.getText().isEmpty() ||
                txtDepartamento.getText().isEmpty() ||
                txtSueldo.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "ERROR", "Rellene todos los campos e inténtelo de nuevo.");
            return;
        }

        try {
            String name = txtname.getText();
            String surname = txtSurname.getText();
            String departamento = txtDepartamento.getText();
            Double sueldo = Double.parseDouble(txtSueldo.getText());

            if (!validateCampos(name, surname, departamento, sueldo)) {
                return;
            }

            // Crear un nuevo empleado con los datos actualizados
            Empleado nuevoEmpleado = new Empleado(empleadoSeleccionado.getId(), name, surname, departamento, sueldo);
            int index = tableView.getItems().indexOf(empleadoSeleccionado);
            tableView.getItems().set(index, nuevoEmpleado);

            // Guardar cambios en el archivo binario
            saveEmpleados();

            // Limpiar campos
            txtname.clear();
            txtSurname.clear();
            txtDepartamento.clear();
            txtSueldo.clear();

            showAlert(Alert.AlertType.INFORMATION, "Actualización Exitosa", "El empleado ha sido actualizado correctamente.");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "ERROR", "Sueldo debe ser un número válido.");
        }
    }



    @FXML
    void borrarEmpleado() {
        Empleado empleadoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado == null) {
            showAlert(Alert.AlertType.ERROR, "ERROR", "Por favor, selecciona un empleado para borrar.");
            return;
        }

        int index = tableView.getItems().indexOf(empleadoSeleccionado);
        tableView.getItems().remove(index);

        // Guardar cambios en el archivo binario
        saveEmpleados();

        // Limpiar campos
        txtname.clear();
        txtSurname.clear();
        txtDepartamento.clear();
        txtSueldo.clear();

        showAlert(Alert.AlertType.INFORMATION, "Borrado Exitoso", "El empleado ha sido eliminado correctamente.");
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
        File file = fileChooser.showSaveDialog(btnInsert.getScene().getWindow());
        if (file != null) {
            showAlert(AlertType.INFORMATION, "Archivo guardado", "El archivo ha sido guardado en:\n" + file.getAbsolutePath());
        }
    }

    @FXML
    void exportarJSON() {
        File file = new File(configuracion.getFicheroJson());

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new Gson();
            String json = gson.toJson(tableView.getItems());
            writer.write(json);
            showAlert(AlertType.INFORMATION, "Éxito", "Empleados exportados a JSON correctamente.");
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "ERROR", "Error al exportar a JSON: " + e.getMessage());
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
                empleado.appendChild(createElement(doc, "name", e.getName()));
                empleado.appendChild(createElement(doc, "surname", e.getSurname()));
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

            showAlert(AlertType.INFORMATION, "Éxito", "Empleados exportados a XML correctamente.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "ERROR", "Error al exportar a XML: " + e.getMessage());
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
        File file = fileChooser.showOpenDialog(btnInsert.getScene().getWindow());
        if (file != null) {
            showAlert(AlertType.INFORMATION, "Archivo seleccionado", "El archivo seleccionado es:\n" + file.getAbsolutePath());
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

            showAlert(AlertType.INFORMATION, "Éxito", "Empleados importados desde JSON correctamente.");
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "ERROR", "Error al importar desde JSON: " + e.getMessage());
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
                    String name = element.getElementsByTagName("name").item(0).getTextContent();
                    String surname = element.getElementsByTagName("surname").item(0).getTextContent();
                    String departamento = element.getElementsByTagName("Departamento").item(0).getTextContent();
                    double sueldo = Double.parseDouble(element.getElementsByTagName("Sueldo").item(0).getTextContent());
                    empleadosList.add(new Empleado(id, name, surname, departamento, sueldo));
                }
            }

            tableView.setItems(empleadosList);
            showAlert(AlertType.INFORMATION, "Éxito", "Empleados importados desde XML correctamente.");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "ERROR", "Error al importar desde XML: " + e.getMessage());
        }
    }


    @FXML
    void insertarEmpleado() {
        if (txtname.getText().isEmpty() ||
                txtSurname.getText().isEmpty() ||
                txtDepartamento.getText().isEmpty() ||
                txtSueldo.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "ERROR", "¡Faltan datos! Completa todos los campos e inténtalo de nuevo.");
            return;
        }

        try {
            String name = txtname.getText();
            String surname = txtSurname.getText();
            String departamento = txtDepartamento.getText();
            Double sueldo = Double.parseDouble(txtSueldo.getText());

            if (!validateCampos(name, surname, departamento, sueldo)) {
                return;
            }

            int ultimoId = configuracion.getIdEmpleado();
            Empleado nuevoEmpleado = new Empleado(ultimoId + 1, name, surname, departamento, sueldo);
            tableView.getItems().add(nuevoEmpleado);

            // Actualizar el último ID en la configuración
            configuracion.setIdEmpleado(ultimoId + 1);
            updateLastId(ultimoId + 1);


            saveEmpleados();
            // Limpiar campos
            txtname.clear();
            txtSurname.clear();
            txtDepartamento.clear();
            txtSueldo.clear();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "ERROR", "El sueldo debe ser un número válido.");
        }
    }




    private boolean validateCampos(String name, String surname, String departamento,Double sueldo) {

        if (name.length() > 30) {
            showAlert(AlertType.ERROR,"ERROR","El nombre no puede superar los 30 caracteres.");
            txtInfo.setText("Info: El nombre no puede superar los 30 caracteres.");
            return false;
        }

        if (surname.length() > 60) {
            showAlert(AlertType.ERROR,"ERROR", "Los apellidos no pueden superar los 60 caracteres.");
            txtInfo.setText("Info: Los apellidos no pueden superar los 60 caracteres.");
            return false;
        }

        if (departamento.length() > 30) {
            showAlert(AlertType.ERROR,"ERROR","El departamento no puede superar los 30 caracteres.");
            txtInfo.setText("Info: El departamento no puede superar los 30 caracteres.");
            return false;
        }

        try {
            if (sueldo < 0 || sueldo > 99999.99) {
                showAlert(AlertType.ERROR,"ERROR","El sueldo debe estar entre 0 y 99,999.99.");
                txtInfo.setText("Info: El sueldo debe estar entre 0 y 99,999.99.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR,"ERROR","El sueldo debe ser un número válido.");
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
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        depart.setCellValueFactory(new PropertyValueFactory<>("departamento"));
        sueldo.setCellValueFactory(new PropertyValueFactory<>("sueldo"));

        tableView.setItems(empleados);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtname.setText(newValue.getName());
                txtSurname.setText(newValue.getSurname());
                txtDepartamento.setText(newValue.getDepartamento());
                txtSueldo.setText(String.valueOf(newValue.getSueldo()));
            }
        });
    }

    private void showAlert(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    private void updateLastId(int nuevoId) {
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
            showAlert(Alert.AlertType.ERROR, "ERROR", "Error al actualizar el archivo de configuración: " + e.getMessage());
        }
    }

    public void saveEmpleados() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("empleados.dat"))) {
            // Obtenemos la lista de empleados de la TableView
            List<Empleado> empleados = new ArrayList<>(tableView.getItems());
            out.writeObject(empleados);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "ERROR", "No se pudo guardar los empleados.");
        }
    }




}