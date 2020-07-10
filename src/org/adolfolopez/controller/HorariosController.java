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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.adolfolopez.bean.Horario;
import org.adolfolopez.db.Conexion;
import org.adolfolopez.sistema.Principal;

public class HorariosController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Horario> listaHorario;
    
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraSalida;
    @FXML private CheckBox chkLunes;
    @FXML private CheckBox chkMartes;
    @FXML private CheckBox chkMiercoles;
    @FXML private CheckBox chkJueves;
    @FXML private CheckBox chkViernes;
    @FXML private TableView tblHorarios;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colHoraInicio;
    @FXML private TableColumn colHoraSalida;
    @FXML private TableColumn colLunes;
    @FXML private TableColumn colMartes;
    @FXML private TableColumn colMiercoles;
    @FXML private TableColumn colJueves;
    @FXML private TableColumn colViernes;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;

    public HorariosController() {
    }

    public HorariosController(Principal escenarioPrincipal) {
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
    
    public void desactivarControles(){
        txtHoraInicio.setEditable(false);
        txtHoraSalida.setEditable(false);
        chkLunes.setDisable(true);
        chkMartes.setDisable(true);
        chkMiercoles.setDisable(true);
        chkJueves.setDisable(true);
        chkViernes.setDisable(true);
    }
    
    public void activarControles(){
        txtHoraInicio.setEditable(true);
        txtHoraSalida.setEditable(true);
        chkLunes.setDisable(false);
        chkMartes.setDisable(false);
        chkMiercoles.setDisable(false);
        chkJueves.setDisable(false);
        chkViernes.setDisable(false);
    }
    
    public void limpiarControles(){
        txtHoraInicio.setText("");
        txtHoraSalida.setText("");
        chkLunes.setSelected(false);
        chkMartes.setSelected(false);
        chkMiercoles.setSelected(false);
        chkJueves.setSelected(false);
        chkViernes.setSelected(false);
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
    
    public void agregar(){
        Horario registro= new Horario();
        registro.setHoraInicio(txtHoraInicio.getText());
        registro.setHoraSalida(txtHoraSalida.getText());
        registro.setLunes(chkLunes.isSelected());
        registro.setMartes(chkMartes.isSelected());
        registro.setMiercoles(chkMiercoles.isSelected());
        registro.setJueves(chkJueves.isSelected());
        registro.setViernes(chkViernes.isSelected());
                
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarHorario(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getHoraInicio());
            procedimiento.setString(2, registro.getHoraSalida());
            procedimiento.setBoolean(3, registro.isLunes());
            procedimiento.setBoolean(4, registro.isMartes());
            procedimiento.setBoolean(5, registro.isMiercoles());
            procedimiento.setBoolean(6, registro.isJueves());
            procedimiento.setBoolean(7, registro.isViernes());
            procedimiento.execute();
            listaHorario.add(registro);
        }catch(Exception e){
            e.printStackTrace();
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
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    int respuesta= JOptionPane.showConfirmDialog(null, "¿Ésta seguro de eliminar el registro?", "Eliminar Horario", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta== JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarHorario(?)}");
                            procedimiento.setInt(1, ((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getCodigoHorario());
                            procedimiento.execute();
                                listaHorario.remove(tblHorarios.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblHorarios.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    
                    }else{
                        tblHorarios.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento" );
                }
        }
    
    }
    
    public ObservableList<Horario> getHorarios(){
        ArrayList<Horario> lista= new ArrayList<Horario>();
        
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarHorarios()}");
            ResultSet resultado= procedimiento.executeQuery();
            while(resultado.next()){
            lista.add(new Horario (resultado.getInt("codigoHorario"),
                                resultado.getString("horaEntrada"),
                                resultado.getString("horaSalida"),
                                resultado.getBoolean("lunes"),
                                resultado.getBoolean("martes"),
                                resultado.getBoolean("miercoles"),
                                resultado.getBoolean("jueves"),
                                resultado.getBoolean("viernes")
                                ));
            
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaHorario= FXCollections.observableList(lista);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblHorarios.setItems(getHorarios());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Horario, Integer>("codigoHorario"));
        colHoraInicio.setCellValueFactory(new PropertyValueFactory<Horario, String>("horaInicio"));
        colHoraSalida.setCellValueFactory(new PropertyValueFactory<Horario, String>("horaSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("viernes"));
    }
    public void seleccionarElementos(){
        if(tblHorarios.getSelectionModel().getSelectedItem()!=null){
            txtHoraInicio.setText(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHoraInicio());
            txtHoraSalida.setText(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHoraSalida());
            chkLunes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isLunes());
            chkMartes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMartes());
            chkMiercoles.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMiercoles());
            chkJueves.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isJueves());
            chkViernes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isViernes());
        }else {
            tblHorarios.getSelectionModel().clearSelection();
        }
    }
}
