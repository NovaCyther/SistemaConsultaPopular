package com.example.referendo;

import com.example.referendo.NavigatorUI.MainView;
import com.vaadin.data.util.MethodProperty;
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
    Label  explanation; // A descriptive text
    PadronTable tabla;

    public EditarPadron(MainView main, String nombrePlebiscito) {
        mainwindow = main;
        panel = new Panel();
       
        final FormLayout layout = new FormLayout();
		layout.setMargin(true);
		panel.setContent(layout);
        layout.addStyleName("outlined");
        layout.setSpacing(true);
        
        tabla = new PadronTable(nombrePlebiscito);
        //tabla.addItem();
        //tabla.addContainerProperty("", CheckBox.class, "");
        CheckBox editableCheckBox = new CheckBox("Editable",
                new MethodProperty<Boolean>(tabla, "editable"));
        //("Seleccionar", CheckBox.class, false);
        
        layout.addComponent(tabla);
        
        
        
        borrarLineas = new Button("Borrar lineas seleccionadas");
        borrarLineas.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            }
        });
    
        guardarCambios = new Button("Guardar cambios"); // A button in the window
        guardarCambios.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	tabla.commit();
            }
        });
        
        final HorizontalLayout botonera = new HorizontalLayout(
        		borrarLineas, guardarCambios);
        layout.addComponent(botonera);
        
        /* Create a new window. */
        mywindow = new Window("Editar Padron", panel);
        mywindow.setPositionX(200);
        mywindow.setPositionY(100);

        /* Add the window inside the main window. */
        mainwindow.getUI().addWindow(mywindow);
    
    }
}
