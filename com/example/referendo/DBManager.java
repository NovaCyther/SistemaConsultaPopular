package com.example.proyvotaciones;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
//import com.vaadin.data.util.sqlSimpleDateFormat;

import com.vaadin.addon.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
//import com.vaadin.sass.internal.parser.ParseException;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.postgresql.jdbc3.Jdbc3PoolingDataSource;
import java.sql.DriverManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.util.sqlcontainer.query.generator.filter.AndTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.BetweenTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.CompareTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.FilterTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.IsNullTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.LikeTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.NotTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.OrTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.SimpleStringTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.StringDecorator;
import com.vaadin.data.Container.Filter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.postgresql.jdbc3.Jdbc3PoolingDataSource;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.SQLUtil;

import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;

public class DBManager implements FreeformStatementDelegate{
	Table table;
	JDBCConnectionPool connectionPool;
	SQLContainer containerV;
	SQLContainer containerT;
	SQLContainer containerP;
	Container test;
	String ID;
	Object itemId;
	Item item1;
	FreeformQuery query;
	public ArrayList<Integer> estaVot;
	public ArrayList<String> tendencias;
	public ArrayList<Double> resultados;
	public ArrayList<String> votaciones;
	
	public Connection connection;
	public Jdbc3PoolingDataSource source;
	private List<Filter> filters;
    private List<OrderBy> orderBys;
	private String queryForm;
	private String where;
	private Validador validador;
	
	
	
	public DBManager(){
		votaciones=new ArrayList();
	    try {
	        connectionPool = new SimpleJDBCConnectionPool(
	                "org.postgresql.Driver",
	                "jdbc:postgresql://localhost:5432/postgres", "postgres", "1234", 2, 5);
	        
	    } catch (SQLException e) {
	          e.printStackTrace();
	    }
	}
	
	public void closeC(){
		connectionPool.destroy();
	}
	
	public DBManager(String q, String w){//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Cambiado
		queryForm = q;
		where = w;
		validador = new Validador();
	}
	
	public Connection crearConexion() {
		try {
 
			Class.forName("org.postgresql.Driver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
		}
		connection = null;
		
		try {
 
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/postgres", "postgres",
					"1234");
 
		} catch (SQLException e) {
 
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			
		}
		
		return connection;
	}
	
	public void cerrarConexion() {
		try  
        {  
            if(connection!=null)  
            {  
            	connection.close();
            	source.close();
            }  
        }  
        catch(SQLException e)  
        {  
            System.out.println("Failed closing connection "+e);  
        }  
	}
	
