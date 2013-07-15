package com.example.proyvotaciones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.proyvotaciones.NavigatorUI.MainView;
import com.vaadin.client.ui.Field;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;

public class PadronTable extends Table {
		
	private String nombrePlebiscito = null;
	private SQLContainer padronContainer = null;
	private String where;
    private List<OrderBy> orderBys;
    public DBManager dBManager;
    public Set<Object> selectedItemIds;
    public Validador validador;
	
	private void initContainers() {
	    try {
	    	JDBCConnectionPool connectionPool = new SimpleJDBCConnectionPool("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/postgres", "postgres", "1234",2,5);
			/* TableQuery and SQLContainer for personaddress -table */
	    	String query = "SELECT nombreplebiscito,cedula,apellido1,apellido2,nombre FROM Padron";
	    	FreeformQuery q = new FreeformQuery(query, connectionPool, "nombrePlebiscito", "cedula");
	    	where = " WHERE nombreplebiscito = '"+nombrePlebiscito+"'";
	        dBManager = new DBManager(query, where);
	        q.setDelegate(dBManager);
	        padronContainer = new SQLContainer(q);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public PadronTable(String nombrePleb) {
		 validador = new Validador();
		 nombrePlebiscito = nombrePleb;
	     initContainers();
	     this.setSelectable(true);
	     this.setMultiSelect(true);
	     this.setTableFieldFactory(new DefaultFieldFactory() {
	    	 @Override
	    	 public TextField createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				// Create fields by their class
				final TextField field = new TextField((String) propertyId);

				if (propertyId.equals("nombreplebiscito")) {
					field.setReadOnly(true);
				}
				field.setRequired(true);
				field.addValueChangeListener(new Property.ValueChangeListener() {
				    
					@Override
					public void valueChange(
							com.vaadin.data.Property.ValueChangeEvent event) {
						if (validador.verificarTextField(field)){
							field.setComponentError(new UserError("Campo no puede estar nulo"));
						}else{
							field.setComponentError(null);
						}
						
					}
				});
				        
				// Fire value changes immediately when the field loses focus
				field.setImmediate(true);

				// Otherwise use the default field factory
				return field;
			}
		});
	     /* This set contains the ids of the "selected" items */
	     selectedItemIds = new HashSet<Object>();

	     /* This checkbox reflects the contents of the selectedItemIds set */
	     this.addGeneratedColumn("seleccionado", new Table.ColumnGenerator() {
	       @Override
	       public Object generateCell(Table source, final Object itemId, Object columnId) {
	         boolean selected = selectedItemIds.contains(itemId);
	         /* When the chekboc value changes, add/remove the itemId from the selectedItemIds set */
	         final CheckBox cb = new CheckBox("", selected);
	         cb.addValueChangeListener( new ValueChangeListener() {
	           @Override
	           public void valueChange(Property.ValueChangeEvent event) {
	             if(selectedItemIds.contains(itemId)){
	               selectedItemIds.remove(itemId);
	             } else {
	               selectedItemIds.add(itemId);
	             }
	           }
	         });
	         return cb;
	       }
	     });
	    this.setEditable(true);
	     
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
	

	public Set<Object> getSelectedItemIds() {
		return selectedItemIds;
	}

	public void setSelectedItemIds(Set<Object> rowsSelected) {
		this.selectedItemIds = rowsSelected;
	}

	public SQLContainer getPadronContainer() {
		return padronContainer;
	}

	public void setPadronContainer(SQLContainer padronContainer) {
		this.padronContainer = padronContainer;
	}
	
}
