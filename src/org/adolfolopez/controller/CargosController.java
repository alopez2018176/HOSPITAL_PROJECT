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
import org.adolfolopez.bean.Cargo;
import org.adolfolopez.db.Conexion;
import org.adolfolopez.sistema.Principal;

public class CargosController implements Initializable {
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Cargo> listaCargo;
    
    @FXML private TextField txtNombre;
    @FXML private TableView tblCargos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombreCargo;
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
        tblCargos.setItems(getCargos());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Cargo, Integer>("codigoCargo"));
        colNombreCargo.setCellValueFactory(new PropertyValueFactory<Cargo, String>("nombreCargo"));
    }
    
     public ObservableList<Cargo> getCargos(){
        ArrayList<Cargo> lista= new ArrayList<Cargo>();
        
        try{
           PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarCargos()}");
           ResultSet resultado= procedimiento.executeQuery();
           while(resultado.next()){
               lista.add(new Cargo (resultado.getInt("codigoCargo"),
                                    resultado.getString("nombreCargo")
                                    ));
           }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaCargo= FXCollections.observableList(lista);
    }

    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    public void seleccionarElemento(){
        if(tblCargos.getSelectionModel().getSelectedItem() != null){
            txtNombre.setText(((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getNombreCargo());
        }else {
            tblCargos.getSelectionModel().clearSelection();
        }
    }
    
    public Cargo buscarCargo(int codigoCargo){
        Cargo resultado= null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarCargo (?)}");
            procedimiento.setInt(1, codigoCargo);
            ResultSet registro= procedimiento.executeQuery();
            while(registro.next()){
                resultado= new Cargo(registro.getInt("codigoCargo"),
                                      registro.getString("nombreCargo"));
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
                if(tblCargos.getSelectionModel().getSelectedItem() != null){
                    int respuesta= JOptionPane.showConfirmDialog(null, "¿Ésta seguro de eliminar el registro?", "Eliminar Cargo", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta== JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarCargo(?)}");
                            procedimiento.setInt(1, ((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getCodigoCargo());
                            procedimiento.execute();
                            listaCargo.remove(tblCargos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    
                    }else{
                        tblCargos.getSelectionModel().clearSelection();
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
        Cargo registro=new Cargo();
        registro.setNombreCargo(txtNombre.getText());
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarCargo(?)}");
            procedimiento.setString(1, registro.getNombreCargo());
            procedimiento.execute();
        
        }catch(Exception e){
            e.printStackTrace();
        }
    
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tblCargos.getSelectionModel().getSelectedItem()!= null){
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarCargo(?,?)}");
            Cargo registro= (Cargo)tblCargos.getSelectionModel().getSelectedItem();
            registro.setNombreCargo(txtNombre.getText());
            procedimiento.setInt(1, registro.getCodigoCargo());
            procedimiento.setString(2, registro.getNombreCargo());
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
