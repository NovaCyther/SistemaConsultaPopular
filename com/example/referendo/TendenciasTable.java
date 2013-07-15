package com.example.proyvotaciones;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;

public class TendenciasTable extends Table{
	private String nombrePlebiscito = null;
	private SQLContainer tendenciasContainer = null;
    public DBManager dBManager;
    
    private void initContainers() {
	    try {
	    	JDBCConnectionPool connectionPool = new SimpleJDBCConnectionPool("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "postgres", "1234",2,5);
	        	TableQuery t = new TableQuery("tendencia", connectionPool);
	        	tendenciasContainer = new SQLContainer(t);
	        	tendenciasContainer.addContainerFilter(
	        			new Equal("nombreplebiscito", nombrePlebiscito ));
				this.setContainerDataSource(tendenciasContainer);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public TendenciasTable(String nombrePleb) {
		 nombrePlebiscito = nombrePleb;
	     initContainers();
	     this.setSelectable(true);
	     this.setEditable(false);
	     this.setHeight("200");
	     this.setWidth("500");
	     this.setColumnCollapsingAllowed(true);
	     this.setColumnCollapsed("nombreplebiscito", true);
	     this.setColumnCollapsed("descripcion", true);
	     this.setColumnCollapsed("contacto", true);
	     this.setColumnCollapsed("informacionadicional", true);
	  }

	public SQLContainer getTendenciasContainer() {
		return tendenciasContainer;
	}

	public void setTendenciasContainer(SQLContainer tendenciasContainer) {
		this.tendenciasContainer = tendenciasContainer;
	}
}
