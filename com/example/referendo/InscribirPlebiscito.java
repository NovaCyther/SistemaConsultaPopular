package com.example.proyvotaciones;

import com.example.proyvotaciones.NavigatorUI.MainView;
import com.vaadin.server.FileResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
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
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class InscribirPlebiscito extends Panel{
	
	TextField nombre;
	TextArea descripcion;
	TextField comunidad;
	TextField organizador;
	ComboBox tipo;
	ComboBox estilo;
	PopupDateField inicioInscripcion;
	PopupDateField finInscripcion;
	PopupDateField inicioDiscusion;
	PopupDateField finDiscusion;
	PopupDateField inicioVotacion;
	PopupDateField finVotacion;
	Button finalizarInscripcion;
	Button guardarCambios;
	CSVLoader csvLoader;
	DBManager dbManager;
	private File tempFile;
	private File imgFile;
	FileReader reader;
	Validador validador;
	String iniIns, finIns, iniDis, finDis, iniVot, finVot;
	FileInputStream fis;
	
	public InscribirPlebiscito(final MainView mainView) {
		
		dbManager = new DBManager();
		validador = new Validador();
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
        layout.addStyleName("outlined");
        layout.setSizeFull();
        layout.setSpacing(true);
        
        /*Inicializacion de tipo*/
        tipo = new ComboBox("Tipo");
        tipo.setNullSelectionAllowed(false);
        tipo.addItem("Referendo");
        tipo.addItem("Plebiscito");
        tipo.setRequired(true);
        
        /*Inicializacion de estilo*/
        estilo = new ComboBox("Estilo");
        estilo.setNullSelectionAllowed(false);
        estilo.addItem("Abierto");
        estilo.addItem("Cerrado");
        estilo.setRequired(true);
        //estilo.select("Abierto");
	    
	   
	    
	    /*Inicializacion de nombre, descripcion, comunidad y organizador*/
	    inicializarTextFields();
	    
	    /*Inicializa la imagen*/
	    final Image image = new Image("Imagen Subida");
	    image.setVisible(false);
	    
	    /*Inicializa las fechas*/
		inicioInscripcion = new PopupDateField("Inicio");
		inicioInscripcion.setDateFormat("dd/MM/yyyy");
		finInscripcion = new PopupDateField("Fin");
		finInscripcion.setDateFormat("dd/MM/yyyy");
		inicioDiscusion = new PopupDateField("Inicio");
		inicioDiscusion.setDateFormat("dd/MM/yyyy");
		finDiscusion = new PopupDateField("Fin");
		finDiscusion.setDateFormat("dd/MM/yyyy");
		inicioVotacion = new PopupDateField("Inicio");
		inicioVotacion.setDateFormat("dd/MM/yyyy");
		finVotacion = new PopupDateField("Fin");
		finVotacion.setDateFormat("dd/MM/yyyy");
        
	
	    layout.addComponent(nombre);
        layout.addComponent(descripcion);
        layout.addComponent(comunidad);
        layout.addComponent(organizador);
        layout.addComponent(tipo);
        layout.addComponent(estilo);
        
        final Upload imgUpload = new Upload("Archivo de la imagen ",
			new Upload.Receiver() {
				public OutputStream receiveUpload(String filename,
						String mimeType) {
					// Create upload stream
					FileOutputStream fos = null; // Stream to write to
					try {
						// Open the file for writing.
						imgFile = new File(".\\" + filename);
						fos = new FileOutputStream(imgFile);
						fis = new FileInputStream(imgFile);
					} catch (final java.io.FileNotFoundException e) {
						Notification.show("Could not open file<br/>",
								e.getMessage(),
								Notification.TYPE_ERROR_MESSAGE);
						return null;
					}
					return fos; // Return the output stream to write to
				}
			});
        imgUpload.addFinishedListener((new Upload.FinishedListener() {
	        @Override
	        public void uploadFinished(Upload.FinishedEvent finishedEvent) {
	        	image.setVisible(true);
				image.setSource(new FileResource(imgFile));
	        }
        }));
		layout.addComponent(imgUpload);

        final Upload upload = new Upload("Archivo del Padrón ", new Upload.Receiver() {
        	@Override
        	public OutputStream receiveUpload(String filename, String mimeType) {
        		 try {
        		 /* Here, we'll stored the uploaded file as a temporary file. No doubt there's
        		 a way to use a ByteArrayOutputStream, a reader around it, use ProgressListener (and
        		 a progress bar) and a separate reader thread to populate a container *during*
        		 the update.
        		  
        		 This is quick and easy example, though.
        		 */
        			 tempFile = File.createTempFile("temp", ".csv");
        			 return new FileOutputStream(tempFile);
        		 } catch (IOException e) {
        			 e.printStackTrace();
        			 return null;
        		 }
        	}
        });
        upload.addFinishedListener((new Upload.FinishedListener() {
	        @Override
	        public void uploadFinished(Upload.FinishedEvent finishedEvent) {
	        	try {
	        		/* Let's build a container from the CSV File */
	        		 reader = new FileReader(tempFile);
	        		 tempFile.delete();
	        		 
	        	} catch (IOException e) {
	        		 e.printStackTrace();
	        	}
	        }
        }));
        layout.addComponent(upload);
        layout.addComponent(image);
        
        
        final HorizontalLayout periodoInscripcion = new HorizontalLayout(
                inicioInscripcion, finInscripcion);
        periodoInscripcion.setCaption("Perido de inscripción de tendencias");
        periodoInscripcion.setSpacing(true);
        layout.addComponent(periodoInscripcion);
        
        
        final HorizontalLayout periodoDiscusion = new HorizontalLayout(
                inicioDiscusion, finDiscusion);
        periodoDiscusion.setCaption("Perido de discusión");
        periodoDiscusion.setSpacing(true);
        layout.addComponent(periodoDiscusion);
        
        final HorizontalLayout periodoVotacion = new HorizontalLayout(
        		inicioVotacion, finVotacion);
        periodoVotacion.setCaption("Perido de votación");
        periodoVotacion.setSpacing(true);
        layout.addComponent(periodoVotacion);
        
        finalizarInscripcion = new Button("Finalizar la inscripcion");
        finalizarInscripcion.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	boolean exito = true;
            	if ( verificarErrorDatos() == false ){
            		try {
						String columnNames = "nombreplebiscito, organizador, descripcion, comunidad, tipo, estilo";/*+ 
				        "inicioinscripciontendencias, inicioperiododiscucion,"+ 
				       "inicioperiodovotacion, fininscripciontendencias, finperiododiscusion,"+ 
				       "finperiodovotacion";*/
						
						
						calculoFechas();
						/*String[] values = {nombre.getValue(), organizador.getValue(), descripcion.getValue(), comunidad.getValue(), tipo.getValue().toString().substring(0, 1),
							       estilo.getValue().toString().substring(0, 1), inicioInscripcion.getValue().toString(), inicioDiscusion.getValue().toString(),
							       inicioVotacion.getValue().toString(), finInscripcion.getValue().toString(), finDiscusion.getValue().toString(),
							       finVotacion.getValue().toString() };*/
						
						String[] values = {nombre.getValue(), organizador.getValue(), descripcion.getValue(), comunidad.getValue(), tipo.getValue().toString().substring(0, 1),
					       estilo.getValue().toString().substring(0, 1), iniIns, iniDis, iniVot, finIns, finDis, finVot};
						dbManager.insertData("plebiscito", columnNames, values);
						if (imgFile != null){
							dbManager.addImagePlebiscito(imgFile.getAbsolutePath(), nombre.getValue());
						}
						nombre.setComponentError(null);
						finalizarInscripcion.setComponentError(null);
            		}catch(Exception e){
							nombre.setComponentError(new UserError("Ya existe un proceso con ese nombre"));
							finalizarInscripcion.setComponentError(new UserError("Ya existe un proceso con ese nombre"));
							exito = false;
							e.printStackTrace();
					}
					try{
						if (reader != null){
	            			String[] columnTypes = {"int","String","String","String","String"};
	            			csvLoader = new CSVLoader( dbManager.crearConexion() );
	            			csvLoader.loadCSV(reader, "padron", "cedula, apellido1, apellido2, nombre, nombreplebiscito", columnTypes, nombre.getValue().toString(), false);
							reader = null;
            			}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						upload.setComponentError(new UserError("Revisar formato del archivo del padron"));
						e.printStackTrace();
						exito = false;
					}
					if (exito){
						Window guardarCambiosWindow = new Window("Guardar Cambios", new Label("Cambios Guardados con exito"));
	            		mainView.getUI().addWindow(guardarCambiosWindow);
	            		guardarCambiosWindow.setPositionX(200);
	            		guardarCambiosWindow.setPositionY(100);
					}
            	}
            }
        });
        layout.addComponent(finalizarInscripcion);

	}
	
	
	public void inicializarTextFields(){
		nombre = new TextField("Nombre del Proceso", "");
		descripcion = new TextArea("Descripcion", "");
		comunidad = new TextField("Comunidad", "");
		organizador =new TextField("Organizador", "");
		nombre.setRequired(true);
		descripcion.setRequired(true);
		comunidad.setRequired(true);
		organizador.setRequired(true);
	}
	
	
	public boolean verificarFechasTraslapadas(){
		boolean hayError = false;
		try{
			if (finInscripcion.getValue()  != null && inicioVotacion.getValue()  != null ) {
				if (finInscripcion.getValue().equals(inicioVotacion.getValue()) || 
					finInscripcion.getValue().after(inicioVotacion.getValue())){
					inicioVotacion.setComponentError(new UserError("Las fechas de inscipcion y votacion no se pueden traslapar"));
					finInscripcion.setComponentError(new UserError("Las fechas de inscipcion y votacion no se pueden traslapar"));
					hayError = true;
				}
			}
			else{
				inicioVotacion.setComponentError(null);
				finInscripcion.setComponentError(null);
			}
		}
		catch(Exception e){
			finInscripcion.setValidationVisible(true);
			inicioVotacion.setValidationVisible(true);
			hayError = true;
		}
		return hayError;
		
	}
	
	
	
	public boolean verificarPadron(){
		return false;
		//!!!Falta
	}
	
	public boolean verificarImagen(){
		return false;
		//!!!Falta
	}
	
	public boolean verificarErrorDatos(){
		boolean hayError = false;
		if ( validador.verificarTextField(nombre) | validador.verificarTextArea(descripcion) 
				| validador.verificarTextField(comunidad) | validador.verificarTextField(organizador) | 
				validador.verificarFechas(inicioDiscusion, finDiscusion) ){
			hayError = true;
		}
		else{
			if ( validador.verificarCampoCedula(organizador) ){
				hayError = true;
			}
		}
		if ( !validador.verificarFechas(inicioInscripcion, finInscripcion) & 
				!validador.verificarFechas(inicioVotacion, finVotacion) ){
			if ( verificarFechasTraslapadas() ){
				hayError = true;
			}
		}
		else{
			hayError = true;
		}
		if (validador.verificarCombobox(tipo) | validador.verificarCombobox(estilo)){
			hayError = true;
		}
		verificarImagen();
		verificarPadron();
		return hayError;
	}

	public void setToNullComponents(){
		nombre.setValue(null);
		descripcion.setValue("");
		organizador.setValue("");
		comunidad.setValue("");
		tipo.setValue(null);
		estilo.setValue(null);
		inicioInscripcion.setValue(null);
		inicioVotacion.setValue(null);
		inicioDiscusion.setValue(null);
		finInscripcion.setValue(null);
		finVotacion.setValue(null);
		finDiscusion.setValue(null);
	}
	
	public void calculoFechas(){
		if (inicioInscripcion.getValue() == null){
			iniIns = ""; 
		}
		else{
			iniIns = inicioInscripcion.getValue().toString();
		}
		if (finInscripcion.getValue() == null){
			finIns = ""; 
		}
		else{
			finIns = finInscripcion.getValue().toString();
		}
		
		if (inicioVotacion.getValue() == null){
			iniVot = ""; 
		}
		else{
			iniVot = inicioVotacion.getValue().toString();
		}
		if (finVotacion.getValue() == null){
			finVot = ""; 
		}
		else{
			finVot = finVotacion.getValue().toString();
		}
		
		if (inicioDiscusion.getValue() == null){
			iniDis = ""; 
		}
		else{
			iniDis = inicioDiscusion.getValue().toString();
		}
		if (finDiscusion.getValue() == null){
			finDis = ""; 
		}
		else{
			finDis = finDiscusion.getValue().toString();
		}
	}
	
}
