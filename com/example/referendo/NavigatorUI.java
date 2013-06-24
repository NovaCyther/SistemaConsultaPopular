package com.example.referendo;


import java.io.OutputStream;
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

@Theme("referendotheme")
public class NavigatorUI extends UI {
    Navigator  navigator;

	public Panel panel;
	public EditarPlebiscito panelEditarPlebiscito;
	public InscribirPlebiscito panelInscribirPlebiscito;
	public DBManager dBmanager;
	public CSVLoader csvLoader;
    
    protected static final String MAINVIEW = "main";
    
    /** A start view for navigating to the main view */
    public class StartView extends VerticalLayout implements View {
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
            root.addStyleName("navigator");
            root.setMargin(true);
            root.setSizeFull();
            
         // Title bar
            HorizontalLayout titleBar = new HorizontalLayout();
            titleBar.setWidth("100%");
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
            //hLayout.setSizeFull();
            
            //add layout
            root.addComponent(hLayout);
            
            // Have a menu on the left side of the screen
            Panel menu = new Panel("Opciones");
            menu.setHeight("100%");
            menu.setWidth(null);
            VerticalLayout menuContent = new VerticalLayout();
            menuContent.addComponent(new Button("Consultar Plebiscito",
                      new ButtonListener("Consultar Plebiscito")));
            menuContent.addComponent(new Button("Inscribir Plebiscito",
                      new ButtonListener("Inscribir Plebiscito")));
            menuContent.addComponent(new Button("Editar Plebiscito",      
                      new ButtonListener("Editar Plebiscito")));
            menuContent.addComponent(new Button("Inscribir Tendencias",
                      new ButtonListener("Inscribir Tendencias")));
            menuContent.addComponent(new Button("Editar Tendencias",
                      new ButtonListener("Editar Tendencias")));
            menuContent.setWidth(null);
            menuContent.setMargin(true);
            menu.setContent(menuContent);
            hLayout.addComponent(menu);

            // A panel that contains a content area on right
            panel = new Panel("Titulo Opcion");
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
                    new Label("Nothing to see here, " +
                              "just pass along."));
                return;
            }
            
            if (event.getParameters().equals("Editar Plebiscito") ) {
            	panelEditarPlebiscito = new EditarPlebiscito(this);
                panel.setContent(panelEditarPlebiscito);
            }

            if (event.getParameters().equals("Inscribir Plebiscito") ) {
            	panelInscribirPlebiscito = new InscribirPlebiscito(this);
                panel.setContent(panelInscribirPlebiscito);
            }
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle("Navigation Example");
        
        // Create a navigator to control the views
        navigator = new Navigator(this, this);
        
        // Create and register the views
        navigator.addView("", new StartView());
        navigator.addView(MAINVIEW, new MainView());
    }

	public DBManager getdBmanager() {
		return dBmanager;
	}

	public CSVLoader getCsvLoader() {
		return csvLoader;
	}

	public void setdBmanager(DBManager dBmanager) {
		this.dBmanager = dBmanager;
	}

	public void setCsvLoader(CSVLoader csvLoader) {
		this.csvLoader = csvLoader;
	}
}