	public void createPool(){
		try {
			source = new Jdbc3PoolingDataSource();
			source.setDataSourceName("A Data Source");
			source.setServerName("localhost");
			source.setDatabaseName("postgres");
			source.setUser("postgres");
			source.setPassword("1234");
			source.setMaxConnections(10);
			new InitialContext().rebind("DataSource", source);
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public Jdbc3PoolingDataSource crearPool(){
		Jdbc3PoolingDataSource pool = null;
		try {
			pool = new Jdbc3PoolingDataSource();
			pool.setDataSourceName("Data Source");
			pool.setServerName("localhost");
			pool.setDatabaseName("postgres");
			pool.setUser("postgres");
			pool.setPassword("1234");
			pool.setMaxConnections(10);
			new InitialContext().rebind("DataSource", source);
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return pool;
	}
	
	public void queryTest(String nombretendencia,String nombreplebiscito,String representante,String descripcion,String pagina,String contacto,String informacionadicional,ArrayList<String> miembros) throws UnsupportedOperationException, SQLException{

		TableQuery tq = new TableQuery("tendencia", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}

        Object itemId2 = containerV.addItem();
        int cedula=Integer.parseInt(representante);
        containerV.getItem(itemId2).getItemProperty("nombretendencia").setValue(nombretendencia);
        containerV.getItem(itemId2).getItemProperty("nombreplebiscito").setValue(nombreplebiscito);
        containerV.getItem(itemId2).getItemProperty("representante").setValue(cedula);
        containerV.getItem(itemId2).getItemProperty("descripcion").setValue(descripcion);
        containerV.getItem(itemId2).getItemProperty("pagina").setValue(pagina);
        containerV.getItem(itemId2).getItemProperty("contacto").setValue(contacto);
        containerV.getItem(itemId2).getItemProperty("informacionadicional").setValue(informacionadicional);

		containerV.commit();

        
		tq = new TableQuery("miembrostendencia", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerP = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  for(int i=0;i<miembros.size();i++){
			  itemId2 = containerP.addItem();
			  containerP.getItem(itemId2).getItemProperty("nombretendencia").setValue(nombretendencia);
			  containerP.getItem(itemId2).getItemProperty("nombreplebiscito").setValue(nombreplebiscito);
			  containerP.getItem(itemId2).getItemProperty("nombremiembro").setValue(miembros.get(i));
		  
		  }

	        containerP.commit();

        
	}
	
	public void agregarPost(String nombretema,String nombreplebiscito,String usuario,String texto){

		TableQuery tq = new TableQuery("post", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  Object itemId2 = containerV.addItem();
	      containerV.getItem(itemId2).getItemProperty("nombretema").setValue(nombretema);
	      containerV.getItem(itemId2).getItemProperty("nombreplebiscito").setValue(nombreplebiscito);
	      containerV.getItem(itemId2).getItemProperty("usuario").setValue(usuario);
	      containerV.getItem(itemId2).getItemProperty("texto").setValue(texto);
	      containerV.getItem(itemId2).getItemProperty("id").setValue(containerV.size()+1);
	      try {
			containerV.commit();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void agregarMiembros(String nombretendencia,String nombreplebiscito,ArrayList<String> miembros,ArrayList<String> miembrosant){
		TableQuery tq = new TableQuery("miembrostendencia", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerP = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  for(int i=0;i<miembrosant.size();i++){
			  deleteMiembrosTendencia(nombreplebiscito,nombretendencia,miembrosant.get(i));
		  }
		  for(int i=0;i<miembros.size();i++){
			  Object itemId2 = containerP.addItem();

			  containerP.getItem(itemId2).getItemProperty("nombretendencia").setValue(nombretendencia);
			  containerP.getItem(itemId2).getItemProperty("nombreplebiscito").setValue(nombreplebiscito);
			  containerP.getItem(itemId2).getItemProperty("nombremiembro").setValue(miembros.get(i));
		  
		  }

	        try {
				containerP.commit();
			} catch (UnsupportedOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	public void editarTendencia(String[] Values) throws Exception{
		String query = "UPDATE tendencia SET"+ 
	            " nombretendencia= '"+Values[0]+"', nombreplebiscito= '"+Values[1]+"', representante= '"+Values[2]+"', descripcion='"+Values[3]+"', pagina= '"+Values[4]+
	            "', contacto= '"+Values[5]+"', informacionadicional = '"+Values[6]+"'"+
			            " WHERE nombreplebiscito = '"+Values[1]+"'"+"and nombretendencia ='"+Values[0]+"'";
		PreparedStatement ps = null;
		try {
			crearConexion();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(query);
			ps.execute();
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
			throw new Exception(
					"Error occured while loading data from file to database."
							+ e.getMessage() + ((SQLException) e).getNextException());
		} finally {
			if (null != ps)
				ps.close();
			if (null != connection)
				connection.close();	
		}
		
	}
	
	public void borrarTendencia(String nombreTendencia, String nombrePlebiscito){
		String query = "DELETE FROM tendencia WHERE nombretendencia = '"+nombreTendencia+"' and nombreplebiscito ='"+nombrePlebiscito+"'";
		PreparedStatement ps = null;
		try {
			crearConexion();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(query);
			ps.execute();
			connection.commit();
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			try {
				throw new Exception(
						"Error occured while loading data from file to database."
								+ e.getMessage() + ((SQLException) e).getNextException());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			if (null != ps)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		}
		
	}
	
	public void getProcesosInscribirTendencia(){//NOPE
		TableQuery tq = new TableQuery("tendencia", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerP = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  

		//  try {
			//convertirAFechaPopUpField(SelectValuePlebiscito("inicioinscripciontendencias", item1.getItemProperty("nombreplebiscito").getValue().toString())).before(new Date());
	//	} catch (Exception e1) {
			// TODO Auto-generated catch block
		//	e1.printStackTrace();
	//	} 
	        for(int i=0;i<containerP.size();i++){
	        	itemId = containerP.getIdByIndex(i);
	            item1=containerP.getItem(itemId);
	            if(!votaciones.contains(item1.getItemProperty("nombreplebiscito").getValue().toString())){
	            	votaciones.add(item1.getItemProperty("nombreplebiscito").getValue().toString());
	            }
	        }
	        try {
				containerP.commit();
			} catch (UnsupportedOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	public void getVotos(String procesoVotacion){
		estaVot=new ArrayList();
		tendencias=new ArrayList();
		resultados=new ArrayList();
		query = new FreeformQuery("select votos FROM resultados", connectionPool);
        try {
			containerV = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        
		query = new FreeformQuery("select nombretendencia FROM resultados", connectionPool);
        try {
			containerT = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        
		query = new FreeformQuery("select nombreplebiscito FROM resultados", connectionPool);
        try {
			containerP = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        
        for(int i=0;i<containerP.size();i++){
        	itemId = containerP.getIdByIndex(i);
            item1=containerP.getItem(itemId);
        	if(item1.getItemProperty("nombreplebiscito").getValue().toString().equals(procesoVotacion)){
        		estaVot.add(i);
   
        	}
        }
        
        for(int i=0;i<estaVot.size();i++){
        	itemId = containerT.getIdByIndex(estaVot.get(i));
            item1=containerT.getItem(itemId);
        	tendencias.add(item1.getItemProperty("nombretendencia").getValue().toString());
        	
        	itemId = containerV.getIdByIndex(estaVot.get(i));
            item1=containerV.getItem(itemId);
            resultados.add((double)Integer.parseInt(item1.getItemProperty("votos").getValue().toString()));
        }
        
        
    }
	
	public void procesosVotacion(){
		votaciones=new ArrayList();
		query = new FreeformQuery("select nombreplebiscito FROM plebiscito", connectionPool);
        try {
			containerV = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        for(int i=0;i<containerV.size();i++){
        	itemId = containerV.getIdByIndex(i);
            item1=containerV.getItem(itemId);
            if(!votaciones.contains(item1.getItemProperty("nombreplebiscito").getValue().toString())){
            	votaciones.add(item1.getItemProperty("nombreplebiscito").getValue().toString());
            }
        }
	}
	
	
	
	public void getTendencias(String votacion){
		tendencias=new ArrayList();
		query = new FreeformQuery("select nombretendencia FROM tendencia where nombreplebiscito ='"+votacion+"'", connectionPool);
        try {
			containerV = new SQLContainer(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        for(int i=0;i<containerV.size();i++){
        	itemId = containerV.getIdByIndex(i);
            item1=containerV.getItem(itemId);
            tendencias.add(item1.getItemProperty("nombretendencia").getValue().toString());
        }
	}
	
	
	
	public void votar(String procesoVotacion,String tendencia,String cedula){
		TableQuery tq = new TableQuery("votacion", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		  Object itemId2 = containerV.addItem();
	      containerV.getItem(itemId2).getItemProperty("nombreplebiscito").setValue(procesoVotacion);
	      containerV.getItem(itemId2).getItemProperty("nombretendencia").setValue(tendencia);
	      containerV.getItem(itemId2).getItemProperty("cedula").setValue(Integer.parseInt(cedula));

	      try {
			containerV.commit();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean yaVoto(String procesoVotacion,String cedula){
		boolean yaVoto=false;
		TableQuery tq = new TableQuery("votacion", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        for(int i=0;i<containerV.size();i++){
        	itemId = containerV.getIdByIndex(i);
            item1=containerV.getItem(itemId);
            if(item1.getItemProperty("nombreplebiscito").getValue().toString().equals(procesoVotacion)&&item1.getItemProperty("cedula").getValue().toString().equals(cedula)){
            	yaVoto=true;
            }
        }
		
		return yaVoto;
	}
	
	public boolean estaEnpadronado(String procesoVotacion,String cedula){
		boolean enpadronado=false;
		TableQuery tq = new TableQuery("padron", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        for(int i=0;i<containerV.size();i++){
        	itemId = containerV.getIdByIndex(i);
            item1=containerV.getItem(itemId);
            if(item1.getItemProperty("nombreplebiscito").getValue().toString().equals(procesoVotacion)&&item1.getItemProperty("cedula").getValue().toString().equals(cedula)){
            	enpadronado=true;
            }
        }
		return enpadronado;
	}
	
	public ArrayList<String> infoTendencias(String nomTendencia,String nomVot){
		ArrayList<String> info=new ArrayList<String>();
		int posTendencia;
		TableQuery tq = new TableQuery("tendencia", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        for(int i=0;i<containerV.size();i++){
        	itemId = containerV.getIdByIndex(i);
            item1=containerV.getItem(itemId);
        	if(item1.getItemProperty("nombreplebiscito").getValue().toString().equals(nomVot)&&item1.getItemProperty("nombretendencia").getValue().toString().equals(nomTendencia)){

        		info.add(containerV.getItem(itemId).getItemProperty("nombretendencia").getValue().toString());
        		info.add(containerV.getItem(itemId).getItemProperty("nombreplebiscito").getValue().toString());
        		info.add(containerV.getItem(itemId).getItemProperty("representante").getValue().toString());
        		info.add(containerV.getItem(itemId).getItemProperty("descripcion").getValue().toString());
        		if (containerV.getItem(itemId).getItemProperty("pagina").getValue()!= null){
        			info.add(containerV.getItem(itemId).getItemProperty("pagina").getValue().toString());
        		}
        		else{
        			info.add("");
        		}
        		if (containerV.getItem(itemId).getItemProperty("contacto").getValue()!= null){
        			info.add(containerV.getItem(itemId).getItemProperty("contacto").getValue().toString());
        		}else{
        			info.add("");
        		}
        		if (containerV.getItem(itemId).getItemProperty("informacionadicional").getValue()!= null){
        			info.add(containerV.getItem(itemId).getItemProperty("informacionadicional").getValue().toString());
        		}else{
        			info.add("");
        		}
        	}
        }
		return info;
	}
	
	public String getTipoProceso(String nombre){
		String tipo="";
		TableQuery tq = new TableQuery("plebiscito", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerP = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	      for(int i=0;i<containerP.size();i++){
	        	itemId = containerP.getIdByIndex(i);
	            item1=containerP.getItem(itemId);
	            if(item1.getItemProperty("nombreplebiscito").getValue().toString().equals(nombre)){

	            	tipo=item1.getItemProperty("tipo").getValue().toString();
	            }
	        }
		return tipo;
	}
	
	public int getCantTendencias(String nombre){
		int cantot=0;
		TableQuery tq = new TableQuery("tendencia", connectionPool);
        try {
			containerV = new SQLContainer(tq);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		 for(int i=0;i<containerV.size();i++){
	        	itemId = containerV.getIdByIndex(i);
	            item1=containerV.getItem(itemId);
	        	if(item1.getItemProperty("nombreplebiscito").getValue().toString().equals(nombre)){
	        		cantot++;
	        	}
	        }
		return cantot;
	}

	public StatementHelper getQueryStatement(int offset, int limit)
            throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer(queryForm + where);

        System.out.println(query);
        query.append(getOrderByString());
        if (offset != 0 || limit != 0) {
            query.append(" LIMIT ").append(limit);
            query.append(" OFFSET ").append(offset);
        }
        sh.setQueryString(query.toString());
        return sh;
    }
	

    private String getOrderByString() {
        StringBuffer orderBuffer = new StringBuffer("");
        if (orderBys != null && !orderBys.isEmpty()) {
            orderBuffer.append(" ORDER BY ");
            OrderBy lastOrderBy = orderBys.get(orderBys.size() - 1);
            for (OrderBy orderBy : orderBys) {
                orderBuffer.append(SQLUtil.escapeSQL(orderBy.getColumn()));
                if (orderBy.isAscending()) {
                    orderBuffer.append(" ASC");
                } else {
                    orderBuffer.append(" DESC");
                }
                if (orderBy != lastOrderBy) {
                    orderBuffer.append(", ");
                }
            }
        }
        return orderBuffer.toString();
    }
	
    public void setFilters(List<Filter> filters)
            throws UnsupportedOperationException {
        this.filters = filters;
    }

    public void setOrderBy(List<OrderBy> orderBys)
            throws UnsupportedOperationException {
        this.orderBys = orderBys;
    }

    public int storeRow(Connection conn, RowItem row) throws SQLException {///modificado
        PreparedStatement statement = null;
        int retval = 0;
        System.out.println(row.getItemPropertyIds().toArray()[1]);
        if (validador.verificarFormatoCedula(row.getItemProperty("cedula") .getValue().toString())){
        	try {
				throw new UnsupportedOperationException("El formato de la cedula "+row.getItemProperty("cedula") .getValue().toString()+" es invalido");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
	        if (row.getId() instanceof TemporaryRowId) {
	            statement = conn
	                    .prepareStatement("INSERT INTO padron( nombreplebiscito, cedula, nombre, apellido1, apellido2) VALUES(?, ?, ?, ?, ?)");
	            setRowValues(statement, row, false);
	        } else {
	            statement = conn
	                    .prepareStatement("UPDATE Padron SET nombrePlebiscito = ?, cedula = ?, Apellido1 = ?, Apellido2 = ?, Nombre = ? WHERE nombrePlebiscito = ? AND cedula = ?");
	            setRowValues(statement, row, true);
	            
	        }
	        System.out.println(statement);
	        
	        
	        retval = statement.executeUpdate();
	        statement.close();
        }
        return retval;
    }
    
    private void setRowValues(PreparedStatement statement, RowItem row, boolean hayQueActualizar) //!!!!!!!!!!!!Cambiado
            throws SQLException {
    
    	statement.setString(1, (String) row.getItemProperty("nombreplebiscito")
                 .getValue());
    	statement.setInt(2, Integer.parseInt(row.getItemProperty("cedula") 
                .getValue().toString() ) );
        statement.setString(3, (String) row.getItemProperty("apellido1")
                .getValue());
        statement.setString(4, (String) row.getItemProperty("apellido2")
                .getValue());
        statement.setString(5, (String) row.getItemProperty("nombre")
                .getValue());
        if (hayQueActualizar){
	        statement.setString(6, (String) row.getItemProperty("nombreplebiscito")
	                .getValue());
	        statement.setInt(7, Integer.parseInt(row.getItemProperty("cedula") 
	                .getValue().toString() ) );
        }
    }

    public boolean removeRow(Connection conn, RowItem row)
            throws UnsupportedOperationException, SQLException {
        PreparedStatement statement = conn
                .prepareStatement("DELETE FROM Padron WHERE nombrePlebiscito = ? and cedula = ?");
        statement.setString(1, (String) row.getItemProperty("nombreplebiscito").getValue());
        statement.setInt(2, (Integer) row.getItemProperty("cedula").getValue());
        int rowsChanged = statement.executeUpdate();
        statement.close();
        return rowsChanged == 1;
    }
    
    public StatementHelper getContainsRowQueryStatement(Object... keys)
            throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer(
                "SELECT * FROM Padron WHERE nombrePlebiscito = ? and Cedula = ?");
        sh.addParameterValue(keys[0]);
        sh.addParameterValue(keys[1]);
        sh.setQueryString(query.toString());
        return sh;
    }


	@Override
	@Deprecated
	public String getQueryString(int offset, int limit)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@Deprecated
	public String getCountQuery() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@Deprecated
	public String getContainsRowQueryString(Object... keys)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public StatementHelper getCountStatement()
			throws UnsupportedOperationException {
		StatementHelper result = new StatementHelper();
        StringBuffer query = new StringBuffer("SELECT COUNT(*) FROM Padron"+where);

        result.setQueryString(query.toString());
        System.out.println(query);
        return result;
	}
	
	public void insertData( String tableName, String columnNames,  //////!!!!!!!!!Cambiado
		String[] Values) throws Exception{
		String query = "INSERT INTO plebiscito("+ 
	            "nombreplebiscito, organizador, descripcion, comunidad, tipo, "+
	            "estilo, inicioinscripciontendencias, inicioperiododiscucion, "+
	            "inicioperiodovotacion, fininscripciontendencias, finperiododiscusion, "+
	            "finperiodovotacion)"+
	    " VALUES ( '" +Values[0]+"', '" + Values[1] + "', '"+Values[2]+"', '" +Values[3]+"', '"+Values[4]+"', '"+ 
	    		Values[5]+ "'";
		for (int i = 6; i < 12; ++i){
			if ( Values[i].equals("") ){
				query +=  ", null";
			}
			else{
				query += ", '"+Values[i]+"' ";
			}
		}
		query += ")";
		
		System.out.println(query);
		PreparedStatement ps = null;
		try {
			crearConexion();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(query);
			System.out.println(ps);
			ps.execute();
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
			throw new Exception(
					"Error occured while loading data from file to database."
							+ e.getMessage() + ((SQLException) e).getNextException());
		} finally {
			if (null != ps)
				ps.close();
			if (null != connection)
				connection.close();	
		}
		
	}
	
	
	public void updateData( String tableName, String columnNames, //////!!!!!!!!!Cambiado
			String[] Values) throws Exception{
			for (int i = 6; i < 12; ++i){
				if ( Values[i].equals("") ){
					Values[i] =  " null";
				}
				else{
					Values[i] = " '"+Values[i]+"' ";
				}
			}
			String query = "UPDATE plebiscito SET"+ 
		            " nombreplebiscito= '"+Values[0]+"', organizador= '"+Values[1]+"', descripcion= '"+Values[2]+"', comunidad='"+Values[3]+"', tipo= '"+Values[4]+
		            "', estilo= '"+Values[5]+"', inicioinscripciontendencias = "+Values[6]+", inicioperiododiscucion = "+Values[7]+
		            ", inicioperiodovotacion = "+Values[8]+", fininscripciontendencias= "+Values[9]+", finperiododiscusion= "+Values[10]+
		            ", finperiodovotacion= "+Values[11]+" "+
		            " WHERE nombreplebiscito = '"+Values[0]+"'";
			System.out.println(query);
			PreparedStatement ps = null;
			try {
				crearConexion();
				connection.setAutoCommit(false);
				ps = connection.prepareStatement(query);
				System.out.println(ps);
				ps.execute();
				connection.commit();
			} catch (Exception e) {
				connection.rollback();
				e.printStackTrace();
				throw new Exception(
						"Error occured while loading data from file to database."
								+ e.getMessage() + ((SQLException) e).getNextException());
			} finally {
				if (null != ps)
					ps.close();
				if (null != connection)
					connection.close();	
			}
			
		}
	
	public PreparedStatement agregarDato(PreparedStatement ps, int index, String string, String type){
		try{
			if ( type.equals("int") ){
				ps.setInt(index, Integer.parseInt(string));
			}
			else{
				if ( type.equals("String") ) {
					ps.setString(index, string);
				}
				else{
					if ( type.equals("float") ){
						ps.setFloat(index, Float.parseFloat(string));
					}
					else{
						if ( type.equals("double") ){
							ps.setFloat(index, Float.parseFloat(string));
						}
						else{
							if ( type.equals("boolean") ){
								ps.setBoolean(index, Boolean.parseBoolean(string));
							}else{
								if (type.equals("Date")){
									ps.setDate(index, convertirAFechaSQL(string));
								}
								else{
									System.out.println("Tipo no reconocido");
								}	
							}
						}
					}
				}	
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return ps;
	}
	
	@SuppressWarnings("deprecation")
	public Date convertirAFechaSQL(String string) throws ParseException{ //cambiado!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		System.out.println(string);
		if (string != null){
			SimpleDateFormat parserSDF=new SimpleDateFormat("dow mon dd hh:mm:ss zzz yyyy");
			java.util.Date dateStr = parserSDF.parse(string);
	        @SuppressWarnings("deprecation")
			java.sql.Date d = new java.sql.Date(dateStr.getDay(),dateStr.getMonth(),dateStr.getYear() );
	        
	        System.out.println("Dia: "+string.substring(5,6));
	        d.setMonth(Integer.parseInt(string.substring(5,6)));
	        d.setDate(Integer.parseInt(string.substring(8,9)));
	        d.setYear(Integer.parseInt(string.substring(0,3)));
	        return d;
		}
		else{
			return null;
		}
		
	}
	
	public Date convertirAFechaPopUpField(String string) throws ParseException{  //cambiado!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		System.out.println(string);
		if (string != null){
	        System.out.println("Dia: "+string.substring(5,7));
	        System.out.println("Dia: "+string.substring(8,10));
	        System.out.println("Dia: "+string.substring(0,4));
	        @SuppressWarnings("deprecation")
	        java.sql.Date d = new java.sql.Date(Integer.parseInt(string.substring(0,4)) - 1900, Integer.parseInt(string.substring(5,7)) - 1, Integer.parseInt(string.substring(8,10)) );
	        return d;
		}
		else{
			return null;
		}
		
	}
	
	public boolean existeSelectCampoRequerido(String value) throws Exception{
		String query = "Select * FROM plebiscito WHERE nombre = '"+value+"'";
		System.out.println(query);
		Statement stmt = null;
		boolean yaExiste = false;
		System.out.println("Entreasdfafsa");
		try {
			crearConexion();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("rs");
			rs.next();
			
			System.out.println("rs nulo");
			if (rs != null){
				System.out.println("rs no nulo");
				if (rs.getString("nombre").equals(value)){
					yaExiste = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(
					"Error occured while loading data from file to database."
							+ e.getMessage() + ((SQLException) e).getNextException());
		} finally {
			if (null != connection)
				connection.close();	
		}
		return yaExiste;
	}
	
	public String SelectValuePlebiscito(String columnName, String value) throws Exception{
		String query = "Select "+columnName+" FROM plebiscito"+" WHERE nombreplebiscito = '"+value+"'";
		System.out.println(query);
		Statement stmt = null;
		String result = "";
		try {
			crearConexion();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			result = rs.getString(columnName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(
					"Error occured while loading data from file to database."
							+ e.getMessage() + ((SQLException) e).getNextException());
		} finally {
			if (null != connection)
				connection.close();	
		}
		return result;
	}
	
	public void borrarFilaTabla( String tableName, String pK) throws Exception{
			String query = "DELETE FROM plebiscito WHERE nombreplebiscito = '"+pK+"'";
			System.out.println(query);
			PreparedStatement ps = null;
			try {
				crearConexion();
				connection.setAutoCommit(false);
				ps = connection.prepareStatement(query);
				System.out.println(ps);
				ps.execute();
				connection.commit();
			} catch (Exception e) {
				connection.rollback();
				e.printStackTrace();
				throw new Exception(
						"Error occured while loading data from file to database."
								+ e.getMessage() + ((SQLException) e).getNextException());
			} finally {
				if (null != ps)
					ps.close();
				if (null != connection)
					connection.close();	
			}
			
		}

	
	public ArrayList<String> getTitulos(String nomVot){
		ArrayList<String> titulos = new ArrayList();
		int posTendencia;
		TableQuery tq = new TableQuery("post", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        for(int i=0;i<containerV.size();i++){
        	itemId = containerV.getIdByIndex(i);
            item1=containerV.getItem(itemId);
        	if(item1.getItemProperty("nombreplebiscito").getValue().toString().equals(nomVot)){

        		titulos.add(containerV.getItem(itemId).getItemProperty("nombretema").getValue().toString());
        	}
        }
		
		return titulos;
	}
	
	public ArrayList<String> getTexto(String nomVot,String nomPlb){
		ArrayList<String> txto = new ArrayList();
		int posTendencia;
		TableQuery tq = new TableQuery("post", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        for(int i=0;i<containerV.size();i++){
        	itemId = containerV.getIdByIndex(i);
            item1=containerV.getItem(itemId);
            
        	if(item1.getItemProperty("nombreplebiscito").getValue().toString().equals(nomPlb)&&item1.getItemProperty("nombretema").getValue().toString().equals(nomVot)){
        		txto.add(containerV.getItem(itemId).getItemProperty("usuario").getValue().toString());
        		txto.add(containerV.getItem(itemId).getItemProperty("texto").getValue().toString());
        	}
        }
		
		return txto;
	}
	
	public ArrayList<String> getMiembrosTendencia(String nomVot,String nomTend){
		ArrayList<String> nombres = new ArrayList();
		int posTendencia;
		TableQuery tq = new TableQuery("miembrostendencia", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        for(int i=0;i<containerV.size();i++){
        	itemId = containerV.getIdByIndex(i);
            item1=containerV.getItem(itemId);
        	if(item1.getItemProperty("nombreplebiscito").getValue().toString().equals(nomVot)&&item1.getItemProperty("nombretendencia").getValue().toString().equals(nomTend)){
        		nombres.add(containerV.getItem(itemId).getItemProperty("nombremiembro").getValue().toString());
        	}
        }
       
		return nombres;
	}
	
	private void deleteMiembrosTendencia(String nomVot,String nomTend,String nombre){
		String query = "DELETE FROM miembrostendencia WHERE nombretendencia = '"+nomTend+"' and nombreplebiscito = '"+nomVot+"' and nombremiembro ='"+nombre+"'";
		PreparedStatement ps = null;
		try {
			crearConexion();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(query);
			ps.execute();
			connection.commit();
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			try {
				throw new Exception(
						"Error occured while loading data from file to database."
								+ e.getMessage() + ((SQLException) e).getNextException());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			if (null != ps)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		}
	}
	
	public byte[] getLLavePublica(String cedpersona){
		byte[] llavePublica=new byte[1024];
		String query = "Select llavepublica FROM firmadigital WHERE persona = '"+cedpersona+"'";

		Statement stmt = null;
		try {
			crearConexion();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			
			if (rs != null){
				llavePublica=rs.getBytes(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				throw new Exception(
						"Error occured while loading data from file to database."
								+ e.getMessage() + ((SQLException) e).getNextException());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		}
		
		return llavePublica;
		
	}
	
	
	public void addLLavePublica(byte[] llavePublica,String cedpersona) throws UnsupportedOperationException, SQLException{

		TableQuery tq = new TableQuery("firmadigital", connectionPool);
		tq.setVersionColumn("OPTLOCK");
		  try {
			  containerV = new SQLContainer(tq);
			} catch (SQLException e) {
				e.printStackTrace();
			}

        Object itemId2 = containerV.addItem();
        int cedula=Integer.parseInt(cedpersona);
        containerV.getItem(itemId2).getItemProperty("llavepublica").setValue(llavePublica);
        containerV.getItem(itemId2).getItemProperty("persona").setValue(cedula);


		containerV.commit();
	}
	
	public void addImage(byte[] imagen,String nombrePlebiscito,String nombreTendencia) throws Exception{
		String query = "UPDATE tendencia SET"+ 
	            " image= '"+imagen+
			            " WHERE nombreplebiscito = '"+nombrePlebiscito+"'"+"and nombretendencia ='"+nombreTendencia+"'";
		PreparedStatement ps = null;
		try {
			crearConexion();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(query);
			ps.execute();
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
			throw new Exception(
					"Error occured while loading data from file to database."
							+ e.getMessage() + ((SQLException) e).getNextException());
		} finally {
			if (null != ps)
				ps.close();
			if (null != connection)
				connection.close();	
		}
	}

	public byte[] getImagen(String nomVot,String nomTend){
		byte[] imagen=new byte[1024];
		
		String query = "Select imagen FROM tendencia WHERE nombretendencia = '"+nomTend+"' and nombreplebiscito = '"+nomVot+"'";

		Statement stmt = null;
		try {
			crearConexion();
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			
			if (rs != null){
				imagen=rs.getBytes(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				throw new Exception(
						"Error occured while loading data from file to database."
								+ e.getMessage() + ((SQLException) e).getNextException());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			if (null != connection)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		}
		return imagen;
	}
	
	public void addImagePlebiscito(String filepath,String nombrePlebiscito) throws Exception{
		String query = "UPDATE plebiscito SET "+ 
	            " imagen= '"+filepath+"'"
			           + " WHERE nombreplebiscito = '"+nombrePlebiscito+"'";
		PreparedStatement ps = null;
		try {
			crearConexion();
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(query);
			ps.execute();
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
			throw new Exception(
					"Error occured while loading data from file to database."
							+ e.getMessage() + ((SQLException) e).getNextException());
		} finally {
			if (null != ps)
				ps.close();
			if (null != connection)
				connection.close();	
		}
	}
}
