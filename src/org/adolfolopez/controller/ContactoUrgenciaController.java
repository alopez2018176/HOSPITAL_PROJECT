package org.adolfolopez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.adolfolopez.bean.ContactoUrgencia;
import org.adolfolopez.bean.Paciente;
import org.adolfolopez.db.Conexion;
import org.adolfolopez.sistema.Principal;

public class ContactoUrgenciaController implements Initializable {
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<ContactoUrgencia> listaContactoUrgencia;
    private ObservableList<Paciente> listaPaciente;
    
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumeroContacto;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblContactosUrgencias;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNumeroContacto;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void desactivarControles (){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtNumeroContacto.setEditable(false);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumeroContacto.setEditable(true);
        cmbCodigoPaciente.setDisable(false);
    }
    
    public void limpiarControles(){
        txtNombres.setText("");
        txtApellidos.setText("");
        txtNumeroContacto.setText("");
        cmbCodigoPaciente.getSelectionModel().clearSelection();
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
                   agregar();
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
                if(tblContactosUrgencias.getSelectionModel().getSelectedItem() != null){
                    int respuesta= JOptionPane.showConfirmDialog(null, "¿Ésta seguro de eliminar el registro?", "Eliminar Contacto Urgencia", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta== JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarContactoUrgencia(?)}");
                            procedimiento.setInt(1, ((ContactoUrgencia)tblContactosUrgencias.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia());
                            procedimiento.execute();
                            listaContactoUrgencia.remove(tblContactosUrgencias.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblContactosUrgencias.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    
                    }else{
                        tblContactosUrgencias.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento" );
                }
        }
    
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblContactosUrgencias.getSelectionModel().getSelectedItem()!= null){
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
    
    public void reporte(){
        switch(tipoOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setText("EDITAR");
                btnReporte.setText("REPORTE");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoOperacion= operaciones.NUEVO;
            break;
        }   
    }
    
    public ContactoUrgencia buscarContactoUrgencia (int codigoContactoUrgencia){
        ContactoUrgencia resultado= null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarContactoUrgencia(?)}");
            procedimiento.setInt(1, codigoContactoUrgencia);
            ResultSet registro= procedimiento.executeQuery();
            while(registro.next()){
                resultado= new ContactoUrgencia(registro.getInt("codigoContactoUrgencia"),
                                      registro.getString("apellidos"),
                                      registro.getString("nombres"),
                                      registro.getString("numeroContacto"),
                                      registro.getInt("codigoMedico"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }    
    
    public void agregar(){
        ContactoUrgencia registro= new ContactoUrgencia();
        registro.setApellidos(txtApellidos.getText());
        registro.setNombres(txtNombres.getText());
        registro.setNumeroContacto(txtNumeroContacto.getText());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarContactoUrgencia(?,?,?,?)}");
            procedimiento.setString(1, registro.getApellidos());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getNumeroContacto());
            procedimiento.setInt(4, registro.getCodigoPaciente());
            procedimiento.execute();
            listaContactoUrgencia.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarContactoUrgencia(?,?,?,?,?)}");
            ContactoUrgencia registro= (ContactoUrgencia)tblContactosUrgencias.getSelectionModel().getSelectedItem();
            registro.setApellidos(txtApellidos.getText());
            registro.setNombres(txtNombres.getText());
	    registro.setNumeroContacto(txtNumeroContacto.getText());
            registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            procedimiento.setInt(1, registro.getCodigoContactoUrgencia());
            procedimiento.setString(2, registro.getApellidos());
            procedimiento.setString(3, registro.getNombres());
	    procedimiento.setString(4, registro.getNumeroContacto());
            procedimiento.setInt(5, registro.getCodigoPaciente());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
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
    
    public void cargarDatos(){
        tblContactosUrgencias.setItems(getContactosUrgencias());
        colCodigo.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoContactoUrgencia"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("apellidos"));
        colNombres.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("nombres"));
        colNumeroContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("numeroContacto"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoPaciente"));
    }
    
    public void seleccionarElemento(){
        if(tblContactosUrgencias.getSelectionModel().getSelectedItem() != null){
            txtApellidos.setText(((ContactoUrgencia)tblContactosUrgencias.getSelectionModel().getSelectedItem()).getApellidos());
            txtNombres.setText(((ContactoUrgencia)tblContactosUrgencias.getSelectionModel().getSelectedItem()).getNombres());
            txtNumeroContacto.setText(((ContactoUrgencia)tblContactosUrgencias.getSelectionModel().getSelectedItem()).getNumeroContacto());
            cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((ContactoUrgencia)tblContactosUrgencias.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        }else {
            tblContactosUrgencias.getSelectionModel().clearSelection();
        }
    }
    
    public ObservableList<ContactoUrgencia> getContactosUrgencias(){
        ArrayList <ContactoUrgencia> lista=new ArrayList<ContactoUrgencia>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarContactosUrgencia()}");
            ResultSet resultado= procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ContactoUrgencia(resultado.getInt("codigoContactoUrgencia"),
                                                resultado.getString("apellidos"),
                                                resultado.getString("nombres"),
                                                resultado.getString("numeroContacto"),
                                                resultado.getInt("codigoPaciente")));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaContactoUrgencia= FXCollections.observableList(lista);
    }
    
    public void soloNumeros(KeyEvent Kevent){
        char key= Kevent.getCharacter().charAt(0);
        if(!Character.isDigit(key)){
            Kevent.consume();
        }
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoPaciente.setItems(getPacientes());
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
}
