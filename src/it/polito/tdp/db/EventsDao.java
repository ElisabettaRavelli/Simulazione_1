package it.polito.tdp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.model.Anno;
import it.polito.tdp.model.Distanze;
import it.polito.tdp.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Anno> getAnni(){
		String sql="SELECT distinct YEAR(reported_date) AS anno " + 
				"FROM events ";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Anno> anni = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				anni.add(new Anno(Year.of(res.getInt("anno"))));
			}
			conn.close();
			return anni;
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Integer> getDistretti(){
		String sql = "SELECT distinct district_id " + 
				"FROM EVENTS ";
		try {
		Connection conn = DBConnect.getConnection() ;

		PreparedStatement st = conn.prepareStatement(sql) ;
		
		List<Integer> distretti = new ArrayList<>() ;
		
		ResultSet res = st.executeQuery() ;
		
		while(res.next()) {
			distretti.add(res.getInt("district_id"));
		}
		conn.close();
		return distretti;
		
		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public Distanze getDistanze(Year anno, Integer distretto){
		String sql = "SELECT AVG(geo_lat) AS lat, AVG(geo_lon) AS lon " + 
				"FROM EVENTS " + 
				"WHERE YEAR(reported_date) = ? " + 
				"AND district_id = ? ";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			st.setInt(2, distretto);
		
			ResultSet res = st.executeQuery() ;
			Distanze centro = null;
			if(res.next()) {
				centro = new Distanze(res.getDouble("lat"), res.getDouble("lon"));
			}
			conn.close();
			return centro;
			
			
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null ;
			}
	}

}
