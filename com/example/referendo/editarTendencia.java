package com.example.proyvotaciones;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.vaadin.server.FileResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;

public class editarTendencia extends Panel {
	DBManager manejoDB;
	ComboBox listaVotaciones;
	Button OK;
	ArrayList<String> infoTendencia;
	ArrayList<String> tendencias;
	Button aceptar;
	ComboBox titulosF;
	
	
	public String nombre="";
	public int cantTot=0;
	public String tipo="plebiscito";
	public String procesoVotacionPertenece="";
	public String representante="";
	public String descripcion="";
	public String pagina="";
	public String contacto="";
	public String infoAd="";
	boolean validador=true;
	public ArrayList<String> miembros;
	public tendencias nuevaTendencia;
	TextField nombreTendencia;
	TextArea descripcionTende;
	Label representanteNombre;
	TextField paginaWeb;
	TextField infoContacto;
	TextArea infoAdicional;
	Upload imgUpload;
	TextField nombreMiembro;
	Button agregarMiembro;
	Button quitarMiembro;
	ComboBox listaMiembors;
	Button inscribirPlebiscito;
	Label mensaje;
	private File imgFile;
	Button borrarPlebiscito;
	
	public editarTendencia(){
		nuevaTendencia=new tendencias();
		manejoDB=new DBManager();
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		listaVotaciones = new ComboBox("Procesos de votacion");
		listaVotaciones.setNullSelectionAllowed(false);
		manejoDB.procesosVotacion();
	
		for(int i=0;i<manejoDB.votaciones.size();i++){
			try {
				if(null!=manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("fininscripciontendencias", manejoDB.votaciones.get(i)))&&null!=manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioinscripciontendencias", manejoDB.votaciones.get(i)))){
					if((manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("fininscripciontendencias", manejoDB.votaciones.get(i)))).before(new Date())&&(manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioinscripciontendencias", manejoDB.votaciones.get(i)))).after(new Date())){
									listaVotaciones.addItem(manejoDB.votaciones.get(i));
					}
				}else{
					listaVotaciones.addItem(manejoDB.votaciones.get(i));			
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		miembros=new ArrayList();
		  OK = new Button("Elegir votacion");
		  
		  OK.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
          	  if(titulosF!=null)titulosF.setVisible(false);
          	  if(aceptar!=null)aceptar.setVisible(false);
          	  titulosF=new ComboBox("tendencias");
          	manejoDB.getTendencias(listaVotaciones.getValue().toString());
    		tendencias=manejoDB.tendencias;
    		infoTendencia=new ArrayList<String>();
    		
    		
    		
 
    		
        		  for(int i=0;i<tendencias.size();i++){
        			  titulosF.addItem(tendencias.get(i));
        		  }
        		  aceptar=new Button("Editar tendencia");
      		  layout.addComponent(titulosF);
      		  aceptar.addClickListener(new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                    	infoTendencia=manejoDB.infoTendencias(titulosF.getValue().toString(),listaVotaciones.getValue().toString());
                    	miembros=manejoDB.getMiembrosTendencia(listaVotaciones.getValue().toString(), titulosF.getValue().toString());
                    	nuevaTendencia.miembrosant=manejoDB.getMiembrosTendencia(listaVotaciones.getValue().toString(), titulosF.getValue().toString());
                    	
                    	System.out.println(infoTendencia.toString());
                    	LayoutEscogido(listaVotaciones.getValue().toString());
                    }
                });
      		  layout.addComponent(aceptar);
            }
        });
		  
		  layout.addComponent(listaVotaciones);
		  layout.addComponent(OK);
	}
	
	public void LayoutEscogido(String nombreVotacion){
		nombre=nombreVotacion;
		tipo=manejoDB.getTipoProceso(nombreVotacion);
		cantTot=manejoDB.getCantTendencias(nombreVotacion);



		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
        layout.addStyleName("outlined");
        layout.setSizeFull();
        layout.setSpacing(true);
        listaMiembors = new ComboBox("Miembros");
        listaMiembors.setNullSelectionAllowed(false);
        inicializarTextFields(tipo);
        nombreTendencia.setReadOnly(true);
	        layout.addComponent(representanteNombre);
	        layout.addComponent(nombreTendencia);
	        layout.addComponent(descripcionTende);
	       
	        if(tipo.equals("P")){
	            layout.addComponent(nombreMiembro);
	            
	            agregarMiembro = new Button("Agregar Miembro");
	            agregarMiembro.addClickListener(new Button.ClickListener() {
	                public void buttonClick(ClickEvent event) {
	                	agregarMiembro();
	                }
	            });
	            
	            
	            layout.addComponent(agregarMiembro); 
	            quitarMiembro = new Button("Quitar Miembro");
	            quitarMiembro.addClickListener(new Button.ClickListener() {
	                public void buttonClick(ClickEvent event) {
	                	quitarMiembro();
	                }
	            });
	            
	            layout.addComponent(listaMiembors);
	            layout.addComponent(quitarMiembro);
	        }else{     	
	        	
	        }
	
	        
	
	
	        
	        layout.addComponent(paginaWeb);
	        layout.addComponent(infoContacto);
	        layout.addComponent(infoAdicional);
	        
		    final Image image = new Image("Imagen Subida");
		    imgFile=new File("C:/Users/lbarboza/Desktop/eclipse/"+nombre+nombreTendencia.getValue());
		    image.setSource(new FileResource(imgFile));
		    image.setVisible(true);
	        final Upload imgUpload = new Upload("Archivo de la imagen ",
				new Upload.Receiver() {
					public OutputStream receiveUpload(String filename,
							String mimeType) {
						// Create upload stream
						FileOutputStream fos = null; // Stream to write to
						try {
							String nombreDB=nombre+nombreTendencia.getValue();
							imgFile = new File(".\\" + nombreDB);
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
	        
	        inscribirPlebiscito = new Button("Terminar Edicion");
	        inscribirPlebiscito.addClickListener(new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	            	
	            	nuevaTendencia.miembros=miembros;
	            	nuevaTendencia.nombre=nombreTendencia.getValue();
	            	nuevaTendencia.procesoVotacionPertenece=nombre;
	            	nuevaTendencia.representante="114350464";
	            	nuevaTendencia.descripcion=descripcionTende.getValue();
	            	nuevaTendencia.pagina=paginaWeb.getValue();
	            	nuevaTendencia.contacto=infoContacto.getValue();
	            	nuevaTendencia.infoAd=infoAdicional.getValue();
	            	validador=true;
	            	verificarDatos();
	            	if(validador){
	            		try {
							nuevaTendencia.editToDB();
							mensaje=new Label("Tendencia editada correctamente");
							inscribirPlebiscito.setVisible(false);
						} catch (UnsupportedOperationException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
							nombreTendencia.setComponentError(new UserError("Tendencia ya existe"));
							mensaje=new Label("Tendencia ya existe");
						}
	            	}else{
	            		
	            		mensaje=new Label("Faltan datos importantes");
	            	}
	            	layout.addComponent(mensaje);
	            	
	            }
	        });
	        layout.addComponent(inscribirPlebiscito);
	        borrarPlebiscito = new Button("Borrar Tendencia");
	        borrarPlebiscito.addClickListener(new Button.ClickListener() {
	            public void buttonClick(ClickEvent event) {
	            	
	            	try {
						nuevaTendencia.borrarDeDB(nombre,nombreTendencia.getValue());
						mensaje=new Label("Tendencia eliminada correctamente");
						borrarPlebiscito.setVisible(false);
						inscribirPlebiscito.setVisible(false);
					} catch (UnsupportedOperationException e) {
						e.printStackTrace();
					} 
	            	layout.addComponent(mensaje);
	            	
	            }
	        });
	        layout.addComponent(borrarPlebiscito);


	}	
	
	
	
	public void inicializarTextFields(String tipo){
		if(tipo.equals("R")){
			
		}else{
			agregarMiembrosDeLista();
		}
		
		nombreTendencia = new TextField("Nombre de la Tendencia", infoTendencia.get(0));
		descripcionTende = new TextArea("Descripcion", infoTendencia.get(3));
		representanteNombre = mensaje=new Label("Organizador:" +infoTendencia.get(2));
		paginaWeb =new TextField("paginaWeb", infoTendencia.get(4));
		infoContacto= new TextField("Informacion contacto", infoTendencia.get(5));
		infoAdicional= new TextArea("Informacion adicional", infoTendencia.get(6));
		nombreMiembro= new TextField("Nombre miembro", "");
		
		nombreTendencia.setRequired(true);
		descripcionTende.setRequired(true);
		//representanteNombre.setRequired(true);
		
		
	}
	
	public void verificarDatos(){
		verificarTextField(nombreTendencia);
		verificarTextArea(descripcionTende);
	//	verificarTextField(representanteNombre);
	}	
	public void verificarTextField(TextField textField){
		try{
			textField.validate();
			textField.setComponentError(null);
		}
		catch(Exception e){
			textField.setValidationVisible(true);
			textField.setComponentError(new UserError("Campo Requerido"));
			validador=false;
		} 
	}
	
	public void verificarTextArea(TextArea textArea){
		try{
			textArea.validate();
			textArea.setComponentError(null);
		}
		catch(Exception e){
			textArea.setValidationVisible(true);
			textArea.setComponentError(new UserError("Campo Requerido"));
			validador=false;
		} 
	}
	
	public void agregarMiembro(){
		if(nombreMiembro.getValue()!=null&&nombreMiembro.getValue()!=""){
			miembros.add(nombreMiembro.getValue());
			listaMiembors.addItem(nombreMiembro.getValue());
			nombreMiembro.setValue("");
		}
	}
	public void agregarMiembrosDeLista(){
		for(int i=0;i<miembros.size();i++){
			listaMiembors.addItem(miembros.get(i));
		}
	}
	public void quitarMiembro(){
		String nombre=listaMiembors.getValue().toString();
		boolean encontrar=true;
		int index=0;
		while(encontrar){
			if(miembros.get(index)==nombre){
				encontrar=false;
				miembros.remove(index);
			}else{
				index++;
			}
		}
		listaMiembors.removeAllItems();
		for(int i=0;i<miembros.size();i++){
			listaMiembors.addItem(miembros.get(i));
		}
		
	}
	
}
