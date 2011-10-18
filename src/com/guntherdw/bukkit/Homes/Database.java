package com.guntherdw.bukkit.Homes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {
	private String db, user, pass, dbhost;
	public static final String GET_HOME = "SELECT * FROM savehomes WHERE name = ? AND description LIKE ? LIMIT 1";
	public static final String GET_HOMES = "SELECT * FROM savehomes WHERE name = ?";
	public static final String SAVE_HOME = "REPLACE INTO savehomes (name, world, x, y, z, rotX, rotY, description) VALUES (?,?,?,?,?,?,?,?) ";
	public static final String DELETE_HOME = "DELETE FROM savehomes WHERE name = ? AND description = ? LIMIT 1";
	public static final String GET_ACTIVE = "SELECT * FROM homes WHERE name = ? LIMIT 1";
	public static final String SET_ACTIVE = "REPLACE INTO homes (name, world, x, y, z, rotX, rotY, `group`) VALUES (?,?,?,?,?,?,?,?) ";
	
	public Database(Homes plugin) {
		this.dbhost = plugin.getConfiguration().getString("dbhost");
        this.db =  plugin.getConfiguration().getString("database");
        this.user = plugin.getConfiguration().getString("username");
        this.pass = plugin.getConfiguration().getString("password");
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://"+dbhost+":3306/" + db + "?autoReconnect=true&user=" + user + "&password=" + pass);
	}
	
	
	public Home getHome(String name, String playername) {
		Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(GET_HOME);
            st.setString(1, playername);
            st.setString(2, name);
            rs = st.executeQuery();
            if(rs.next()) {
            	Home home = new Home(rs.getString(9), rs.getString(2), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6), rs.getFloat(7), rs.getFloat(8), rs.getString(3));
            	home.setId(rs.getInt(1));
            	return home;
            }
            
        } catch(Exception e) {
        	Homes.log.warning("[Homes] Error getting home " + name + " of player " + playername + " :");
            e.printStackTrace();
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return null;
	}
	
	public List<Home> getHomes(String playername) {
		Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Home> homes = new ArrayList<Home>();
        try {
            conn = getConnection();
            st = conn.prepareStatement(GET_HOMES);
            st.setString(1, playername);
            rs = st.executeQuery();
            while(rs.next()) {
            	Home home = new Home(rs.getString(9), rs.getString(2), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6), rs.getFloat(7), rs.getFloat(8), rs.getString(3));
            	home.setId(rs.getInt(1));
            	homes.add(home);
            }
        } catch(Exception e) {
        	Homes.log.warning("[Homes] Error getting homes of player " + playername + " :");
            e.printStackTrace();
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return homes;
	}
	
	public boolean save(Home home) {
		Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(SAVE_HOME, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, home.getPlayername());
            st.setString(2, home.getWorld());
            st.setDouble(3, home.getX());
            st.setDouble(4, home.getY());
            st.setDouble(5, home.getZ());
            st.setFloat(6, home.getYaw());
            st.setFloat(7, home.getPitch());
            st.setString(8, home.getName());
            st.execute();
            rs = st.getGeneratedKeys();
            if(rs.next()) {
            	home.setId(rs.getInt(1));
            }
        } catch(Exception e) {
        	Homes.log.warning("[Homes] Error saving " + home.toString() + " :");
            e.printStackTrace();
            return false;
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return true;
	}
	
	public boolean delete(Home home) {
		Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(DELETE_HOME);
            st.setString(1, home.getPlayername());
            st.setString(2, home.getName());
            st.execute();
        } catch(Exception e) {
        	Homes.log.warning("[Homes] Error deleting " + home.toString() + " :");
            e.printStackTrace();
            return false;
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return true;
	}
	
	public Home getActiveHome(String playername) {
		Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(GET_ACTIVE);
            st.setString(1, playername);
            rs = st.executeQuery();
            if(rs.next()) {
            	Home home = new Home(rs.getString(9), rs.getString(2), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6), rs.getFloat(7), rs.getFloat(8), rs.getString(3));
            	home.setId(-1);
            	return home;
            }
        } catch(Exception e) {
        	Homes.log.warning("[Homes] Error getting active home of player " + playername + " :");
            e.printStackTrace();
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return null;
	}
	
	public boolean setActiveHome(Home home) {
		Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(SET_ACTIVE);
            st.setString(1, home.getPlayername());
            st.setString(2, home.getWorld());
            st.setDouble(3, home.getX());
            st.setDouble(4, home.getY());
            st.setDouble(5, home.getZ());
            st.setFloat(6, home.getYaw());
            st.setFloat(7, home.getPitch());
            st.setString(8, home.getName());
            st.execute();
        } catch(Exception e) {
        	Homes.log.warning("[Homes] Error setting active " + home.toString() + " :");
            e.printStackTrace();
            return false;
        } finally {
            try{
                if(conn != null) conn.close();
                if(st != null) st.close();
                if(rs != null) rs.close();
            } catch(Exception e) {}
        }
        return true;
	}
}
