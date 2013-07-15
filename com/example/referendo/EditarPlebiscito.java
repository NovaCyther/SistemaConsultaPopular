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
public class EditarPlebiscito extends Panel{
	
	ComboBox nombre;
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
	Button editarPadron;
	Button eliminarPlebiscito;
	Button finalizarEdicion;
	DBManager dBManager;
	EditarPlebiscito container;
	Validador validador;
	SQLContainer containerNombre;
	CSVLoader csvLoader;
	private File tempFile;
	FileReader reader;
	EditarPadron editarPadronWindow;
	String pk;
	Image image;
	File imgFile;
	String iniIns, finIns, iniDis, finDis, iniVot, finVot;

	public EditarPlebiscito(final MainView mainView) {
		
		validador = new Validador();
		dBManager = new DBManager();
		pk = "";
		
		final FormLayout layout = new FormLayout();
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
				if (nombre.getValue() != null){
					obtenerDatos();
				}
			}
        });
        
        
        try {
        	JDBCConnectionPool sqlPool = new SimpleJDBCConnectionPool("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "postgres", "1234",2,5);
        	TableQuery q = new TableQuery("plebiscito", sqlPool);
			containerNombre = new SQLContainer(q);
			nombre.setContainerDataSource(containerNombre);
			nombre.setItemCaptionPropertyId("nombreplebiscito");
			nombre.setImmediate(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /*Iniciializa la imagen subida*/
        final Image image = new Image("Imagen Subida");
	    image.setVisible(false);
      
	    
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
        
        
       
	    /*Inicializacion de nombre, descripcion, comunidad y organizador*/
	    inicializarTextFields();
	    
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
    		layout.addComponent(image);
        
 
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
        
        editarPadron = new Button("Editar padrón");
        editarPadron.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	if (nombre.getValue() != null){
            		editarPadronWindow = new EditarPadron(mainView, nombre.getValue().toString());
            		nombre.setComponentError(null);
            		editarPadron.setComponentError(null);
            	}
            	else{
            		nombre.setComponentError(new UserError("Se debe eligir primero un plebiscito"));
            		editarPadron.setComponentError(new UserError("Se debe eligir primero un plebiscito"));
            	}
            }
        });
        
        eliminarPlebiscito = new Button("Eliminar plebiscito");
        eliminarPlebiscito.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	if (nombre.getValue() != null){
            		try {
						dBManager.borrarFilaTabla("plebiscito", nombre.getValue().toString());
						setToNullComponents();
						Window guardarCambiosWindow = new Window("Eliminar Plebiscito", new Label("Plebiscito eliminado con exito"));
	            		mainView.getUI().addWindow(guardarCambiosWindow);
	            		guardarCambiosWindow.setPositionX(200);
	            		guardarCambiosWindow.setPositionY(100);
	            		
	            		
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
        });

        finalizarEdicion = new Button("Finalizar la edición");
        finalizarEdicion.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	boolean exito = true;
            	if ( verificarErrorDatos() == false ){
            		try {
            			if (reader != null){
	            			String[] columnTypes = {"int","String","String","String","String"};
	            			csvLoader = new CSVLoader( dBManager.crearConexion() );
							csvLoader.loadCSV(reader, "padron", "cedula, apellido1, apellido2, nombre, nombreplebiscito", columnTypes, nombre.getValue().toString(), true);
							reader = null;
            			}
            		} catch (Exception e) {
						// TODO Auto-generated catch block
						upload.setComponentError(new UserError("Revisar formato del archivo del padron"));
						e.printStackTrace();
						exito = false;
					}
					try{	
						String columnNames = "nombreplebiscito, organizador, descripcion, comunidad, tipo, estilo";/*+ 
				        "inicioinscripciontendencias, inicioperiododiscucion,"+ 
				       "inicioperiodovotacion, fininscripciontendencias, finperiododiscusion,"+ 
				       "finperiodovotacion";*/
			
						calculoFechas();
						String[] values = {nombre.getValue().toString(), organizador.getValue(), descripcion.getValue(), comunidad.getValue(), tipo.getValue().toString().substring(0, 1),
							       estilo.getValue().toString().substring(0, 1), iniIns, iniDis, iniVot, finIns, finDis, finVot};
						dBManager.updateData("plebiscito", columnNames, values);
						if (imgFile != null){
							dBManager.addImagePlebiscito(imgFile.getAbsolutePath(), nombre.getValue().toString());
						}
						nombre.setComponentError(null);
						finalizarEdicion.setComponentError(null);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						finalizarEdicion.setComponentError(new UserError("Error edicion en la base de datos"));
						exito = false;
						e.printStackTrace();
					}
					if (exito){
						Window guardarCambiosWindow = new Window("Guardar Cambios", new Label("Cambios Guardados con exito"));
	            		mainView.getUI().addWindow(guardarCambiosWindow);
	            		guardarCambiosWindow.setPositionX(100);
	            		guardarCambiosWindow.setPositionY(100);
					}
            	}
            }
        });
        
        final HorizontalLayout botonera = new HorizontalLayout(
        		editarPadron, eliminarPlebiscito, finalizarEdicion);
        layout.addComponent(botonera);
	}
	
	public void obtenerDatos(){
		 try {
			 	pk = nombre.getValue().toString();
			 	if (dBManager.SelectValuePlebiscito("estilo", pk).equals("A")){
			 		estilo.select("Abierto");
			 	}else{
			 		estilo.select("Cerrado");
			 	}
			 	if (dBManager.SelectValuePlebiscito("tipo", pk).equals("R")){
			 		tipo.select("Referendo");
			 	}else{
			 		tipo.select("Plebiscito");
			 	}
				
				descripcion.setValue(dBManager.SelectValuePlebiscito("descripcion", pk));
				organizador.setValue(dBManager.SelectValuePlebiscito("organizador", pk));
				comunidad.setValue(dBManager.SelectValuePlebiscito("comunidad", pk));
				inicioInscripcion.setValue(dBManager.convertirAFechaPopUpField(dBManager.SelectValuePlebiscito("inicioinscripciontendencias", pk)));
				inicioVotacion.setValue(dBManager.convertirAFechaPopUpField(dBManager.SelectValuePlebiscito("inicioperiodovotacion", pk)));
				inicioDiscusion.setValue(dBManager.convertirAFechaPopUpField(dBManager.SelectValuePlebiscito("inicioperiododiscucion", pk)));
				finInscripcion.setValue(dBManager.convertirAFechaPopUpField(dBManager.SelectValuePlebiscito("fininscripciontendencias", pk)));
				finVotacion.setValue(dBManager.convertirAFechaPopUpField(dBManager.SelectValuePlebiscito("finperiodovotacion", pk)));
				finDiscusion.setValue(dBManager.convertirAFechaPopUpField(dBManager.SelectValuePlebiscito("finperiododiscusion", pk)));
		 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void inicializarTextFields(){
		descripcion = new TextArea("Descripcion", "");
		comunidad = new TextField("Comunidad", "");
		organizador = new TextField("Organizador", "");
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
	
	public boolean verificarErrorDatos(){
		boolean hayError = false;
		if ( validador.verificarTextArea(descripcion) 
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
		if (validador.verificarCombobox(nombre) | validador.verificarCombobox(tipo) | validador.verificarCombobox(estilo)){
			hayError = true;
		}
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

