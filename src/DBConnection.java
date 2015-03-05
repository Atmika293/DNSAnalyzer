import java.sql.*;
import java.util.Properties;



public class DBConnection {
	
	private Connection connect;
	private Properties connProperties;
	private String username;
	private String password;
	private String dbms;
	private String dbname;
	private String location;
	
	DBConnection(String usnm,String pswd,String dbms,String location,String dbname){
		this.connect  = null;
		this.username = usnm;
		this.password = pswd;
		this.dbms = dbms;
		this.location = location;
		this.dbname = dbname;
	}
	
	DBConnection(String dbms,String location,String dbname){
		this.connect  = null;
		this.username = "Atmika";
		this.password = "";
		this.dbms = dbms;
		this.location = location;
		this.dbname = dbname;
	}
	
	public String getDBName(){
		return this.dbname;
	}
	
	public Connection getConnect(){
		return this.connect;
	}
	
	public void setProperties(){
		connProperties.setProperty("Username", this.username);
		connProperties.setProperty("Password", this.password);
		connProperties.setProperty("DBMS", this.dbms);
		connProperties.setProperty("DBName",this.dbname);
	}
	
	public Connection getConnection(){
		connect = null;
		connProperties = new Properties();
		this.setProperties();
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connect = DriverManager.getConnection("jdbc:" + this.dbms + ":" +this.location+this.dbname +";create=true",this.username,this.password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}
	
	public void createSchema(String schemaname) throws SQLException{
		Statement stmt = null;
	    try {
	        stmt = this.connect.createStatement();
	        stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS "+schemaname);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        if (stmt != null) { stmt.close(); }
	    }
	}
	
	public void dropTable(String tableName) throws SQLException{
		Statement stmt = null;
	    try {
	        stmt = this.connect.createStatement();
	        stmt.executeUpdate("DROP TABLE IF EXISTS "+this.dbname+"."+tableName);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        if (stmt != null) { stmt.close(); }
	    }
	}
	public void createTable(String createString) throws SQLException {

	    Statement stmt = null;
	    try {
	        stmt = this.connect.createStatement();
	        stmt.executeUpdate(createString);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        if (stmt != null) { stmt.close(); }
	    }
	}
	
	public void populateTable(String populateTable,Object...parameters) throws SQLException {

	    PreparedStatement pstmt = null;
	    try {
	        pstmt = this.connect.prepareStatement(populateTable);
	        for(int i = 1;i <= parameters.length;i++)
	        	pstmt.setObject(i, parameters[i-1]);
	        pstmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        if (pstmt != null) { pstmt.close(); }
	    }
	}
	
	public ResultSet retrieveResultSet(String retrieve) throws SQLException {

	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        pstmt = this.connect.prepareStatement(retrieve);
	       // for(int i = 1;i <= parameters.length;i++)
	        //	pstmt.setObject(i, parameters[i-1]);
	        rs = pstmt.executeQuery();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        if (pstmt != null) { pstmt.close(); }
	    }
	    return rs;
	}
}
