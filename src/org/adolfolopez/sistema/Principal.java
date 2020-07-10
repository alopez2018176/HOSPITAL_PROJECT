package org.adolfolopez.sistema;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.adolfolopez.controller.AcercaDeController;
import org.adolfolopez.controller.AreasController;
import org.adolfolopez.controller.CargosController;
import org.adolfolopez.controller.ContactoUrgenciaController;
import org.adolfolopez.controller.EspecialidadesController;
import org.adolfolopez.controller.HorariosController;
import org.adolfolopez.controller.MedicosController;
import org.adolfolopez.controller.MenuPrincipalController;
import org.adolfolopez.controller.PacientesController;
import org.adolfolopez.controller.ResponsableTurnoController;
import org.adolfolopez.controller.TelefonoMedicoController;

public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/adolfolopez/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    
    @Override
    public void start(Stage escenarioPrincipal) {
        this.escenarioPrincipal=escenarioPrincipal;
        escenarioPrincipal.getIcons().add(new Image("/org/adolfolopez/images/iconoBarra.png"));
        escenarioPrincipal.setTitle("Hospital de Infectología");
        menuPrincipal();
        escenarioPrincipal.show();
       
    }
    
    public void menuPrincipal(){
      try{
          MenuPrincipalController menuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml",600,400);
          menuPrincipal.setEscenarioPrincipal(this);
      }catch(Exception e){
          e.printStackTrace();
      }
    }
    
    public void acercaDe(){
      try{
          AcercaDeController infoProgramador = (AcercaDeController)cambiarEscena("AcercaDeView.fxml",360,485);
          infoProgramador.setInfoProgramador(this);
      }catch(Exception e){
          e.printStackTrace();
      }
    }
    
    public void ventanaMedicos(){
        try{
           MedicosController ventanaM = (MedicosController) cambiarEscena("MedicosView.fxml",900,530);
           ventanaM.setVentanaM(this);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void ventanaEspecialidades(){
        try{
          EspecialidadesController especialidad= (EspecialidadesController) cambiarEscena("EspecialidadesView.fxml",500,300);
          especialidad.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaAreas(){
        try{
            AreasController areas= (AreasController) cambiarEscena("AreasView.fxml",500,300);
            areas.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaCargos(){
        try{
            CargosController cargos= (CargosController) cambiarEscena("CargosView.fxml",500,300);
            cargos.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    
    }
    
    public void ventanaContactoUrgencia(){
        try{
            ContactoUrgenciaController contactoUr= (ContactoUrgenciaController) cambiarEscena("ContactoUrgenciasView.fxml",600,500);
           contactoUr.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void ventanaTMedicos(){
        try{
            TelefonoMedicoController tMedicos= (TelefonoMedicoController) cambiarEscena("TelefonosMedicosView.fxml",600,400);
            tMedicos.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void ventanaPacientes(){
        try{
            PacientesController paciente= (PacientesController) cambiarEscena("PacientesView.fxml",830,600);
            paciente.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaResponsableTurno(){
        try{
            ResponsableTurnoController responsable= (ResponsableTurnoController) cambiarEscena("ResponsableDeTurnoView.fxml",600,450);
            responsable.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaHorarios(){
        try{
            HorariosController horario= (HorariosController) cambiarEscena("HorariosView.fxml",600,450);
            horario.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();   
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena=new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado= (Initializable)cargadorFXML.getController();
        return resultado;
    } 
    
    public static void main (String [] args){
        launch(args);
    }
    
}

