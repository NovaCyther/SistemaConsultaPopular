package com.example.referendo;

import com.example.referendo.NavigatorUI.MainView;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
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
	FileReader reader;
	Validador validador;
	
	public InscribirPlebiscito(final MainView mainView) {
		
		dbManager = new DBManager();
		validador = new Validador();
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
        layout.addStyleName("outlined");
        layout.setSizeFull();
        layout.setSpacing(true);
        Upload imgUpload;
        
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
	    
		inicioInscripcion = new PopupDateField("Inicio");
		inicioInscripcion.setDateFormat("yyyy-mm-dd");
		finInscripcion = new PopupDateField("Fin");
		finInscripcion.setDateFormat("yyyy-mm-dd");
		inicioDiscusion = new PopupDateField("Inicio");
		inicioDiscusion.setDateFormat("yyyy-mm-dd");
		finDiscusion = new PopupDateField("Fin");
		finDiscusion.setDateFormat("yyyy-mm-dd");
		inicioVotacion = new PopupDateField("Inicio");
		inicioVotacion.setDateFormat("yyyy-mm-dd");
		finVotacion = new PopupDateField("Fin");
		finVotacion.setDateFormat("yyyy-mm-dd");
       
        layout.addComponent(nombre);
        layout.addComponent(descripcion);
        layout.addComponent(comunidad);
        layout.addComponent(organizador);
        layout.addComponent(tipo);
        layout.addComponent(estilo);
        
       
        imgUpload = new Upload("Archivo de la imagen ", null);
        imgUpload.setButtonCaption("Cargar archivo de la imagen");
        imgUpload.setButtonCaption("Start Upload");
        layout.addComponent(new Upload("Archivo de la imagen ", new Upload.Receiver() {
            public OutputStream receiveUpload(String filename, String MIMEType) {
                return null;
            }
        }));
        
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
        
        finalizarInscripcion = new Button("Finalizar la inscripcion");
        finalizarInscripcion.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	if ( verificarErrorDatos() == false ){
            		try {
						String columnNames = "nombreplebiscito, organizador, descripcion, comunidad, tipo, estilo";/*+ 
				        "inicioinscripciontendencias, inicioperiododiscucion,"+ 
				       "inicioperiodovotacion, fininscripciontendencias, finperiododiscusion,"+ 
				       "finperiodovotacion";*/
			
						String[] values = {nombre.getValue(), organizador.getValue(), descripcion.getValue(), comunidad.getValue(), tipo.getValue().toString().substring(0, 1),
							       estilo.getValue().toString().substring(0, 1), inicioInscripcion.getValue().toString(), inicioDiscusion.getValue().toString(),
							       inicioVotacion.getValue().toString(), finInscripcion.getValue().toString(), finDiscusion.getValue().toString(),
							       finVotacion.getValue().toString() };
						dbManager.insertData("plebiscito", columnNames, values);
						
						if (reader != null){
	            			String[] columnTypes = {"int","String","String","String","String"};
	            			csvLoader = new CSVLoader( dbManager.crearConexion() );
	            			csvLoader.loadCSV(reader, "padron", "cedula, apellido1, apellido2, nombre, nombreplebiscito", columnTypes, nombre.getValue().toString(), true);
							reader = null;
            			}
						
						Window guardarCambiosWindow = new Window("Guardar Cambios", new Label("Cambios Guardados con exito"));
	            		mainView.getUI().addWindow(guardarCambiosWindow);
	            		guardarCambiosWindow.setPositionX(200);
	            		guardarCambiosWindow.setPositionY(100);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						upload.setComponentError(new UserError("Revisar formato del archivo del padron"));
						e.printStackTrace();
					}
            	}
            }
        });
        layout.addComponent(finalizarInscripcion);

	}
	
	
	public void inicializarTextFields(){
		nombre = new TextField("Nombre del Plebiscito", "");
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
			if (finInscripcion.getValue().equals(inicioVotacion.getValue()) || 
					finInscripcion.getValue().after(inicioVotacion.getValue())){
				if (finInscripcion.getValue()  != null && inicioVotacion.getValue()  != null ) {
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

	/*
	 * Getters y setters
	 */

	public TextField getNombre() {
		return nombre;
	}


	public TextArea getDescripcion() {
		return descripcion;
	}


	public TextField getComunidad() {
		return comunidad;
	}


	public TextField getOrganizador() {
		return organizador;
	}


	public ComboBox getTipo() {
		return tipo;
	}


	public ComboBox getEstilo() {
		return estilo;
	}


	public PopupDateField getInicioInscripcion() {
		return inicioInscripcion;
	}


	public PopupDateField getFinInscripcion() {
		return finInscripcion;
	}


	public PopupDateField getInicioDiscusion() {
		return inicioDiscusion;
	}


	public PopupDateField getFinDiscusion() {
		return finDiscusion;
	}


	public PopupDateField getInicioVotacion() {
		return inicioVotacion;
	}


	public PopupDateField getFinVotacion() {
		return finVotacion;
	}


	public Button getFinalizarInscripcion() {
		return finalizarInscripcion;
	}


	public CSVLoader getCsvLoader() {
		return csvLoader;
	}


	public DBManager getDbManager() {
		return dbManager;
	}


	public File getTempFile() {
		return tempFile;
	}


	public FileReader getReader() {
		return reader;
	}


	public Validador getValidador() {
		return validador;
	}


	public void setNombre(TextField nombre) {
		this.nombre = nombre;
	}


	public void setDescripcion(TextArea descripcion) {
		this.descripcion = descripcion;
	}


	public void setComunidad(TextField comunidad) {
		this.comunidad = comunidad;
	}


	public void setOrganizador(TextField organizador) {
		this.organizador = organizador;
	}


	public void setTipo(ComboBox tipo) {
		this.tipo = tipo;
	}


	public void setEstilo(ComboBox estilo) {
		this.estilo = estilo;
	}


	public void setInicioInscripcion(PopupDateField inicioInscripcion) {
		this.inicioInscripcion = inicioInscripcion;
	}


	public void setFinInscripcion(PopupDateField finInscripcion) {
		this.finInscripcion = finInscripcion;
	}


	public void setInicioDiscusion(PopupDateField inicioDiscusion) {
		this.inicioDiscusion = inicioDiscusion;
	}


	public void setFinDiscusion(PopupDateField finDiscusion) {
		this.finDiscusion = finDiscusion;
	}


	public void setInicioVotacion(PopupDateField inicioVotacion) {
		this.inicioVotacion = inicioVotacion;
	}


	public void setFinVotacion(PopupDateField finVotacion) {
		this.finVotacion = finVotacion;
	}


	public void setFinalizarInscripcion(Button finalizarInscripcion) {
		this.finalizarInscripcion = finalizarInscripcion;
	}


	public void setCsvLoader(CSVLoader csvLoader) {
		this.csvLoader = csvLoader;
	}


	public void setDbManager(DBManager dbManager) {
		this.dbManager = dbManager;
	}


	public void setTempFile(File tempFile) {
		this.tempFile = tempFile;
	}


	public void setReader(FileReader reader) {
		this.reader = reader;
	}


	public void setValidador(Validador validador) {
		this.validador = validador;
	}
	
	public void enviarAInscribir(String columnNames, List<String> columnTypes, List<String> values){
		if ( !inicioInscripcion.getValue().toString().isEmpty() ){
			
		}
	}
}
