package com.example.proyvotaciones;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


import com.vaadin.data.Property;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;



public class MiembrosTable extends Table{
	private String nombrePlebiscito = null;
	private String nombreTendencia = null;
	private SQLContainer miembrosContainer = null;
    public DBManager dBManager;
   
	private void initContainers() {
	    try {
	    	JDBCConnectionPool connectionPool = new SimpleJDBCConnectionPool("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "postgres", "1234",2,5);
	        	TableQuery t = new TableQuery("miembrostendencia", connectionPool);
	        	miembrosContainer = new SQLContainer(t);
	        	miembrosContainer.addContainerFilter(
	        			new And(new Equal("nombreplebiscito", nombrePlebiscito ),new Equal("nombretendencia", nombreTendencia )));
				this.setContainerDataSource(miembrosContainer);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public MiembrosTable(String nombrePleb, String nombreTend) {
		 nombrePlebiscito = nombrePleb;
		 nombreTendencia = nombreTend;
	     initContainers();
	     this.setSelectable(true);
	     this.setEditable(false);
	  }

	public SQLContainer getMiembrosContainer() {
		return miembrosContainer;
	}

	public void setMiembrosContainer(SQLContainer miembrosContainer) {
		this.miembrosContainer = miembrosContainer;
	}
}
