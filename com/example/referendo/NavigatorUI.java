package com.example.proyvotaciones;


import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

//@Theme("referendotheme")
//@Theme("proyvotacionestheme")
public class NavigatorUI extends UI {
    Navigator  navigator;

	public Panel panel;
	public EditarPlebiscito panelEditarPlebiscito;
	public inscribirTendencia panelInscribirTendencia;
	public ConsultarTendencia panelVerTendencias;
	public grafico result;
	public foro discusion;
	public votaciones votacion;
	public InscribirPlebiscito panelInscribirPlebiscito;
	public DBManager dBmanager;
	public CSVLoader csvLoader;
	public editarTendencia panelEditarTendencia;
	public ConsultarProceso panelConsultarProceso;
	
	
    protected static final String MAINVIEW = "main";
    
    /** A start view for navigating to the main view */
    public class StartView extends VerticalLayout implements View  {
        public StartView() {
            setSizeFull();
            
            Button button = new Button("Go to Main View",
                    new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    navigator.navigateTo(MAINVIEW);
                }
            });
            addComponent(button);
            setComponentAlignment(button, Alignment.MIDDLE_CENTER);
        }        
        
        @Override
        public void enter(ViewChangeEvent event) {
            Notification.show("Sistema de referendos en línea");
        }
    }

    /** Main view with a menu */
    public class MainView extends VerticalLayout implements View {

        // Menu navigation button listener
        class ButtonListener implements Button.ClickListener {
            String menuitem;
            public ButtonListener(String menuitem) {
                this.menuitem = menuitem;
            }

            @Override
            public void buttonClick(ClickEvent event) {
                // Navigate to a specific state
                navigator.navigateTo(MAINVIEW + "/" + menuitem);
            }
        }

        public MainView() {
        	
            VerticalLayout root = new VerticalLayout();
            root.addStyleName("proyvotacionestheme");
           // root.setMargin(true);
            root.setSizeFull();
            
         // Title bar
            HorizontalLayout titleBar = new HorizontalLayout();
            titleBar.setWidth("100%");
           // titleBar.setHeight("25%");
            root.addComponent(titleBar);

            Label title = new Label("Sistema de Referendos en linea");
            title.addStyleName("title");
            titleBar.addComponent(title);
            Label titleComment = new Label("for Vaadin");
            titleComment.addStyleName("titlecomment");
            titleComment.setSizeUndefined();
            titleBar.addComponent(titleComment);
            titleBar.setExpandRatio(title, 1.0f); // Minimize the comment
            
            // Layout with menu on left and view area on right
            HorizontalLayout hLayout = new HorizontalLayout();
            hLayout.setSizeFull();
            hLayout.setSpacing(true);
            //add layout
            root.addComponent(hLayout);
            root.setExpandRatio(hLayout, 1);
            // Have a menu on the left side of the screen
            Panel menu = new Panel("Opciones");
            menu.addStyleName("menucontainer");
            menu.addStyleName("light");
            menu.setHeight("100%");
            menu.setWidth("-1px");

            VerticalLayout menuContent = new VerticalLayout();
            menuContent.addComponent(new Button("Consultar Proceso",
                      new ButtonListener("Consultar Proceso")));
            menuContent.addComponent(new Button("Inscribir Plebiscito",
                      new ButtonListener("Inscribir Plebiscito")));
            menuContent.addComponent(new Button("Editar Plebiscito",      
                      new ButtonListener("Editar Plebiscito")));
            menuContent.addComponent(new Button("Inscribir Tendencias",
                      new ButtonListener("Inscribir Tendencias")));
            menuContent.addComponent(new Button("Editar Tendencias",
                      new ButtonListener("Editar Tendencias")));
            menuContent.addComponent(new Button("Consultar Tendencias",
                    new ButtonListener("Consultar Tendencias")));
            menuContent.addComponent(new Button("Ver Resultados",
                    new ButtonListener("Ver Resultados")));
            menuContent.addComponent(new Button("Ver foro",
                    new ButtonListener("Ver foro")));
            menuContent.addComponent(new Button("Votar",
                    new ButtonListener("Votar")));
            menuContent.setWidth(null);
            menuContent.setMargin(true);
            menu.setContent(menuContent);
            hLayout.addComponent(menu);

            // A panel that contains a content area on right
            panel = new Panel("Sistema Edecisiones");
            panel.setSizeFull();
            hLayout.addComponent(panel);
            hLayout.setExpandRatio(panel, 1.0f);

            addComponent(hLayout);
            setExpandRatio(hLayout, 1.0f);
            
            // Allow going back to the start
            Button logout = new Button("Logout",
                       new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    navigator.navigateTo("");
                }
            });
            addComponent(logout);
        }        
        
        @Override
        public void enter(ViewChangeEvent event) {
            VerticalLayout panelContent = new VerticalLayout();
            panelContent.setSizeFull();
            panelContent.setMargin(true);
            panel.setContent(panelContent); // Also clears

            if (event.getParameters() == null
                || event.getParameters().isEmpty()) {
                panelContent.addComponent(
                    new Label("Descripción sistema Edecisiones: ")
                    		);
                panelContent.addComponent(new Label ("El sistema Edecisiones está orientado a incentivar y facilitar los procesos de la" +
                		"\n democracia participativa en una organización o comunidad."));
                return;
            }
            
            
            if (event.getParameters().equals("Consultar Proceso") ) {
            	panelConsultarProceso = new ConsultarProceso(this);
                panel.setContent(panelConsultarProceso);
            }
            
            if (event.getParameters().equals("Editar Plebiscito") ) {
            	panelEditarPlebiscito = new EditarPlebiscito(this);
                panel.setContent(panelEditarPlebiscito);
            }

            if (event.getParameters().equals("Inscribir Plebiscito") ) {
            	panelInscribirPlebiscito = new InscribirPlebiscito(this);
                panel.setContent(panelInscribirPlebiscito);
            }
            if (event.getParameters().equals("Editar Tendencias") ) {
            	panelEditarTendencia = new editarTendencia();
                panel.setContent(panelEditarTendencia);
                
            }
            
            if (event.getParameters().equals("Inscribir Tendencias") ) {
            	panelInscribirTendencia = new inscribirTendencia();
                panel.setContent(panelInscribirTendencia);
                
            }
            
            if (event.getParameters().equals("Consultar Tendencias") ) {
            	panelVerTendencias = new ConsultarTendencia(this);
                panel.setContent(panelVerTendencias);
            }
            
            if (event.getParameters().equals("Ver Resultados") ) {
            	result = new grafico();
                panel.setContent(result);
            }
        
            if (event.getParameters().equals("Ver foro") ) {
            	discusion = new foro();
                panel.setContent(discusion);
            }
            if (event.getParameters().equals("Votar") ) {
            	votacion = new votaciones();
            	panel.setContent(votacion);
            	
            	
        		//firmaDigital firma = new firmaDigital();
        		//String probando ="esto es una prueba, espero que funcione";
        		//System.out.println(firma.firmar("111111111",probando.getBytes()));
        		//firma.generarKey("111111111");
        		//firma.generarKey("100000000");
        		//firma.generarKey("114350464");
        		//firma.generarKey("114220164");
        		//firma.testKeys("114350464");
            }
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle("Sistema de referendos en línea");
        
        // Create a navigator to control the views
        navigator = new Navigator(this, this);
        
        // Create and register the views
        navigator.addView("", new StartView());
        navigator.addView(MAINVIEW, new MainView());
    }
}