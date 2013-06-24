package com.example.referendo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.referendo.NavigatorUI.MainView;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.SQLUtil;

public class PadronTable extends Table  {
		
	private String nombrePlebiscito = null;
	private SQLContainer padronContainer = null;
	private List<Filter> filters;
    private List<OrderBy> orderBys;
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
	      "Cedula", "NombrPlebiscito", "Apellido1", "Apellido2", "Nombre"};
	
	private void initContainers() {
	    try {
	    	JDBCConnectionPool connectionPool = new SimpleJDBCConnectionPool("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/ReferendoDB", "postgres", "password",2,5);
			/* TableQuery and SQLContainer for personaddress -table */
	        FreeformQuery q = new FreeformQuery("SELECT cedula,nombrePlebiscito,apellido1,apellido2,nombre FROM Padron WHERE nombrePlebiscito = '"+nombrePlebiscito+"'", connectionPool,"Cedula" , "nombrePlebiscito");
	        List<String> field = new ArrayList<>();
	        field.add("Cedula");
	        field.add("nombrePlebiscito");
	        field.add("apellido1");
	        field.add("apellido2");
	        field.add("nombre");
	        q.setDelegate(new DBManager());
	        padronContainer = new SQLContainer(q);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public PadronTable(String nombrePleb) {
		 nombrePlebiscito = nombrePleb;
	     initContainers();
	     this.setSelectable(true);
	     this.setEditable(true);
	     this.setMultiSelect(true);
	     
	     setContainerDataSource(padronContainer);
	  }
	
	@Override
	public void commit() throws SourceException {
	  /* Commit the data entered in the person form to the actual item. */
	  super.commit();

	  /* Commit changes to the database. */
	  try {
		  padronContainer.commit();
	  } catch (UnsupportedOperationException e) {
	      e.printStackTrace();
	  } catch (SQLException e) {
	      e.printStackTrace();
	  }
	  setReadOnly(true);
	}

	@Override
	public void discard() throws SourceException {
	  super.discard();
	  /* On discard, roll back the changes. */
	  try {
		  padronContainer.rollback();
	  } catch (UnsupportedOperationException e) {
	      e.printStackTrace();
	  } catch (SQLException e) {
	      e.printStackTrace();
	  }
	  /* Clear the form */
	  //setItemDataSource(null);
	  setContainerDataSource(null);
	  setReadOnly(true);
	}
	
}
