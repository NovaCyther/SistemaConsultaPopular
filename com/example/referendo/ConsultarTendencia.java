package com.example.proyvotaciones;

import java.sql.SQLException;
import java.util.ArrayList;

import com.example.proyvotaciones.NavigatorUI.MainView;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.data.util.filter.Compare.Equal;


public class ConsultarTendencia extends Panel{
	public DBManager manejoDB;
	public ArrayList<String> info;
	
	Label Lnombre;
	Label Lvotacion;
	Label Lrepresentante;
	Label Ldescripcion;
	Label pag;
	Link Lpagina;
	Label Lcontacto;
	Label LinfoAd;
	ComboBox nombrePlebiscito;
	ComboBox nombreTendencia;
	final FormLayout layout;
	MainView mainwindow;  // Reference to main window
    Window mywindow;    // The window to be opened
	private SQLContainer containerPlebiscito;
	private SQLContainer containerTendencia;
	JDBCConnectionPool sqlPool;
	MiembrosTable tablaMiembros;
	
	public ConsultarTendencia(final MainView mainView){
		mainwindow = mainView;
		layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		manejoDB = new DBManager();
		info = new ArrayList<String>();
		
		/*Inicializacion de nombre plebiscito*/
		nombrePlebiscito = new ComboBox("Nombre del Proceso");
		nombrePlebiscito.setNullSelectionAllowed(false);
		nombrePlebiscito.setRequired(true);
		nombrePlebiscito.addBlurListener(new BlurListener() {	
		@Override
		public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				removerDatos();
				if (nombrePlebiscito.getValue() != null){
					try {
			        	TableQuery t = new TableQuery("tendencia", sqlPool);
			        	containerTendencia = new SQLContainer(t);
			        	containerTendencia.addContainerFilter(
			        		    new Equal("nombreplebiscito", nombrePlebiscito.getValue().toString() ));
						nombreTendencia.setContainerDataSource(containerTendencia);
						nombreTendencia.setItemCaptionPropertyId("nombretendencia");
						nombreTendencia.setImmediate(true);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (containerTendencia.size() != 0){
						nombreTendencia.setVisible(true);
					}
					else{
						nombreTendencia.setVisible(false);
						/* Create a new window. */
				        mywindow = new Window("No existen tendencias inscritas", new Label("No existen tendencias inscritas en el proceso en este momento"));
				        mywindow.setPositionX(200);
				        mywindow.setPositionY(100);
				        mywindow.setHeight("100");
				        mywindow.setWidth("300");
				        /* Add the window inside the main window. */
				        mainwindow.getUI().addWindow(mywindow);
					}
				}
			}
        });
		
		try {
        	sqlPool = new SimpleJDBCConnectionPool("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "postgres", "1234",2,5);
        	TableQuery q = new TableQuery("plebiscito", sqlPool);
        	containerPlebiscito = new SQLContainer(q);
			nombrePlebiscito.setContainerDataSource(containerPlebiscito);
			nombrePlebiscito.setItemCaptionPropertyId("nombreplebiscito");
			nombrePlebiscito.setImmediate(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Inicializacion de nombre tendecia*/
		nombreTendencia = new ComboBox("Nombre de la Tendencia");
		nombreTendencia.setNullSelectionAllowed(false);
		nombreTendencia.setRequired(true);
		nombreTendencia.setVisible(false);
		nombreTendencia.addBlurListener(new BlurListener() {	
		@Override
		public void blur(BlurEvent event) {
				// TODO Auto-generated method stub
				removerDatos();
				if (nombreTendencia.getValue() != null){
					info=manejoDB.infoTendencias(nombreTendencia.getValue().toString().split("/")[0],nombrePlebiscito.getValue().toString());
					obtenerDatos();
					mostrarDatos();
					mostrarMiembros();
				}
			}
        });
		
		Lnombre = new Label();
		Lvotacion = new Label();
		Lrepresentante = new Label();
		Ldescripcion = new Label();
		Lpagina = new Link();
		Lcontacto = new Label();
		LinfoAd = new Label();
		pag = new Label();
		
		final HorizontalLayout paginaWeb = new HorizontalLayout(pag, Lpagina);
		
		layout.addComponent(nombrePlebiscito);
		layout.addComponent(nombreTendencia);
		layout.addComponent(Lnombre);
		layout.addComponent(Lvotacion);
		layout.addComponent(Lrepresentante);
		layout.addComponent(Ldescripcion);
		layout.addComponent(paginaWeb);
		layout.addComponent(Lcontacto);
		layout.addComponent(LinfoAd);
		removerDatos();
		
	}

	
	public void obtenerDatos(){
		Lnombre.setValue("Nombre Tendencia:     "+info.get(0));
		Lvotacion.setValue("Nombre del Proceso:   "+info.get(1));
		Lrepresentante.setValue("Nombre del Representante: "+info.get(2));
		Ldescripcion.setValue("Descripcion:          "+info.get(3));
		pag.setValue("Pagina web:  ");
		procesarPagina();
		Lcontacto.setValue("Informacion Contacto: "+info.get(5));
		LinfoAd.setValue("Informacion Adicional:"+info.get(6));
	}
	
	public void mostrarDatos(){
		Lnombre.setVisible(true);
		Lvotacion.setVisible(true);
		Lrepresentante.setVisible(true);
		Ldescripcion.setVisible(true);
		Lpagina.setVisible(true);
		Lcontacto.setVisible(true);
		LinfoAd.setVisible(true);
		pag.setVisible(true);
	}
	
	public void removerDatos(){
		Lnombre.setVisible(false);
		Lvotacion.setVisible(false);
		Lrepresentante.setVisible(false);
		Ldescripcion.setVisible(false);
		Lpagina.setVisible(false);
		Lcontacto.setVisible(false);
		LinfoAd.setVisible(false);
		pag.setVisible(false);
		if (tablaMiembros != null){
			tablaMiembros.setVisible(false);
		}
	}
	
	public void procesarPagina(){
		if (!info.get(4).isEmpty() && info.get(4).length() > 8){
			if ( info.get(4).substring(0,7).equals("http://") ){
				Lpagina.setCaption(info.get(4));
				Lpagina.setResource(new ExternalResource(info.get(4)));
			}else{
				Lpagina.setCaption("http://"+info.get(4));
				Lpagina.setResource(new ExternalResource("http://" +info.get(4)));
			}
			Lpagina.setTargetName("_blank");
		}
	}
	
	public void mostrarMiembros(){
		tablaMiembros = new MiembrosTable(nombrePlebiscito.getValue().toString(), nombreTendencia.getValue().toString().split("/")[0]);
		layout.addComponent(tablaMiembros);
		if (tablaMiembros.getMiembrosContainer().size()!= 0){
			tablaMiembros.setVisible(true);
		}
		else{
			tablaMiembros.setVisible(false);
		}
	}
	
}
