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

@Path("/trenddata")
public class TrendReader {

	@Context
	ResourceConfig context;

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private PreparedStatement preparestatement;

	@GET
    @Path("/gettrendhistory/{region}/{activity}/{isActivity}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<TrendItem> getTrendData(@PathParam("region") String region, @PathParam("activity") String activity, @PathParam("isActivity") boolean isActivity) {

		List<TrendItem> toReturn = new ArrayList<TrendItem>();
		
		String columnType = "";
		String connectionString = "";
		if (isActivity) {
			connectionString = (String) context.getProperty("activityDbCon");
			columnType = "Activity_Type";
		} else {
			connectionString = (String) context.getProperty("placeDbCon");
			columnType = "Place_Type";
		}
		
		connectionString = connectionString.replace("-", "&");

		try {

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = (Connection) DriverManager
					.getConnection(connectionString);

			// Statements allow to issue SQL queries to the database
			statement = (Statement) connect.createStatement();

			// Result set get the result of the SQL query
			String query = "SELECT * FROM r_g_o_ WHERE c_l_m_=? ORDER BY Date DESC Limit 15;";
			query = query.replace("r_g_o_", region);
			query = query.replace("c_l_m_", columnType);

			preparestatement = (PreparedStatement) connect
					.prepareStatement(query);
			preparestatement.setString(1,activity);
			

			resultSet = preparestatement.executeQuery();

			while (resultSet.next()) {
				
				TrendItem itemToAdd = new TrendItem();
				String date = resultSet.getString("Date");
				itemToAdd.setDate(date);
				String trendvalue = resultSet.getString("frequency_foursquare");
				itemToAdd.setTrendValue(trendvalue);
				toReturn.add(itemToAdd);
			}

		} catch (Exception e) {
			System.out.println("Error occured in mysql");
			e.printStackTrace();
		} finally {
			close();
		}

		return toReturn;
	}

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
