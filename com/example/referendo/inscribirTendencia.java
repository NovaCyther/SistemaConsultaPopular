package com.example.proyvotaciones;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ErrorMessage;
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
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;

public class inscribirTendencia extends Panel {

	public String nombre="";
	public String tipo="plebiscito";
	public String procesoVotacionPertenece="";
	//public String[] representante=new String[2];
	public String representante="";
	public String descripcion="";
	public String pagina="";
	public String contacto="";
	public String infoAd="";
	boolean validador=true;
	//public ArrayList<String[]> miembros;
	public ArrayList<String> miembros;
	public tendencias nuevaTendencia;
	public DBManager manejoDB;
	TextField nombreTendencia;
	TextArea descripcionTende;
	TextField representanteNombre;
	//TextField representanteCedula;
	TextField paginaWeb;
	TextField infoContacto;
	TextArea infoAdicional;
	Upload imgUpload;
	TextField nombreMiembro;
	//TextField cedulaMiembro;
	Button agregarMiembro;
	Button quitarMiembro;
	Button OK; 
	ComboBox listaMiembors;
	ComboBox listaVotaciones;
	Button inscribirPlebiscito;
	
	public inscribirTendencia(){
		manejoDB=new DBManager();
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		listaVotaciones = new ComboBox("Procesos de votacion");
		listaVotaciones.setNullSelectionAllowed(false);
		manejoDB.procesosVotacion();
		manejoDB.votaciones.add("2010");
		manejoDB.votaciones.add("2011");
		manejoDB.votaciones.add("2012");
		manejoDB.votaciones.add("2013");
		for(int i=0;i<manejoDB.votaciones.size();i++){
			listaVotaciones.addItem(manejoDB.votaciones.get(i));
			
		}
		
		  OK = new Button("Aceptar");
		  OK.addClickListener(new Button.ClickListener() {
              public void buttonClick(ClickEvent event) {
            	  LayoutEscogido(listaVotaciones.getValue().toString());
              }
          });
		  layout.addComponent(listaVotaciones);
		  layout.addComponent(OK);
	}
	
	
	
	
	public void LayoutEscogido(String nombreVotacion){
		nombre=nombreVotacion;
		if(nombre=="2012"){
			tipo="referendo";
		}else{
			tipo="plebiscito";
		}
		
		miembros=new ArrayList();
		nuevaTendencia=new tendencias();
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
        layout.addStyleName("outlined");
        layout.setSizeFull();
        layout.setSpacing(true);
        listaMiembors = new ComboBox("Miembros");
        listaMiembors.setNullSelectionAllowed(false);
        inicializarTextFields();
		
        layout.addComponent(nombreTendencia);
        layout.addComponent(descripcionTende);
        layout.addComponent(representanteNombre);
        if(tipo=="plebiscito"){
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
        /*
        imgUpload = new Upload("Archivo de la imagen ", null);
        imgUpload.setButtonCaption("Cargar archivo de la imagen");
        imgUpload.setButtonCaption("Start Upload");
        layout.addComponent(new Upload("Archivo de la imagen ", new Upload.Receiver() {
            public OutputStream receiveUpload(String filename, String MIMEType) {
                return null;
            }
        }));
        */
        
        inscribirPlebiscito = new Button("Inscribir");
        inscribirPlebiscito.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	
            	nuevaTendencia.miembros=miembros;
            	nuevaTendencia.nombre=nombreTendencia.getValue();
            	nuevaTendencia.procesoVotacionPertenece=nombre;
            	nuevaTendencia.representante=representanteNombre.getValue();
            	nuevaTendencia.descripcion=descripcionTende.getValue();
            	nuevaTendencia.pagina=paginaWeb.getValue();
            	nuevaTendencia.contacto=infoContacto.getValue();
            	nuevaTendencia.infoAd=infoAdicional.getValue();
            	validador=true;
            	verificarDatos();
            	if(validador){
            		try {
						nuevaTendencia.saveToDB();
					} catch (UnsupportedOperationException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
						nombreTendencia.setComponentError(new UserError("Tendencia ya existe"));
					}
            	}
            	
            	//navigator.navigateTo("main");
            }
        });
        layout.addComponent(inscribirPlebiscito);
	}
	
	
	
	public void agregarMiembro(){
		if(nombreMiembro.getValue()!=null&&nombreMiembro.getValue()!=""){
			miembros.add(nombreMiembro.getValue());
			listaMiembors.addItem(nombreMiembro.getValue());
			nombreMiembro.setValue("");
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
	
	
	public void inicializarTextFields(){
		nombreTendencia = new TextField("Nombre de la Tendencia", "");
		descripcionTende = new TextArea("Descripcion", "");
		representanteNombre = new TextField("Reprensentante", "");
		paginaWeb =new TextField("paginaWeb", "");
		infoContacto= new TextField("Informacion contacto", "");
		infoAdicional= new TextArea("Informacion adicional", "");
		nombreMiembro= new TextField("Nombre miembro", "");
		
		nombreTendencia.setRequired(true);
		descripcionTende.setRequired(true);
		representanteNombre.setRequired(true);
		
		
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
	
	public void verificarDatos(){
		verificarTextField(nombreTendencia);
		verificarTextArea(descripcionTende);
		verificarTextField(representanteNombre);
	}	
	
	public void verificarTendenciaDup(){
		
		
	}
	
}


