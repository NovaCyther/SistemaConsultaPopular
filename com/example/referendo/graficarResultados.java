package com.example.proyvotaciones;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.ArrayList;

public class graficarResultados {
	
	public String nombreVotacion;
	public ArrayList<String> tendencias;
	public ArrayList<Double> resultados;
	public ArrayList<Double> porcentajes;
	public int cantTendencias=5;
	public DBManager manejoDB;
	
	
	
	
	public void graficarResultados(){
		manejoDB=new DBManager();
		manejoDB.getVotos(nombreVotacion);
		tendencias=manejoDB.tendencias;
		resultados=manejoDB.resultados;
		cantTendencias=tendencias.size();
		refresh();
	}
	
	public void showGrafico(String nombreVotacion){
		this.nombreVotacion=nombreVotacion;
		
	}
	


	
	public void refresh(){

		
		porcentajes=new ArrayList();
		double totVotos=0;
		double mayor =0;
		int indiceMayor=0;
		String temp1;
		double temp2=0;
		double percnt=0;

		for(int i=0;i<cantTendencias;i++){
			if(resultados.get(i)>mayor){
				mayor=resultados.get(i);
				indiceMayor=i;
			}
			totVotos=totVotos+resultados.get(i);
		}
		for(int i=0;i<cantTendencias;i++){
			System.out.println(resultados.get(i));
			percnt=(resultados.get(i)*100.0)/totVotos;
			percnt= roundTwoDecimals(percnt);
			porcentajes.add(percnt);
			
		}
		temp2=porcentajes.get(0);
		temp1=tendencias.get(0);
		porcentajes.set(0,porcentajes.get(indiceMayor));
		tendencias.set(0,tendencias.get(indiceMayor));
		porcentajes.set(indiceMayor,temp2);
		tendencias.set(indiceMayor,temp1);
	}
	
	public double roundTwoDecimals(double d) {
	    DecimalFormat twoDForm = new DecimalFormat("#.00");
	    return Double.valueOf(twoDForm.format(d));
	}
	

}