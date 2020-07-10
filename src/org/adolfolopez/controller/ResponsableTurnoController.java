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
import org.adolfolopez.bean.Area;
import org.adolfolopez.bean.Cargo;
import org.adolfolopez.bean.ResponsableTurno;
import org.adolfolopez.db.Conexion;
import org.adolfolopez.sistema.Principal;

public class ResponsableTurnoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<ResponsableTurno> listaResponsableTurno;
    private ObservableList<Area> listaArea;
    private ObservableList<Cargo> listaCargo;
    
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTPersonal;
    @FXML private ComboBox cmbCodigoArea;
    @FXML private ComboBox cmbCodigoCargo;
    @FXML private TableView tblResponsableTurno;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colCodigoArea;
    @FXML private TableColumn colCodigoCargo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void desactivarControles (){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtTPersonal.setEditable(false);
        cmbCodigoArea.setDisable(true);
        cmbCodigoCargo.setDisable(true);

    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtTPersonal.setEditable(true);
        cmbCodigoArea.setDisable(false);
        cmbCodigoCargo.setDisable(false);
    }
    
    public void limpiarControles(){
        txtNombres.setText("");
        txtApellidos.setText("");
        txtTPersonal.setText("");
        cmbCodigoArea.getSelectionModel().clearSelection();
        cmbCodigoCargo.getSelectionModel().clearSelection();
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
                if(tblResponsableTurno.getSelectionModel().getSelectedItem() != null){
                    int respuesta= JOptionPane.showConfirmDialog(null, "¿Ésta seguro de eliminar el registro?", "Eliminar Responsable de Turno", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta== JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarResponsableTurno(?)}");
                            procedimiento.setInt(1, ((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
                            procedimiento.execute();
                            listaResponsableTurno.remove(tblResponsableTurno.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblResponsableTurno.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    
                    }else{
                        tblResponsableTurno.getSelectionModel().clearSelection();
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
                if(tblResponsableTurno.getSelectionModel().getSelectedItem()!= null){
                    btnEditar.setText("ACTUALIZAR");
                    btnReporte.setText("CANCELAR");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    cmbCodigoArea.setDisable(true);
                    cmbCodigoCargo.setDisable(true);
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
    
    public ResponsableTurno buscarResponsableTurno (int codigoResponsableTurno){
        ResponsableTurno resultado= null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarResponsableTurno(?)}");
            procedimiento.setInt(1, codigoResponsableTurno);
            ResultSet registro= procedimiento.executeQuery();
            while(registro.next()){
                resultado= new ResponsableTurno(registro.getInt("codigoResponsableTurno"),
                                      registro.getString("nombreResponsable"),
                                      registro.getString("apellidosResponsable"),
                                      registro.getString("telefonoPersonal"),
                                      registro.getInt("codigoArea"),
		  registro.getInt("codigoCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void agregar(){
        ResponsableTurno registro= new ResponsableTurno();
        registro.setNombreResponsable(txtNombres.getText());
        registro.setApellidosResponsable(txtApellidos.getText());
        registro.setTelefonoPersonal(txtTPersonal.getText());
        registro.setCodigoArea(((Area)cmbCodigoArea.getSelectionModel().getSelectedItem()).getCodigoArea());
        registro.setCodigoCargo(((Cargo)cmbCodigoCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());

        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarResponsableTurno(?,?,?,?,?)}");
            procedimiento.setString(1, registro.getNombreResponsable());
            procedimiento.setString(2, registro.getApellidosResponsable());
            procedimiento.setString(3, registro.getTelefonoPersonal());
            procedimiento.setInt(4, registro.getCodigoArea());
            procedimiento.setInt(5, registro.getCodigoCargo());
            procedimiento.execute();
            listaResponsableTurno.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
     public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarResponsableTurno(?,?,?,?,?,?)}");
            ResponsableTurno registro= (ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem();
            registro.setNombreResponsable(txtNombres.getText());
            registro.setApellidosResponsable(txtApellidos.getText());
            registro.setTelefonoPersonal(txtTPersonal.getText());
            registro.setCodigoArea(((Area)cmbCodigoArea.getSelectionModel().getSelectedItem()).getCodigoArea());
            registro.setCodigoCargo(((Cargo)cmbCodigoCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());

            procedimiento.setInt(1, registro.getCodigoResponsableTurno());
            procedimiento.setString(2, registro.getNombreResponsable());
            procedimiento.setString(3, registro.getApellidosResponsable());
            procedimiento.setString(4, registro.getTelefonoPersonal());
            procedimiento.setInt(5, registro.getCodigoArea());
            procedimiento.setInt(6, registro.getCodigoCargo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ResponsableTurnoController() {
    }

    public ResponsableTurnoController(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
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
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cargarDatos();
       cmbCodigoArea.setItems(getAreas());
       cmbCodigoCargo.setItems(getCargos());
    }
    
    public void cargarDatos(){
        tblResponsableTurno.setItems(getResponsablesTurnos());
        colCodigo.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoResponsableTurno"));
        colNombres.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("nombreResponsable"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("apellidosResponsable"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("telefonoPersonal"));
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoArea"));
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoCargo"));
    }
    
    public void seleccionarElemento(){
        if(tblResponsableTurno.getSelectionModel().getSelectedItem() != null){
            txtNombres.setText(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getNombreResponsable());
            txtNombres.setText(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getApellidosResponsable());
            txtTPersonal.setText(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            cmbCodigoArea.getSelectionModel().select(buscarArea(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoArea()));
            cmbCodigoCargo.getSelectionModel().select(buscarCargo(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoCargo()));
        }else {
            tblResponsableTurno.getSelectionModel().clearSelection();
        }
    }
    
    public ObservableList<ResponsableTurno> getResponsablesTurnos(){
        ArrayList <ResponsableTurno> lista=new ArrayList<ResponsableTurno>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarResponsablesTurno()}");
            ResultSet resultado= procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ResponsableTurno(resultado.getInt("codigoResponsableTurno"),
                                                resultado.getString("nombreResponsable"),
                                                resultado.getString("apellidosResponsable"),
                                                resultado.getString("telefonoPersonal"),
                                                resultado.getInt("codigoArea"),
		            resultado.getInt("codigoCargo")));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaResponsableTurno= FXCollections.observableList(lista);
    }
    
    public void soloNumeros(KeyEvent Kevent){
        char key= Kevent.getCharacter().charAt(0);
        if(!Character.isDigit(key)){
            Kevent.consume();
        }
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
}
