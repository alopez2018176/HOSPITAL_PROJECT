package org.adolfolopez.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.adolfolopez.bean.Paciente;
import org.adolfolopez.sistema.Principal;
import eu.schudt.javafx.controls.calendar.DatePicker;   
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.adolfolopez.db.Conexion;
import org.adolfolopez.report.GenerarReporte;

public class PacientesController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fecha;
    
    @FXML private TextField txtDPI;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNombres;
    @FXML private TextField txtEdad;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOcupacion;
    @FXML private TextField txtSexo;
    @FXML private TableView tblPacientes;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colDPI;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colEdad;
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colOcupacion;
    @FXML private TableColumn colSexo;
    @FXML private GridPane grpFecha;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
     public void desactivarControles(){
        txtDPI.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        grpFecha.setDisable(true);
        txtEdad.setEditable(false);
        txtDireccion.setEditable(false);
        txtOcupacion.setEditable(false);
        txtSexo.setEditable(false);
    }
    
    public void activarControles(){
        txtDPI.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        grpFecha.setDisable(false);
        txtEdad.setEditable(false);
        txtDireccion.setEditable(true);
        txtOcupacion.setEditable(true);
        txtSexo.setEditable(true);
    }
    
    public void limpiarControles(){
        txtDPI.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        fecha.setSelectedDate(null);
        txtEdad.setText("");
        txtDireccion.setText("");
        txtOcupacion.setText("");
        txtSexo.setText("");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/adolfolopez/resource/DatePicker.css");
        grpFecha.add(fecha, 0,0);
    }
    
    public void cargarDatos(){
        tblPacientes.setItems(getPacientes());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("codigoPaciente"));
        colDPI.setCellValueFactory(new PropertyValueFactory<Paciente, String>("dpi"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Paciente, String>("apellidos"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Paciente, String>("nombres"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaNacimiento"));
        colEdad.setCellValueFactory(new PropertyValueFactory<Paciente, String>("edad"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("direccion"));
        colOcupacion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("ocupacion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente, String>("sexo"));
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista= new ArrayList<Paciente>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes()}");
            ResultSet resultado= procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                                    resultado.getString("dpi"),
                                    resultado.getString("apellidos"),
                                    resultado.getString("nombres"),
                                    resultado.getDate("fechaNacimiento"),
                                    resultado.getInt("edad"),
                                    resultado.getString("direccion"),
                                    resultado.getString("ocupacion"),
                                    resultado.getString("sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPaciente= FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblPacientes.getSelectionModel().getSelectedItem() != null){
            txtDPI.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDpi()));
            txtApellidos.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getApellidos()));
            txtNombres.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getNombres());
            txtEdad.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getEdad()));
            txtDireccion.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDireccion());
            txtOcupacion.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getOcupacion()));
            txtSexo.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getSexo());
        }else {
            tblPacientes.getSelectionModel().clearSelection();
        }
    }
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado=null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro= procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Paciente(registro.getInt("codigoPaciente"),
                                        registro.getString("dpi"),
                                        registro.getString("apellidos"),
                                        registro.getString("nombres"),
                                        registro.getDate("fechaNacimiento"),
                                        registro.getInt("edad"),
                                        registro.getString("direccion"),
                                        registro.getString("ocupacion"),
                                        registro.getString("sexo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case NINGUNO:
                imprimirReporte();
                limpiarControles();
            break;
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setText("EDITAR");
                btnReporte.setText("REPORTE");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoOperacion= operaciones.NINGUNO;
            break;
        }   
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoPaciente", null);
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Pacientes", parametros);
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblPacientes.getSelectionModel().getSelectedItem()!= null){
                    btnEditar.setText("ACTUALIZAR");
                    btnReporte.setText("CANCELAR");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion= operaciones.ACTUALIZAR;
                }else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
            break;    
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("EDITAR");
                btnReporte.setText("REPORTE");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoOperacion= operaciones.NINGUNO;
                cargarDatos();
            break;
        }
    
    } 
     
    public void eliminar(){
        switch(tipoOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("NUEVO");
                btnEliminar.setText("ELIMINAR");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoOperacion= operaciones.NINGUNO;
            break;
            default:
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    int respuesta= JOptionPane.showConfirmDialog(null, "¿Ésta seguro de eliminar el registro?", "Eliminar Paciente", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta== JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarPaciente(?)}");
                            procedimiento.setInt(1, ((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                            procedimiento.execute();
                            listaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblPacientes.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    
                    }else{
                        tblPacientes.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento" );
                }
        }
    
    }
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                   limpiarControles();
                   activarControles();
                   btnNuevo.setText("GUARDAR");
                   btnEliminar.setText("CANCELAR");
                   btnEditar.setDisable(true);
                   btnReporte.setDisable(true);
                   tipoOperacion= operaciones.GUARDAR;
            break;
            case GUARDAR:
                   guardar();
                   desactivarControles();
                   limpiarControles();
                   btnNuevo.setText("NUEVO");
                   btnEliminar.setText("ELIMINAR");
                   btnEditar.setDisable(false);
                   btnReporte.setDisable(false);
                   tipoOperacion= operaciones.NINGUNO;
                   cargarDatos();
            break;

        }
    
    }
    
    public void guardar(){
        Paciente registro=new Paciente();
        registro.setDpi(txtDPI.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setNombres(txtNombres.getText());
        registro.setFechaNacimiento(fecha.getSelectedDate());
        registro.setDireccion(txtDireccion.getText());
        registro.setOcupacion(txtOcupacion.getText());
        registro.setSexo(txtSexo.getText());
        
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{Call sp_AgregarPaciente(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getDpi());
            procedimiento.setString(2, registro.getApellidos());
            procedimiento.setString(3, registro.getNombres());
            procedimiento.setDate(4, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(5, registro.getDireccion());
            procedimiento.setString(6, registro.getOcupacion());
            procedimiento.setString(7, registro.getSexo());
            procedimiento.execute();
            listaPaciente.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarPaciente(?,?,?,?,?,?,?,?)}");
            Paciente registro= (Paciente)tblPacientes.getSelectionModel().getSelectedItem();
            registro.setDpi(txtDPI.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNombres(txtNombres.getText());
            registro.setFechaNacimiento(fecha.getSelectedDate());
            registro.setDireccion(txtDireccion.getText());
            registro.setOcupacion(txtOcupacion.getText());
            registro.setSexo(txtSexo.getText());
            procedimiento.setInt(1, registro.getCodigoPaciente());
            procedimiento.setString(2, registro.getDpi());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNombres());
            procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(6, registro.getDireccion());
            procedimiento.setString(7, registro.getOcupacion());
            procedimiento.setString(8, registro.getSexo());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
   
    public void soloNumeros(KeyEvent Kevent){
        char key= Kevent.getCharacter().charAt(0);
        if(!Character.isDigit(key)){
            Kevent.consume();
        }
    }
    
    public PacientesController() {
    }

    public PacientesController(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
    }
}
