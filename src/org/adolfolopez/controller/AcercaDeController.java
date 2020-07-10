package org.adolfolopez.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.adolfolopez.sistema.Principal;

public class AcercaDeController implements Initializable {
    private Principal infoProgramador;
    
    @Override
    
    public void initialize (URL location, ResourceBundle resources){
    
    }
    
    public Principal getInfoProgramador(){
        return infoProgramador;
    }
    
    public void setInfoProgramador(Principal infoProgramador){
        this.infoProgramador = infoProgramador;
    }
    public void llamarEscenarioPrincipal(){
        infoProgramador.menuPrincipal();
    }
    
      
}
