package com.example.proyvotaciones;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

public class CSVLoader {

	private static final String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
	private static final String TABLE_REGEX = "\\$\\{table\\}";
	private static final String KEYS_REGEX = "\\$\\{keys\\}";
	private static final String VALUES_REGEX = "\\$\\{values\\}";

	private Connection connection;
	private char seprator;

	/**
	 * Public constructor to build CSVLoader object with Connection details. The
	 * connection is closed on success or failure.
	 * 
	 * @param connection
	 */
	public CSVLoader(Connection connection) {
		this.connection = connection;
		// Set default separator
		this.seprator = ',';
	}

	/**
	 * Parse CSV file using OpenCSV library and load in given database table.
	 * 
	 * @param csvFile
	 *            Input CSV file
	 * @param tableName
	 *            Database table name to import data
	 * @param columnNames
	 *            Names of the columns
	 * @param truncateBeforeLoad
	 *            Truncate the table before inserting new records.
	 * @throws Exception
	 */
	public void loadCSV(FileReader fileReader, String tableName, String columnNames,
			String[]columnTypes, String pK, boolean truncateBeforeLoad) throws Exception {

		CSVReader csvReader = null;
		if (null == this.connection) {
			throw new Exception("Not a valid connection.");
		}
		try {

			csvReader = new CSVReader(fileReader, this.seprator);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error occured while executing file. "
					+ e.getMessage());
		}

		String[] headerRow = csvReader.readNext();

		if (null == headerRow) {
			csvReader.close();
			throw new FileNotFoundException(
					"No data defined in given CSV file."
							+ "Please check the CSV file format.");
		}

		String questionmarks = StringUtils.repeat("?,", headerRow.length + 1);
		questionmarks = (String) questionmarks.subSequence(0,
				questionmarks.length() - 1);

		String query = SQL_INSERT.replaceFirst(TABLE_REGEX, tableName);
		query = query.replaceFirst(KEYS_REGEX, columnNames);
		query = query.replaceFirst(VALUES_REGEX, questionmarks);

		System.out.println("Query: " + query);

		String[] nextLine;
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = this.connection;
			con.setAutoCommit(false);
			ps = con.prepareStatement(query);

			if (truncateBeforeLoad) {
				// delete data from table before loading csv
				System.out.println("DELETE FROM " + tableName + " WHERE nombreplebiscito = '"+pK+"'");
				con.createStatement().execute("DELETE FROM " + tableName + " WHERE nombreplebiscito = '"+pK+"'");
			}

			final int batchSize = 1000;
			int count = 0;
			String type;
			while ((nextLine = csvReader.readNext()) != null) {
				if (null != nextLine) {
					int index = 1;
					for (String string : nextLine) {
						type = columnTypes[ (index - 1) % columnTypes.length];
						ps = agregarDato(ps, index++, string, type);
						if (index  % columnTypes.length == 0 && index != 1){
							ps = agregarDato(ps,index++,pK,type);
							System.out.println(pK);
						}
						System.out.println(type);
						System.out.println(index);
						System.out.println(ps);
					}
					ps.addBatch();
				}
				if (++count % batchSize == 0) {
					ps.executeBatch();
				}
			}
			ps.executeBatch(); // insert remaining records
			con.commit();
		} catch (Exception e) {
			con.rollback();
			e.printStackTrace();
			throw new Exception(
					"Error occured while loading data from file to database."
							+ e.getMessage() + ((SQLException) e).getNextException());
		} finally {
			if (null != ps)
				ps.close();
			if (null != con)
				con.close();

			csvReader.close();
		}
	}

	public char getSeprator() {
		return seprator;
	}

	public void setSeprator(char seprator) {
		this.seprator = seprator;
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
								System.out.println("Tipo no reconocido");
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

	
}
