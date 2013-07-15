package com.example.proyvotaciones;

import java.util.Iterator;

import com.example.proyvotaciones.NavigatorUI.MainView;
import com.vaadin.data.Item;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class EditarPadron {

	MainView mainwindow;  // Reference to main window
    Window mywindow;    // The window to be opened
    Panel panel;
    Button borrarLineas;  // Button for opening the window
    Button guardarCambios; // A button in the window
    Button agregarEntrada;
    Label  explanation; // A descriptive text
    PadronTable tabla;
    String nombrePlebiscito;

    public EditarPadron(MainView main, String nombrePleb) {
        mainwindow = main;
        panel = new Panel();
        nombrePlebiscito = nombrePleb;
       
        final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		panel.setContent(layout);
        layout.addStyleName("outlined");
        layout.setSpacing(true);
        tabla = new PadronTable(nombrePlebiscito);
        layout.addComponent(tabla);
       

        
        agregarEntrada = new Button("Agregar una nueva Linea");
        agregarEntrada.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	crearNuevaFila();
            }
        });
        
        borrarLineas = new Button("Borrar lineas seleccionadas");
        borrarLineas.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	for ( Object itemId : tabla.getSelectedItemIds() ) {
                    tabla.removeItem(itemId);
                }
            }
        });
    
        guardarCambios = new Button("Guardar cambios"); // A button in the window
        guardarCambios.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	try{
	            	tabla.commit();
	            	//guardarCambios.setComponentError(null);
            	}
            	catch(UnsupportedOperationException E){
            		guardarCambios.setComponentError(new UserError(E.getLocalizedMessage()));
            	}
            }
        });
        
        final HorizontalLayout botonera = new HorizontalLayout(
        		borrarLineas, agregarEntrada, guardarCambios);
        layout.addComponent(botonera);
        
        /* Create a new window. */
        mywindow = new Window("Editar Padron", panel);
        mywindow.setPositionX(200);
        mywindow.setPositionY(100);

        /* Add the window inside the main window. */
        mainwindow.getUI().addWindow(mywindow);
    }
    
    public void crearNuevaFila(){
    	 Object id = tabla.addItem();
         tabla.getItem(id).getItemProperty("nombreplebiscito").setValue(nombrePlebiscito);
         tabla.getItem(id).getItemProperty("cedula").setValue(1000000000);
         tabla.getItem(id).getItemProperty("apellido1").setValue("Apellido 1");
         tabla.getItem(id).getItemProperty("apellido2").setValue("Apellido 2");
         tabla.getItem(id).getItemProperty("nombre").setValue("Nombre");
    }
}
