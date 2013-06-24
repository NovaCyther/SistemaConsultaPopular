package com.example.referendo;

import java.util.Date;

import com.vaadin.client.ui.dd.DDUtil;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class Validador {
	
	DBManager dBManager;
	
	public Validador(){
		DBManager dBManager = new DBManager();
	}
	
	public boolean verificarTextField(TextField textField){
		boolean hayError = false;
		try{
			textField.validate();
			textField.setComponentError(null);
		}
		catch(Exception e){
			textField.setValidationVisible(true);
			textField.setComponentError(new UserError("Campo Requerido"));
			hayError = true;
		}
		return hayError; 
	}
	
	public boolean verificarCombobox(ComboBox comboBox){
		boolean hayError = false;
		try{
			comboBox.validate();
			comboBox.setComponentError(null);
		}
		catch(Exception e){
			comboBox.setValidationVisible(true);
			comboBox.setComponentError(new UserError("Campo Requerido"));
			hayError = true;
		}
		return hayError; 
	}
	
	public boolean verificarTextArea(TextArea textArea){
		boolean hayError = false;
		try{
			textArea.validate();
			textArea.setComponentError(null);
		}
		catch(Exception e){
			textArea.setValidationVisible(true);
			textArea.setComponentError(new UserError("Campo Requerido"));
			hayError = true;
		}
		return hayError; 
	}
	
	public boolean verificarFormatoCedula(String cedula){
		boolean hayError = false;
		if ( cedula.length() != 9 || cedula.startsWith("0") ){
			hayError = true;
		}
		return hayError;
	}
	
	public boolean verificarCampoCedula(TextField textField){
		boolean hayError = false;
		if ( verificarFormatoCedula(textField.getValue() ) ){
			textField.setValidationVisible(true);
			textField.setComponentError(new UserError("Debe ser un número de 9 digitos y el primero diferente de 0."));
			hayError = true;
		}
		else{
			textField.setComponentError(null);
		}
		return hayError;
	}
	
	public boolean verificarFechas(PopupDateField inicio, PopupDateField fin){
		boolean hayError = false;
		try{
			System.out.println(fin.getValue());
			if (fin.getValue() != null && inicio.getValue() != null ){
				if ( inicio.getValue().after( fin.getValue() ) || inicio.getValue().equals(fin.getValue() ) ){
					inicio.setComponentError(new UserError("La fecha de inicio debe ser anterior a la fecha final."));
					fin.setComponentError(new UserError("La fecha final debe ser posterior a la fecha inicial."));
					hayError = true;
				}
				if ( inicio.getValue().before(new Date()) ) {
					inicio.setComponentError(new UserError("La fecha de inicio debe ser posterior a la fecha actual."));
					hayError = true;
				}
			}else{
				if (fin.getValue()  != null && inicio.getValue() == null ){
					inicio.setComponentError(new UserError("No hay fecha de inicio definida"));
					hayError = true;
				}
				else{
					if (fin.getValue() == null &&  inicio.getValue()  != null ){
						inicio.setComponentError(new UserError("No hay fecha final definida"));
						hayError = true;
					}
				}
			}
			
			
			if (!hayError){
				inicio.setComponentError(null);
				fin.setComponentError(null);
			}
		}
		catch(Exception e){
			inicio.setValidationVisible(true);
			fin.setValidationVisible(true);
			hayError = true;
		}
		return hayError;
	}
	
	
	public boolean verficarNombreUnico(TextField textField){
		boolean hayError = false;
		try {
			if (textField.getValue().isEmpty()){
				hayError = true;
			}
			else{
				if (dBManager.existeSelectCampoRequerido(textField.getValue().toString())){
					hayError = true;
					textField.setValidationVisible(true);
					textField.setComponentError(new UserError("Ya existe este plebiscito. Por favor elegir otro nombre"));
				}
				else{
					
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hayError = true;
		}
		return hayError;
	}

}
