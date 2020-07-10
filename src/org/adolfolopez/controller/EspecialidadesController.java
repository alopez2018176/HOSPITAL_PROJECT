package org.adolfolopez.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.adolfolopez.bean.Especialidad;
import org.adolfolopez.db.Conexion;
import org.adolfolopez.sistema.Principal;

public class EspecialidadesController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion= operaciones.NINGUNO;
    private ObservableList<Especialidad> listaEspecialidad;
    
    @FXML private TextField txtNombre;
    @FXML private TableView tblEspecialidades;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombreEspecialidad;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void desactivarControles(){
        txtNombre.setEditable(false);
    }
    public void activarControles(){
        txtNombre.setEditable(true);
    }
    public void limpiarControles(){
        txtNombre.setText("");
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
    }
    
    public void cargarDatos(){
        tblEspecialidades.setItems(getEspecialidades());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Especialidad, Integer>("codigoEspecialidad"));
        colNombreEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, String>("nombreEspecialidad"));
    }
    
    public ObservableList<Especialidad> getEspecialidades(){
        ArrayList<Especialidad> lista= new ArrayList<Especialidad>();
        
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarEspecialidades()}");
            ResultSet resultado=procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Especialidad (resultado.getInt("codigoEspecialidad"),
                                            resultado.getString("nombreEspecialidad")
                                            ));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEspecialidad= FXCollections.observableList(lista);
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    public void seleccionarElemento(){
        if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
            txtNombre.setText(((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getNombreEspecialidad());
        }else {
            tblEspecialidades.getSelectionModel().clearSelection();
        }
    }
    
    public Especialidad buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado= null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarEspecialidad (?)}");
            procedimiento.setInt(1, codigoEspecialidad);
            ResultSet registro= procedimiento.executeQuery();
            while(registro.next()){
                resultado= new Especialidad(registro.getInt("codigoEspecialidad"),
                                      registro.getString("nombreEspecialidad"));
            }
        
        }catch(Exception e){
            e.printStackTrace();
        }
          
        return resultado;
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
                if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                    int respuesta= JOptionPane.showConfirmDialog(null, "¿Ésta seguro de eliminar el registro?", "Eliminar Especialidad", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta== JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarEspecialidad(?)}");
                            procedimiento.setInt(1, ((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
                            procedimiento.execute();
                            listaEspecialidad.remove(tblEspecialidades.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    
                    }else{
                        tblEspecialidades.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento" );
                }
        }
    
    }
    
    public void nuevo(ActionEvent e){
        switch(tipoOperacion){
            case NINGUNO:
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
        Especialidad registro=new Especialidad();
        registro.setNombreEspecialidad(txtNombre.getText());
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarEspecialidad(?)}");
            procedimiento.setString(1, registro.getNombreEspecialidad());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblEspecialidades.getSelectionModel().getSelectedItem()!= null){
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
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarEspecialidad(?,?)}");
            Especialidad registro= (Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem();
            registro.setNombreEspecialidad(txtNombre.getText());
            procedimiento.setInt(1, registro.getCodigoEspecialidad());
            procedimiento.setString(2, registro.getNombreEspecialidad());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
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
    
}
