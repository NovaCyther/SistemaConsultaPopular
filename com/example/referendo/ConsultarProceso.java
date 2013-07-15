package com.example.proyvotaciones;


import com.example.proyvotaciones.NavigatorUI.MainView;
import com.vaadin.client.metadata.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.FileResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class ConsultarProceso extends Panel{
	
	ComboBox nombre;
	Label descripcion;
	Label comunidad;
	Label organizador;
	Label tipo;
	Label estilo;
	Label inicioInscripcion;
	Label finInscripcion;
	Label inicioDiscusion;
	Label finDiscusion;
	Label inicioVotacion;
	Label finVotacion;
	Label error;
	Button consultarPadron;
	DBManager dBManager;
	EditarPlebiscito container;
	SQLContainer containerNombre;
	private File tempFile;
	FileReader reader;
	EditarPadron editarPadronWindow;
	String pk;
	Image image;
	File imgFile;
	String iniIns, finIns, iniDis, finDis, iniVot, finVot;
	TendenciasTable tablaTendencias;
	final FormLayout layout;
	MainView mainwindow;  // Reference to main window
    Window mywindow;    // The window to be opened
    CheckBox tipoCheck;
    CheckBox estiloCheck;
    final HorizontalLayout periodoInscripcion;
    final HorizontalLayout periodoDiscusion;
    final HorizontalLayout periodoVotacion;
    

	public ConsultarProceso(final MainView mainView) {
		
		dBManager = new DBManager();
		pk = "";
		
		layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
        layout.addStyleName("outlined");
        layout.setSizeFull();
        layout.setSpacing(true);
        
        /*Inicializacion de nombre*/
        nombre = new ComboBox("Nombre del Proceso");
        nombre.setNullSelectionAllowed(false);
        nombre.setRequired(true);
        nombre.addBlurListener(new BlurListener() {	
		@Override
		public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				removerDatos();
				if (nombre.getValue() != null){
					obtenerDatos();
					mostrarDatos();
					mostrarTendencias();
				}
			}
        });
        
        error = new Label("No exiten plebiscitos inscritos en este momento");
        
        try {
        	JDBCConnectionPool sqlPool = new SimpleJDBCConnectionPool("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "postgres", "1234",2,5);
        	TableQuery q = new TableQuery("plebiscito", sqlPool);
			containerNombre = new SQLContainer(q);
			nombre.setContainerDataSource(containerNombre);
			nombre.setItemCaptionPropertyId("nombreplebiscito");
			nombre.setImmediate(true);
			if (containerNombre.size()==0){
				mostrarError();
			}else{
				nombre.setVisible(true);
				error.setVisible(false);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*Iniciializa la imagen subida*/
        final Image image = new Image("Imagen Subida");
	    image.setVisible(false);
      
	    
        /*Inicializacion de tipo*/
        tipo = new Label("Tipo");
        
        /*Inicializacion de estilo*/
        estilo = new Label("Estilo");

        
        
       
	    /*Inicializacion de nombre, descripcion, comunidad y organizador*/
	    inicializarLabels();
	    
	    inicioInscripcion = new Label("Inicio");
		finInscripcion = new Label("Fin");
		inicioDiscusion = new Label("Inicio");
		finDiscusion = new Label("Fin");
		inicioVotacion = new Label("Inicio");
		finVotacion = new Label("Fin");
       
		layout.addComponent(error);
		layout.addComponent(nombre);
        layout.addComponent(descripcion);
        layout.addComponent(comunidad);
        layout.addComponent(organizador);
        layout.addComponent(tipo);
        layout.addComponent(estilo);
        layout.addComponent(image);
        
 
        periodoInscripcion = new HorizontalLayout(
                inicioInscripcion, finInscripcion);
        periodoInscripcion.setCaption("Perido de inscripción de tendencias");
        periodoInscripcion.setSpacing(true);
        layout.addComponent(periodoInscripcion);
        
        
        periodoDiscusion = new HorizontalLayout(
                inicioDiscusion, finDiscusion);
        periodoDiscusion.setCaption("Perido de discusión");
        periodoDiscusion.setSpacing(true);
        layout.addComponent(periodoDiscusion);
        
        periodoVotacion = new HorizontalLayout(
        		inicioVotacion, finVotacion);
        periodoVotacion.setCaption("Perido de votación");
        periodoVotacion.setSpacing(true);
        layout.addComponent(periodoVotacion);
        removerDatos();
        
	}
	
	public void obtenerDatos(){
		 try {
			 	pk = nombre.getValue().toString();
			 	if (dBManager.SelectValuePlebiscito("estilo", pk).equals("A")){
			 		estilo.setValue("Estilo: Abierto");
			 	}else{
			 		estilo.setValue("Estilo: Cerrado");
			 	}
			 	if (dBManager.SelectValuePlebiscito("tipo", pk).equals("R")){
			 		tipo.setValue("Tipo: Referendo");
			 	}else{
			 		tipo.setValue("Tipo: Plebiscito");
			 	}
				
				descripcion.setValue("Descripcion: "+dBManager.SelectValuePlebiscito("descripcion", pk));
				organizador.setValue("Organizador: "+dBManager.SelectValuePlebiscito("organizador", pk));
				comunidad.setValue("Comunidad: "+dBManager.SelectValuePlebiscito("comunidad", pk));
				inicioInscripcion.setValue(dBManager.SelectValuePlebiscito("inicioinscripciontendencias", pk));
				inicioVotacion.setValue(dBManager.SelectValuePlebiscito("inicioperiodovotacion", pk));
				inicioDiscusion.setValue(dBManager.SelectValuePlebiscito("inicioperiododiscucion", pk));
				finInscripcion.setValue(dBManager.SelectValuePlebiscito("fininscripciontendencias", pk));
				finVotacion.setValue(dBManager.SelectValuePlebiscito("finperiodovotacion", pk));
				finDiscusion.setValue(dBManager.SelectValuePlebiscito("finperiododiscusion", pk));
		 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void inicializarLabels(){
		descripcion = new Label("Descripcion");
		comunidad = new Label("Comunidad");
		organizador = new Label("Organizador");
	}
	
	public void mostrarDatos(){
		descripcion.setVisible(true);
		organizador.setVisible(true);
		comunidad.setVisible(true);
		tipo.setVisible(true);
		estilo.setVisible(true);
		inicioInscripcion.setVisible(true);
		inicioVotacion.setVisible(true);
		inicioDiscusion.setVisible(true);
		finInscripcion.setVisible(true);
		finVotacion.setVisible(true);
		finDiscusion.setVisible(true);
		periodoInscripcion.setVisible(true);
		periodoDiscusion.setVisible(true);
		periodoVotacion.setVisible(true);
	}
	
	public void removerDatos(){
		descripcion.setVisible(false);
		organizador.setVisible(false);
		comunidad.setVisible(false);
		tipo.setVisible(false);
		estilo.setVisible(false);
		inicioInscripcion.setVisible(false);
		inicioVotacion.setVisible(false);
		inicioDiscusion.setVisible(false);
		finInscripcion.setVisible(false);
		finVotacion.setVisible(false);
		finDiscusion.setVisible(false);
		periodoInscripcion.setVisible(false);
		periodoDiscusion.setVisible(false);
		periodoVotacion.setVisible(false);
		if (tablaTendencias != null){
			tablaTendencias.setVisible(false);
		}
	}
	
	public void mostrarTendencias(){
		tablaTendencias = new TendenciasTable(nombre.getValue().toString());
		layout.addComponent(tablaTendencias);
		if (tablaTendencias.getTendenciasContainer().size()!= 0){
			tablaTendencias.setVisible(true);
		}
		else{
			tablaTendencias.setVisible(false);
		}
	}
	
	public void mostrarError(){
		nombre.setVisible(false);
		error.setVisible(true);
	}
	
}


