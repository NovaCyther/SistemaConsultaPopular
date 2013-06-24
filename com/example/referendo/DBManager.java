package com.example.referendo;

import java.sql.DriverManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.AndTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.BetweenTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.CompareTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.FilterTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.IsNullTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.LikeTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.NotTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.OrTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.SimpleStringTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.StringDecorator;
import com.vaadin.data.Container.Filter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.postgresql.jdbc3.Jdbc3PoolingDataSource;

import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
 
public class DBManager implements FreeformStatementDelegate{
	
	public Connection connection;
	public Jdbc3PoolingDataSource source;
	private List<Filter> filters;
    private List<OrderBy> orderBys;
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
	      "Cedula", "NombrPlebiscito", "Apellido1", "Apellido2", "Nombre"};
	private static final String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
	private static final String TABLE_REGEX = "\\$\\{table\\}";
	private static final String KEYS_REGEX = "\\$\\{keys\\}";
	private static final String VALUES_REGEX = "\\$\\{values\\}";
 
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
					"jdbc:postgresql://127.0.0.1:5432/ReferendoDB", "postgres",
					"password");
 
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
			source.setDatabaseName("ReferendoDB");
			source.setUser("postgres");
			source.setPassword("password");
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
			pool.setDatabaseName("ReferendoDB");
			pool.setUser("postgres");
			pool.setPassword("password");
			pool.setMaxConnections(10);
			new InitialContext().rebind("DataSource", source);
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return pool;
	}
	
	
	public StatementHelper getQueryStatement(int offset, int limit)
            throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer("SELECT * FROM Padron ");
        if (filters != null) {
            query.append(FilterToWhereTranslator.getWhereStringForFilters(
                    filters, sh));
        }
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

    public int storeRow(Connection conn, RowItem row) throws SQLException {
        PreparedStatement statement = null;
        System.out.println(row.getItemPropertyIds().toArray()[1]);
        if (row.getId() instanceof TemporaryRowId) {
            statement = conn
                    .prepareStatement("INSERT INTO Padron VALUES(?, ?, ?, ?, ?)");
            setRowValues(statement, row);
        } else {
            statement = conn
                    .prepareStatement("UPDATE Padron SET Cedula = ?, nombrePlebiscito = ?, Apellido1 = ?, Apellido2 = ?, Nombre = ? WHERE Cedula = ? AND nombrePlebiscito = ?");
            setRowValues(statement, row);
            System.out.println(statement);
        }

        int retval = statement.executeUpdate();
        statement.close();
        return retval;
    }
    
    private void setRowValues(PreparedStatement statement, RowItem row)
            throws SQLException {
    
        statement.setInt(1, Integer.parseInt(row.getItemProperty("cedula") 
                .getValue().toString() ) );
        statement.setString(2, (String) row.getItemProperty("nombreplebiscito")
                .getValue());
        statement.setString(3, (String) row.getItemProperty("apellido1")
                .getValue());
        statement.setString(4, (String) row.getItemProperty("apellido2")
                .getValue());
        statement.setString(5, (String) row.getItemProperty("nombre")
                .getValue());
        statement.setInt(6, Integer.parseInt(row.getItemProperty("cedula") 
                .getValue().toString() ) );
        statement.setString(7, (String) row.getItemProperty("nombreplebiscito")
                .getValue());
    }

    public boolean removeRow(Connection conn, RowItem row)
            throws UnsupportedOperationException, SQLException {
        PreparedStatement statement = conn
                .prepareStatement("DELETE FROM Padron WHERE ID = ?");
        statement.setInt(1, (Integer) row.getItemProperty("ID").getValue());
        int rowsChanged = statement.executeUpdate();
        statement.close();
        return rowsChanged == 1;
    }
    
    public StatementHelper getContainsRowQueryStatement(Object... keys)
            throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer(
                "SELECT * FROM Padron WHERE Cedula = ? and nombrePlebiscito = ?");
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
        StringBuffer query = new StringBuffer("SELECT COUNT(*) FROM Padron");
        if (filters != null) {
            query.append(QueryBuilder.getWhereStringForFilters(filters, result));
        }
        result.setQueryString(query.toString());
        System.out.println(query);
        return result;
	}
	
	public void insertData( String tableName, String columnNames,
		String[] Values) throws Exception{
		String query = "INSERT INTO plebiscito("+ 
	            "nombreplebiscito, organizador, descripcion, comunidad, tipo, "+
	            "estilo, inicioinscripciontendencias, inicioperiododiscucion, "+
	            "inicioperiodovotacion, fininscripciontendencias, finperiododiscusion, "+
	            "finperiodovotacion)"+
	    " VALUES ( '" +Values[0]+"', '" + Values[1] + "', '"+Values[2]+"', '" +Values[3]+"', '"+Values[4]+"', '"+ 
	    		Values[5]+ "', '" +Values[6]+"', '" +Values[7]+"', '" +
	    		Values[8]+"', '" +Values[9]+"', '" +Values[10]+"', '" +
	    		Values[11]+"')";
		
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
	
	
	public void updateData( String tableName, String columnNames,
			String[] Values) throws Exception{
			String query = "UPDATE plebiscito SET"+ 
		            "nombreplebiscito= '"+Values[0]+"', organizador= '"+Values[1]+"', descripcion= '"+Values[2]+"', comunidad='"+Values[3]+"', tipo= '"+Values[4]+
		            "', estilo= '"+Values[5]+"', inicioinscripciontendencias = '"+Values[6]+"', inicioperiododiscucion = '"+Values[7]+
		            "', inicioperiodovotacion = '"+Values[8]+"', fininscripciontendencias= '"+Values[9]+"', finperiododiscusion= '"+Values[10]+
		            "', finperiodovotacion= '"+Values[11]+"')"+
		            "WHERE nombre = '"+Values[0]+"'";
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
	
	public Date convertirAFechaSQL(String string) throws ParseException{
		System.out.println(string);
		if (string != null){
			SimpleDateFormat parserSDF=new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
			java.util.Date dateStr = parserSDF.parse(string);
	        @SuppressWarnings("deprecation")
			java.sql.Date d = new java.sql.Date(dateStr.getDay(),dateStr.getMonth(),dateStr.getYear() );
	        return d;
		}
		else{
			return null;
		}
		
	}
	
	public Date convertirAFechaPopUpField(String string) throws ParseException{
		System.out.println(string);
		if (string != null){
			SimpleDateFormat parserSDF=new SimpleDateFormat("yyyy-mm-dd");
			java.util.Date dateStr = parserSDF.parse(string);
	        @SuppressWarnings("deprecation")
			java.sql.Date d = new java.sql.Date(dateStr.getDay(),dateStr.getMonth(),dateStr.getYear() );
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
}
