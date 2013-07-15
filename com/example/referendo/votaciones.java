package com.example.proyvotaciones;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import com.example.proyvotaciones.NavigatorUI.MainView.ButtonListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Button.ClickEvent;


public class votaciones extends Panel {
	String cedula="114350464";
	DBManager manejoDB;
	ComboBox listaVotaciones;
	Button OK;
	public ArrayList<String> tendencias;
	public ArrayList<ArrayList<String>> infoTendencia;
	public ArrayList<CheckBox> opciones;
	int porQuienVotar;
	Button votarYa;
	String votacion;
	public File file;
	Upload upload;
	boolean pruebaLlave=false;
	firmaDigital comprobacionPersonas;
	String nombreFile;
	Label mensaje;
	ComboBox personas;
	File imgFile;
	public votaciones(){
		manejoDB=new DBManager();
		comprobacionPersonas=new firmaDigital();
		final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		listaVotaciones = new ComboBox("Procesos de votacion");
		listaVotaciones.setNullSelectionAllowed(false);
		manejoDB.procesosVotacion();
		
		
		  //ESTO ES SOLO PARA LA DEMOSTRACION
		personas = new ComboBox();
		personas.addItem("114350464");
		personas.addItem("100000000");
		personas.addItem("111111111");
		personas.addItem("114220164");
		personas.addItem("123456789");
		  //ESTO ES SOLO PARA LA DEMOSTRACION
		  personas.addBlurListener(new BlurListener(){
				@Override
				public void blur(BlurEvent event) {
					cedula=personas.getValue().toString();
					listaVotaciones.removeAllItems();
				    try{
						for(int i=0;i<manejoDB.votaciones.size();i++){
							if(null!=manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("finperiodovotacion", manejoDB.votaciones.get(i)))&&null!=manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioperiodovotacion", manejoDB.votaciones.get(i)))){
								if((manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("finperiodovotacion", manejoDB.votaciones.get(i)))).before(new Date())&&(manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioperiodovotacion", manejoDB.votaciones.get(i)))).after(new Date())){
									if(manejoDB.estaEnpadronado(manejoDB.votaciones.get(i),cedula)&&!manejoDB.yaVoto(manejoDB.votaciones.get(i), cedula)){
										listaVotaciones.addItem(manejoDB.votaciones.get(i));
									}
								}
							}else{
								if(manejoDB.estaEnpadronado(manejoDB.votaciones.get(i),cedula)&&!manejoDB.yaVoto(manejoDB.votaciones.get(i), cedula)){
									listaVotaciones.addItem(manejoDB.votaciones.get(i));
								}	
							}
						}
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
//*****************************************
	    try{
			for(int i=0;i<manejoDB.votaciones.size();i++){
				if(null!=manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioperiodovotacion", manejoDB.votaciones.get(i)))){
					if((manejoDB.convertirAFechaPopUpField(manejoDB.SelectValuePlebiscito("inicioperiodovotacion", manejoDB.votaciones.get(i)))).after(new Date())){
						if(manejoDB.estaEnpadronado(manejoDB.votaciones.get(i),cedula)&&!manejoDB.yaVoto(manejoDB.votaciones.get(i), cedula)){
							listaVotaciones.addItem(manejoDB.votaciones.get(i));
						}
					}
				}else{
					if(manejoDB.estaEnpadronado(manejoDB.votaciones.get(i),cedula)&&!manejoDB.yaVoto(manejoDB.votaciones.get(i), cedula)){
						listaVotaciones.addItem(manejoDB.votaciones.get(i));
					}		
				}
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		  OK = new Button("Aceptar");
		  

		  
		  
		  OK.addClickListener(new Button.ClickListener() {
              public void buttonClick(ClickEvent event) {
            	  LayoutEscogido(listaVotaciones.getValue().toString());
            	  
              }
          });
		  layout.addComponent(personas);
		  layout.addComponent(listaVotaciones);
		  layout.addComponent(OK);
	}
	
	public void LayoutEscogido(String nombreVotacion){
		final FormLayout layout = new FormLayout();
		votacion=nombreVotacion;
		manejoDB.getTendencias(nombreVotacion);
		tendencias=manejoDB.tendencias;
		infoTendencia=new ArrayList<ArrayList<String>>();
		opciones=new ArrayList<CheckBox>();

        upload = new Upload("Llave Privada ",
					new Upload.Receiver() {
						public OutputStream receiveUpload(String filename,
								String mimeType) {
							// Create upload stream
							FileOutputStream fos = null; // Stream to write to
							try {
								// Open the file for writing.
								file = new File(".\\" + filename);
								fos = new FileOutputStream(file);
							} catch (final java.io.FileNotFoundException e) {
								Notification.show("Could not open file<br/>",
										e.getMessage(),
										Notification.TYPE_ERROR_MESSAGE);
								return null;
							}
							nombreFile=filename;
							return fos; // Return the output stream to write to
						}
					});
		        upload.addFinishedListener((new Upload.FinishedListener() {
			        @Override
			        public void uploadFinished(Upload.FinishedEvent finishedEvent) {		
			        }
		        }));
 
		votarYa=new Button("Votar");
		votarYa.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	String dfirmaa="hola mundo";


            	if(comprobacionPersonas.firmar(nombreFile,cedula, dfirmaa.getBytes())){
                	votarYa.setVisible(false);
            		if(mensaje!=null){
            			mensaje.setVisible(false);
            		}
                	manejoDB.votar(votacion, tendencias.get(porQuienVotar), cedula);
        			mensaje=new Label("Voto hecho correctamente");
            	}else{
            		if(mensaje!=null){
            			mensaje.setVisible(false);
            		}
        			mensaje=new Label("Firma Digital erronea");
            	}
       			layout.addComponent(mensaje);

            }
        });
		
		 

		layout.setMargin(true);
		setContent(layout);
        layout.addStyleName("outlined");
        layout.setSizeFull();
        layout.setSpacing(true);
		for(int i=0;i<tendencias.size();i++){
			infoTendencia.add(manejoDB.infoTendencias(tendencias.get(i), nombreVotacion));	

		}
		
		for(int i=0;i<infoTendencia.size();i++){
			final int fml=i;
		    final Image image = new Image(infoTendencia.get(i).get(0));
		    imgFile=new File("C:/Users/lbarboza/Desktop/eclipse/"+votacion+infoTendencia.get(i).get(0));
		    image.setSource(new FileResource(imgFile));
		    image.setVisible(true);
		    layout.addComponent(image);
			//layout.addComponent(new Label(infoTendencia.get(i).get(0)));
			opciones.add(new CheckBox());
			opciones.get(i).addBlurListener(new BlurListener(){
				@Override
				public void blur(BlurEvent event) {
					opciones.get(fml).setValue(false);
					porQuienVotar=fml;
				}
			});
			layout.addComponent(opciones.get(i));
		}
		
	
		layout.addComponent(upload);
		votarYa.setWidth("50%");
		votarYa.setHeight("50%");
		
		layout.addComponent(votarYa);
		
		
	}
	
}
