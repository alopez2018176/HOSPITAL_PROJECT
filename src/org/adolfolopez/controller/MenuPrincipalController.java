package org.adolfolopez.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.adolfolopez.sistema.Principal;

public class MenuPrincipalController implements Initializable {
    private Principal escenarioPrincipal;
    
    @Override
    
    public void initialize (URL location, ResourceBundle resources){
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    public void llamarAcercaDe(){
        escenarioPrincipal.acercaDe();
    }
    public void ventanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }
    public void ventanaEspecialidades(){
        escenarioPrincipal.ventanaEspecialidades();
    }
    public void ventanaAreas(){
        escenarioPrincipal.ventanaAreas();
    }
    public void ventanaCargos(){
        escenarioPrincipal.ventanaCargos();
    }
    public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
    }
    public void ventanaTMedicos(){
        escenarioPrincipal.ventanaTMedicos();
    }
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
    public void ventanaHorarios(){
        escenarioPrincipal.ventanaHorarios();
    }
    public void ventanaResponsableTurno(){
        escenarioPrincipal.ventanaResponsableTurno();
    }
}
