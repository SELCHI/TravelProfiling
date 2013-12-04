package com.selchi;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import com.sun.jersey.api.core.ResourceConfig;

@Path("/foursquaredata")
public class FourSquareDataReader {

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private PreparedStatement preparestatement;
	
	@Context
	ResourceConfig context;
	
	
	@GET
    @Path("/toplocations")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<FourSquareDataItem> getFourSquareTopLocs(){
		
		String connectionString = (String) context.getProperty("dbConnection");
		connectionString = connectionString.replace("-","&");
		
		List<FourSquareDataItem> toReturn = new ArrayList<FourSquareDataItem>();
		
		try {

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = (Connection) DriverManager
					.getConnection(connectionString);

			// Statements allow to issue SQL queries to the database
			statement = (Statement) connect.createStatement();

			// Result set get the result of the SQL query
			resultSet = statement
					.executeQuery("SELECT * FROM `fsqsearch` ORDER BY checkins_count DESC LIMIT 7");

			while (resultSet.next()) {
				// It is possible to get the columns via name
				// also possible to get the columns via the column number
				// which starts at 1
				// e.g. resultSet.getSTring(2);
				FourSquareDataItem toAdd = new FourSquareDataItem();
				String name = resultSet.getString(5);
				toAdd.setName(name);
				String region = resultSet.getString(4);
				toAdd.setRegion(region);
				String ontotag = resultSet.getString(6);
				toAdd.setOntotag(ontotag);
				int checkins = resultSet.getInt(11);
				toAdd.setCheckins(checkins);				
				
				toReturn.add(toAdd);

			}

		} catch (Exception e) {
			System.out.println("Error occured in mysql");
			e.printStackTrace();
		} finally {
			close();
		}
		
		return toReturn;
		
	}
	
	@GET
    @Path("/insideitems/{region}/{ontotag}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public FourSquareBigData getInsideItems(@PathParam("region") String region,@PathParam("ontotag") String ontotag){
		
		String connectionString = (String) context.getProperty("dbConnection");
		connectionString = connectionString.replace("-","&");
		FourSquareBigData toReturn = new FourSquareBigData();
		
		try {

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = (Connection) DriverManager
					.getConnection(connectionString);

			// Statements allow to issue SQL queries to the database
			statement = (Statement) connect.createStatement();

			// Result set get the result of the SQL query
			String query = "SELECT * FROM  `fsqsearch` WHERE  region=? and onto_tag=? ORDER BY checkins_count DESC";
			
			preparestatement = (PreparedStatement) connect.prepareStatement(query);
			preparestatement.setString(1, region);
			preparestatement.setString(2, ontotag.replace("_", " "));
			
			resultSet = preparestatement.executeQuery();
			int others = 0;
			int size = 5;
			List<FourSquareDataItem> topFive = new ArrayList<FourSquareDataItem>();
			
			while (resultSet.next()) {
				// It is possible to get the columns via name
				// also possible to get the columns via the column number
				// which starts at 1
				// e.g. resultSet.getSTring(2);
				
				if(size >0){
					
					FourSquareDataItem toAdd = new FourSquareDataItem();
					String name = resultSet.getString(5);
					toAdd.setName(name);
					int checkins = resultSet.getInt(11);
					toAdd.setCheckins(checkins);	
					
					topFive.add(toAdd);
				}else{
					others+=resultSet.getInt(11);
				}
				
				size--;
			}
			
			toReturn.setOthers(others);
			toReturn.setTopFive(topFive);

		} catch (Exception e) {
			System.out.println("Error occured in mysql");
			e.printStackTrace();
		} finally {
			close();
		}
		
		
		return toReturn;
	}
	

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

}
