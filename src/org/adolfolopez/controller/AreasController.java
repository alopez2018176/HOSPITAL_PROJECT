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
import org.adolfolopez.bean.Area;
import org.adolfolopez.db.Conexion;
import org.adolfolopez.sistema.Principal;

public class AreasController implements Initializable {
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion= operaciones.NINGUNO;
    private ObservableList<Area> listaArea;
    
    @FXML private TextField txtNombre;
    @FXML private TableView tblAreas;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombreArea;
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
        tblAreas.setItems(getAreas());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Area, Integer>("codigoArea"));
        colNombreArea.setCellValueFactory(new PropertyValueFactory<Area, String>("nombreArea"));
    }
    
    public ObservableList<Area> getAreas(){
        ArrayList<Area> lista= new ArrayList<Area>();
        
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarAreas()}");
            ResultSet resultado= procedimiento.executeQuery();
            while(resultado.next()){
            lista.add(new Area (resultado.getInt("codigoArea"),
                                resultado.getString("nombreArea")
                                ));
            
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaArea= FXCollections.observableList(lista);
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void seleccionarElemento(){
        if(tblAreas.getSelectionModel().getSelectedItem() != null){
            txtNombre.setText(((Area)tblAreas.getSelectionModel().getSelectedItem()).getNombreArea());
        }else {
            tblAreas.getSelectionModel().clearSelection();
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
        Area registro=new Area();
        registro.setNombreArea(txtNombre.getText());
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarArea(?)}");
            procedimiento.setString(1, registro.getNombreArea());
            procedimiento.execute();
        
        }catch(Exception e){
            e.printStackTrace();
        }
    
    }
    
    public Area buscarArea(int codigoArea){
        Area resultado= null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarArea (?)}");
            procedimiento.setInt(1, codigoArea);
            ResultSet registro= procedimiento.executeQuery();
            while(registro.next()){
                resultado= new Area(registro.getInt("codigoArea"),
                                      registro.getString("nombreArea"));
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
                if(tblAreas.getSelectionModel().getSelectedItem() != null){
                    int respuesta= JOptionPane.showConfirmDialog(null, "¿Ésta seguro de eliminar el registro?", "Eliminar Area", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta== JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarArea(?)}");
                            procedimiento.setInt(1, ((Area)tblAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
                            procedimiento.execute();
                            listaArea.remove(tblAreas.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    
                    }else{
                        tblAreas.getSelectionModel().clearSelection();
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
                if(tblAreas.getSelectionModel().getSelectedItem()!= null){
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarArea(?,?)}");
            Area registro= (Area)tblAreas.getSelectionModel().getSelectedItem();
            registro.setNombreArea(txtNombre.getText());
            procedimiento.setInt(1, registro.getCodigoArea());
            procedimiento.setString(2, registro.getNombreArea());
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
