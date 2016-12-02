package org.aksw.wsdmcup;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Tommaso Soru {@literal (tsoru@informatik.uni-leipzig.de)}
 *
 */
public class SQLiteManager {
	
	private Statement statement;
	private Connection connection;
	
	private String dbname;

	public SQLiteManager(String dbname, boolean initialize)
			throws ClassNotFoundException, SQLException {
		
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:"
				+ dbname);
		connection.setAutoCommit(false);
		statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		
		this.dbname = dbname;
		
		if(initialize)
			createTables();
		
	}
	
	public void createTables() throws SQLException {

		statement.executeUpdate("drop table if exists msgraph;");
		statement.executeUpdate("create table msgraph (sub, obj, score INTEGER);");
		connection.commit();
		
	}
	
	private String strip(String s) {
		return "\"" + s.replace("\"", "\\\"") + "\"";
	}

	public void insertTriple(String sub, String obj, String score) throws SQLException {
		
		sub = strip(sub);
		obj = strip(obj);
		
		String q = "insert into msgraph values (" + sub + ", " + obj + ", " + score + ");";
//		System.out.println(q);
		
		statement
				.executeUpdate(q);
		
	}

	public String getAllHashes(String tableName, String id) throws SQLException {
				
		ResultSet rs = statement
				.executeQuery("select * from " + tableName + " where id = '"+id.replaceAll("'", "\\'")+"'");
		while (rs.next()) {
			// read the result set
			rs.getString("p");
		}

		return null;
	}

	public void commit() throws SQLException {
		connection.commit();
	}
	
	public void statementClose() throws SQLException {
		statement.close();
	}

	public void close() throws SQLException {
		connection.close();
		try {
		    new File(dbname).delete();
		} catch (Exception x) {
		    System.err.format("Cannot delete db: "+dbname);
		}
	}

	public String getDbname() {
		return dbname;
	}

	public Integer getScore(String subject, String obj) throws SQLException {
		
		subject = strip(subject);
		obj = strip(obj);
		
		ResultSet rs = statement
				.executeQuery("select score from msgraph where sub = "+subject+" and obj = "+obj);
		if (rs.next()) {
			// read the result set
			return rs.getInt("score");
		}

		return null;
	}

	public Integer getMaxScore(String subject) throws SQLException {

		subject = strip(subject);
		
		ResultSet rs = statement
				.executeQuery("select max(score) as m from msgraph where sub = "+subject);
		if (rs.next()) {
			// read the result set
			return rs.getInt("m");
		}

		return null;
	}

	public Set<String> getObjects(String subject) throws SQLException {
		
		subject = strip(subject);
		
		TreeSet<String> set = new TreeSet<>();
		
		ResultSet rs = statement
				.executeQuery("select obj from msgraph where sub = "+subject);
		while (rs.next()) {
			// read the result set
			set.add(rs.getString("obj"));
		}

		return set;
	}
	
	public SQLObject getEverything(String subject) throws SQLException {
		
		subject = strip(subject);
		
		HashMap<String, Integer> map = new HashMap<>();
		
		ResultSet rs = statement
				.executeQuery("select obj, score from msgraph where sub = "+subject);
		while (rs.next()) {
			// read the result set
			map.put(rs.getString("obj"), rs.getInt("score"));
		}

		return new SQLObject(subject, map);
		
	}

}

class SQLObject {
	
	private String subject;
	private HashMap<String, Integer> objects;
	private Integer max;
	
	public SQLObject(String subject, HashMap<String, Integer> objects) {
		this.subject = subject;
		this.objects = objects;
		
		Integer max = Integer.MIN_VALUE;
		for(Integer i : objects.values())
			if(i > max)
				max = i;
		this.max = max;
	}
	
	public boolean isEmpty() {
		return objects.isEmpty();
	}
	
	public Integer getScore(String object) {
		return objects.get(object);
	}
	
	public Set<String> getObjects() {
		return objects.keySet();
	}

	public String getSubject() {
		return subject;
	}

	public HashMap<String, Integer> getObjectsMap() {
		return objects;
	}

	public Integer getMax() {
		return max;
	}
	
}
