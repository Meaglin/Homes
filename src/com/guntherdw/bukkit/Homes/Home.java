package com.guntherdw.bukkit.Homes;

import org.bukkit.Location;
import org.bukkit.Server;

/**
 * @author GuntherDW
 */
public class Home {
	
	private int id;
	private String name, world, playername;
    private double X,Y,Z;
    private float Pitch,Yaw;
    
    public Home(String name, String playername, double x, double y, double z, float yaw, float pitch, String world) {
    	this.name = name;
    	this.playername = playername;
    	this.X = x;
        this.Y = y;
        this.Z = z;
        this.Yaw = yaw;
        this.Pitch = pitch;
        this.world = world;
    }
    
    public Home(String name, String playername, Location location) {
		this(name, playername, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.getWorld().getName());
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the world
	 */
	public String getWorld() {
		return world;
	}

	/**
	 * @param world the world to set
	 */
	public void setWorld(String world) {
		this.world = world;
	}

	/**
	 * @return the playername
	 */
	public String getPlayername() {
		return playername;
	}

	/**
	 * @param playername the playername to set
	 */
	public void setPlayername(String playername) {
		this.playername = playername;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return X;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		X = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return Y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		Y = y;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return Z;
	}

	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		Z = z;
	}

	/**
	 * @return the pitch
	 */
	public float getPitch() {
		return Pitch;
	}

	/**
	 * @param pitch the pitch to set
	 */
	public void setPitch(float pitch) {
		Pitch = pitch;
	}

	/**
	 * @return the yaw
	 */
	public float getYaw() {
		return Yaw;
	}

	/**
	 * @param yaw the yaw to set
	 */
	public void setYaw(float yaw) {
		Yaw = yaw;
	}

	public Location getLocation(Server server) {
    	return new Location(server.getWorld(getWorld()),
                getX(), getY()+1, getZ(), getYaw(), getPitch() );
    }

    public String toString() {
        return "Home{name:" + getName() + " playername:" + getPlayername() + " x:"+this.X+",y:"+this.Y+",z:"+this.Z+",Yaw:"+this.Yaw+",Pitch:"+this.Pitch+",World:"+this.world+"}";
    }
}
