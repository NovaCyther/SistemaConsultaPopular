package com.example.proyvotaciones;

import java.util.ArrayList;
import java.util.Date;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class foro extends Panel{
	public DBManager manejoDB;
	ComboBox listaVotaciones;
	Button OK;
	Button aceptar;
	Button nuevoTema;
	String nombre;
	TextField titulo;
	ArrayList<String> titulos;
	ComboBox titulosF;
	String tema;
	TextArea responder;
	String Nplebiscito;
	public foro(){
		manejoDB=new DBManager();
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		listaVotaciones = new ComboBox("Procesos de votacion");
		listaVotaciones.setNullSelectionAllowed(false);
		manejoDB.procesosVotacion();

		    try{
				for(int i=0;i<manejoDB.votaciones.size();i++){
					if(null!=manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("finperiododiscusion", manejoDB.votaciones.get(i)))&&null!=manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioperiododiscucion", manejoDB.votaciones.get(i)))){
						if((manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("finperiododiscusion", manejoDB.votaciones.get(i)))).before(new Date())&&(manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioperiododiscucion", manejoDB.votaciones.get(i)))).after(new Date())){
							listaVotaciones.addItem(manejoDB.votaciones.get(i));
						}
					}else{
						listaVotaciones.addItem(manejoDB.votaciones.get(i));	
					}
				}
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		  OK = new Button("Elegir votacion");
		  nuevoTema=new Button("Nuevo Tema");
		  OK.addClickListener(new Button.ClickListener() {
              public void buttonClick(ClickEvent event) {
            	  if(titulosF!=null)titulosF.setVisible(false);
            	  if(aceptar!=null)aceptar.setVisible(false);
            	  titulosF=new ComboBox("Titulo temas");
          		  titulos=manejoDB.getTitulos(listaVotaciones.getValue().toString());
          		  for(int i=0;i<titulos.size();i++){
          			  titulosF.addItem(titulos.get(i));
          		  }
          		  aceptar=new Button("Ver tema");
        		  layout.addComponent(titulosF);
        		  aceptar.addClickListener(new Button.ClickListener() {
                      public void buttonClick(ClickEvent event) {
                    	  LayoutEscogido(titulosF.getValue().toString(),listaVotaciones.getValue().toString());
                      }
                  });
        		  nuevoTema.setVisible(false);
        		  
          		  nuevoTema=new Button("Nuevo Tema");
        		  layout.addComponent(titulosF);
        		  layout.addComponent(aceptar);
        		  nuevoTema.addClickListener(new Button.ClickListener() {
                      public void buttonClick(ClickEvent event) {
                    	  nuevoTema(listaVotaciones.getValue().toString());
                      }
                  });
        		  layout.addComponent(nuevoTema);
              }
          });
		  
		  layout.addComponent(listaVotaciones);
		  layout.addComponent(OK);
	}
	
	public void LayoutEscogido(String nombreTema,String plebiscito){
		tema=nombreTema;
		Nplebiscito=plebiscito;
		Label usuario;
		TextArea textoU;
		titulos=new ArrayList();
		titulos=manejoDB.getTexto(nombreTema,plebiscito);
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
        layout.addStyleName("outlined");
        layout.setSizeFull();
        layout.setSpacing(true);
        Label nombreTemaAct=new Label("<b>"+tema+"</b>",Label.CONTENT_XHTML);
       // nombreTemaAct.s
        layout.addComponent(nombreTemaAct);
        for(int i=0;i<titulos.size();i=i+2){
        	textoU=new TextArea("Autor:"+titulos.get(i));
        	textoU.setValue(titulos.get(i+1));
        	textoU.setReadOnly(true);
        	textoU.setWidth("50%");
        	textoU.setHeight("50%");
        	
        	layout.addComponent(textoU);
        }
        responder =new TextArea("Autor: Luis");
        responder.setWidth("100%");
        responder.setHeight("100%");
        
        Button responderTema = new Button("Responder");
        responderTema.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	DBManager manejoDB=new DBManager();
            	manejoDB.agregarPost(tema,Nplebiscito, "Luis", responder.getValue());
            	LayoutEscogido(tema,Nplebiscito);
            }
        });
       	
       	layout.addComponent(responder);
       	layout.addComponent(responderTema);
	}
	
	public void nuevoTema(String plebiscito){
		Nplebiscito=plebiscito;
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
        layout.addStyleName("outlined");
        layout.setSizeFull();
        titulo = new TextField("Titulo tema");
        responder =new TextArea("Autor: Luis");
        responder.setWidth("100%");
        responder.setHeight("100%");
 
        Button responderTema = new Button("Agregar Tema");
        responderTema.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	DBManager manejoDB=new DBManager();
            	titulos=manejoDB.getTitulos(Nplebiscito);
            	manejoDB.agregarPost(titulo.getValue().toString(),Nplebiscito, "Luis", responder.getValue());
            	LayoutEscogido(titulo.getValue().toString(),Nplebiscito);
            	//titulo.setVisible(true);
            }
        });
       	layout.addComponent(titulo);
       	layout.addComponent(responder);
       	layout.addComponent(responderTema);
        
	}
}
