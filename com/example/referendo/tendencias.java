package com.example.proyvotaciones;


import java.sql.SQLException;
import java.util.ArrayList;
import java.io.*;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.Table;

public class tendencias {

	public String nombre="";
	public String procesoVotacionPertenece="";
	//public String[] representante=new String[2];
	public String representante="";
	public String descripcion="";
	public String pagina="";
	public String contacto="";
	public String infoAd="";
	public ArrayList<String> miembros;
	public ArrayList<String> miembrosant;
	public DBManager manejoDB;
	
	private int errorID=0;
	
	private File file = new File("file.txt");
	private BufferedReader reader = null;
	private ArrayList<String> list = new ArrayList<String>();
	
	
	public void saveToDB() throws UnsupportedOperationException, SQLException{
		manejoDB=new DBManager();
		manejoDB.queryTest(nombre,procesoVotacionPertenece,representante,descripcion,pagina,contacto,infoAd,miembros);
	}
	
	public void editToDB() throws UnsupportedOperationException, SQLException{
		manejoDB=new DBManager();
		String[] values = new String[7];
		values[0]=nombre;
		values[1]=procesoVotacionPertenece;
		values[2]=representante;
		values[3]=descripcion;
		values[4]=pagina;
		values[5]=contacto;
		values[6]=infoAd;

		try {
			manejoDB.editarTendencia(values);
			manejoDB.agregarMiembros(nombre, procesoVotacionPertenece, miembros,miembrosant);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void borrarDeDB(String nombreVotacion,String nombreTendencia){
		manejoDB=new DBManager();
		manejoDB.borrarTendencia(nombreTendencia, nombreVotacion);
	}
	
	public void getFromDB(String nombre,String procesoVotacion){
		
	}
	

	
	public boolean verifiData(){
		boolean verificacion=true;
		if(nombre==""||nombre==null){
			verificacion=false;
			errorID=1;
		}
		
		if(procesoVotacionPertenece==""||procesoVotacionPertenece==null){//proceso de votacion viene de combobox, deberia ya estar en la DB
			verificacion=false;
			errorID=2;
		}
		
		if(representante==""){
			verificacion=false;
			errorID=3;
		}
		
		if(representante==""){
			verificacion=false;
			errorID=4;
		}
		

		
		if(contacto==""||contacto==null){
			verificacion=false;
			errorID=6;
		}
		

		
		return verificacion;
	}
	
	
	private boolean checkCedula(String cedula){
		boolean verificacion=true;
		if(cedula.length()!=9){
			verificacion=false;
		}
		
		//check  cedula vr nombre
	
		return verificacion;
	}
	
	public String dataError(){
		String error="";
		switch(errorID){
		case 1:{
			error="falta nombre";
			break;
			}
		case 2:{
			error="proceso de votacion no existe";
			break;
			}
		case 3:{
			error="falta nombre";
			break;
			}
		case 4:{
			error="error con cedula";
			break;
			}
		case 5:{
			error="error con cedula";
			break;
			}
		case 6:{
			error="falta contacto";
			break;
			}
		case 7:{
			error="error con cedula";
			break;
			}
		default:break;
		
		}
		errorID=0;
		return error;
	}
	
	
	
	
	public void uploadFromFile(String filename){
		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    while ((text = reader.readLine()) != null) {
		        list.add(text);
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
	}
	
}