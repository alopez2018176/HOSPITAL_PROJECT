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
import org.adolfolopez.bean.Medico;
import org.adolfolopez.bean.TelefonoMedico;
import org.adolfolopez.db.Conexion;
import org.adolfolopez.sistema.Principal;

public class TelefonoMedicoController implements Initializable {
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Medico> listaMedico;
    private ObservableList<TelefonoMedico> listaTMedicos;
    
    @FXML private TextField txtTPersonal;
    @FXML private TextField txtTTrabajo;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private TableView tblTelefonosMedicos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colTPersonal;
    @FXML private TableColumn colTTrabajo;
    @FXML private TableColumn colCodigoMedico;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void desactivarControles(){
        txtTPersonal.setEditable(false);
        txtTTrabajo.setEditable(false);
        cmbCodigoMedico.setDisable(true);
    }
    
    public void activarControles(){
        txtTPersonal.setEditable(true);
        txtTTrabajo.setEditable(true);
        cmbCodigoMedico.setDisable(false);
    }
    
    public void limpiarControles(){
        txtTPersonal.setText("");
        txtTTrabajo.setText("");
        cmbCodigoMedico.getSelectionModel().clearSelection();
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
                if(tblTelefonosMedicos.getSelectionModel().getSelectedItem() != null){
                    int respuesta= JOptionPane.showConfirmDialog(null, "¿Ésta seguro de eliminar el registro?", "Eliminar Télefono Médico", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta== JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTelefonoMedico(?)}");
                            procedimiento.setInt(1, ((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico());
                            procedimiento.execute();
                            listaMedico.remove(tblTelefonosMedicos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblTelefonosMedicos.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    
                    }else{
                        tblTelefonosMedicos.getSelectionModel().clearSelection();
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
                if(tblTelefonosMedicos.getSelectionModel().getSelectedItem()!= null){
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
    
    public TelefonoMedico buscarTelefonoMedico(int codigoTelefonoMedico){
        TelefonoMedico resultado= null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTelefonoMedico(?)}");
            procedimiento.setInt(1, codigoTelefonoMedico);
            ResultSet registro= procedimiento.executeQuery();
            while(registro.next()){
                resultado= new TelefonoMedico(registro.getInt("codigoTelefonoMedico"),
                                      registro.getString("telefonoPersonal"),
                                      registro.getString("telefonoTrabajo"),
                                      registro.getInt("codigoMedico"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarTelefonoMedico(?,?,?,?)}");
            TelefonoMedico registro= (TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem();
            registro.setTelefonoPersonal(txtTPersonal.getText());
            registro.setTelefonoTrabajo(txtTTrabajo.getText());
            registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
            procedimiento.setInt(1, registro.getCodigoTelefonoMedico());
            procedimiento.setString(2, registro.getTelefonoPersonal());
            procedimiento.setString(3, registro.getTelefonoTrabajo());
            procedimiento.setInt(4, registro.getCodigoMedico());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void agregar(){
        TelefonoMedico registro= new TelefonoMedico();
        registro.setTelefonoPersonal(txtTPersonal.getText());
        registro.setTelefonoTrabajo(txtTTrabajo.getText());
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTelefonoMedico(?,?,?)}");
            procedimiento.setString(1, registro.getTelefonoPersonal());
            procedimiento.setString(2, registro.getTelefonoTrabajo());
            procedimiento.setInt(3, registro.getCodigoMedico());
            procedimiento.execute();
            listaTMedicos.add(registro);
        }catch(Exception e){
            e.printStackTrace();
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
        cmbCodigoMedico.setItems(getMedicos());
    }
    
    public Medico buscarMedico(int codigoMedco){
        Medico resultado = null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{sp_BuscarMedico(?)}");
            procedimiento.setInt(1, codigoMedco);
            ResultSet registro= procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Medico(registro.getInt("codigoMedico"),
                                        registro.getInt("licenciaMedica"),
                                        registro.getString("nombres"),
                                        registro.getString("apellidos"),
                                        registro.getString("horaEntrada"),
                                        registro.getString("horaSalida"),
                                        registro.getInt("turnoMaximo"),
                                        registro.getString("sexo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista= new ArrayList<Medico>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos()}");
            ResultSet resultado= procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medico(resultado.getInt("codigoMedico"),
                                    resultado.getInt("licenciaMedica"),
                                    resultado.getString("nombres"),
                                    resultado.getString("apellidos"),
                                    resultado.getString("horaEntrada"),
                                    resultado.getString("horaSalida"),
                                    resultado.getInt("turnoMaximo"),
                                    resultado.getString("sexo")));
            
            }
                
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaMedico= FXCollections.observableList(lista);
    }
    
    public void cargarDatos(){
        tblTelefonosMedicos.setItems(getTelefonosMedicos());
        colCodigo.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoTelefonoMedico"));
        colTPersonal.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoPersonal"));
        colTTrabajo.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoTrabajo"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoMedico"));
    }
    
    public void seleccionarElemento(){
        if(tblTelefonosMedicos.getSelectionModel().getSelectedItem() != null){
            txtTPersonal.setText(((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            txtTTrabajo.setText(((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getTelefonoTrabajo());
            cmbCodigoMedico.getSelectionModel().select(buscarMedico(((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        }else {
            tblTelefonosMedicos.getSelectionModel().clearSelection();
        }
    }
    
    public ObservableList<TelefonoMedico> getTelefonosMedicos(){
        ArrayList<TelefonoMedico> lista=new ArrayList<TelefonoMedico>();
        
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTelefonosMedicos}");
            ResultSet resultado= procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new TelefonoMedico(resultado.getInt("codigoTelefonoMedico"),
                                            resultado.getString("telefonoPersonal"),
                                            resultado.getString("telefonoTrabajo"),
                                            resultado.getInt("codigoMedico")));    
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTMedicos= FXCollections.observableList(lista);
    }
    
    public void soloNumeros(KeyEvent Kevent){
        char key= Kevent.getCharacter().charAt(0);
        if(!Character.isDigit(key)){
            Kevent.consume();
        }
    }
        
    public void ventanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }
}
