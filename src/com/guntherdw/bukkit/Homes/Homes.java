package com.guntherdw.bukkit.Homes;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Homes extends JavaPlugin {
    protected final static Logger log = Logger.getLogger("Minecraft");
    public static Permissions perm = null;
    public Map<String, Home> homes;
    public List<String> savehomesTCUtils;
    public TweakcraftUtils tweakcraftutils;
    private Database database;
    
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info("["+pdfFile.getName() + "] Homes version " + pdfFile.getVersion() + " is disabled!");
    }

    public void initConfig()
    {
        try{
        	getConfiguration().setProperty("dbhost", "localhost");
            getConfiguration().setProperty("database", "databasename");
            getConfiguration().setProperty("username", "database-username");
            getConfiguration().setProperty("password", "database-password");
        } catch (Throwable e)
        {
            log.severe("[Homes] There was an exception while we were saving the config, be sure to doublecheck!");
        }
    }

    public void onEnable() {
        if(getConfiguration() == null)
        {
            log.severe("[Homes] You have to configure me now, reboot the server after you're done!");
            getDataFolder().mkdirs();
            initConfig();
            this.setEnabled(false);
        }
        database = new Database(this);
        savehomesTCUtils = new ArrayList<String>();
        PluginDescriptionFile pdfFile = this.getDescription();
        this.setupPermissions();
        this.setupTCUtils();
        log.info("["+pdfFile.getName() + "] Homes version " + pdfFile.getVersion() + " is enabled!");
    }

    public void setupPermissions() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (perm == null) {
            if (plugin != null) {
                perm = (Permissions) plugin;
            }
        }
    }
    
    public void setupTCUtils() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("TweakcraftUtils");

        if (tweakcraftutils == null) {
            if (plugin != null) {
                tweakcraftutils = (TweakcraftUtils) plugin;
            }
        }
    }

    public boolean check(Player player, String permNode) {
        if (perm == null) {
            return true;
        } else {
            return player.isOp() ||
                    perm.getHandler().permission(player, permNode);
        }
    }
    
    public Database getMysqlDatabase() {
    	return database;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    	if (!(commandSender instanceof Player)) {
    		commandSender.sendMessage(ChatColor.RED + "What are you doing here ?");
    		return true;
    	}
    	Player player = (Player) commandSender;
    	String cmd = command.getName().toLowerCase();
    	
    	if(cmd.equals("homes")) {
    		if(!check(player, "homes.homes")) {
    			player.sendMessage(ChatColor.RED +"You do not have the required permissions to use this command.");
    			return true;
    		}
    		if(strings.length < 1) {
    			player.sendMessage(ChatColor.RED + "Usage: /homes [add|del|use|list|goto] <name>");
    			return true;
    		}
    		String sub = strings[0].toLowerCase();
    		String name = "";
    		if(strings.length > 1) {
    			for(int i = 1; i < strings.length;i++) {
    				name += " " + strings[i];
    			}
    		}
    		if(!name.equals(""))name = name.substring(1);
    		name = name.replaceAll("%", "\\%").replaceAll("_", "\\_");
    		if(sub.equals("add")) {
    			if(name.equals("")) {
    				player.sendMessage(ChatColor.RED + "Usage: /homes [add|del|use|list|goto] <name>");
    				return true;
    			}
    			Home home = new Home(name, player.getName(), player.getLocation());
    			if(getMysqlDatabase().save(home)) {
    				player.sendMessage(ChatColor.GREEN + "Home '" + home.getName() + "' saved.");
    				log.info("[Homes] player " + player.getName() + " saved " + home.toString() + "."); 
    			} else {
    				player.sendMessage(ChatColor.RED + "Error saving home '" + home.getName() + "' please contact an admin.");
    			}
    		} else if(sub.equals("del")) {
    			if(name.equals("")) {
    				player.sendMessage(ChatColor.RED + "Usage: /homes [add|del|use|list|goto] <name>");
    				return true;
    			}
    			Home home = getMysqlDatabase().getHome(name, player.getName());
    			if(home == null) {
    				player.sendMessage(ChatColor.RED + "Cannot find home with name '" + name + "' to delete.");
    				return true;
    			}
    			if(getMysqlDatabase().delete(home)) {
    				player.sendMessage(ChatColor.GREEN + "Home '" + home.getName() + "' succesfully deleted.");
    				log.info("[Homes] player " + player.getName() + " deleted " + home.toString() + "."); 
    			} else {
    				player.sendMessage(ChatColor.RED + "Error deleting home '" + home.getName() + "' please contact an admin.");
    			}
    		} else if(sub.equals("use")) {
    			if(name.equals("")) {
    				player.sendMessage(ChatColor.RED + "Usage: /homes [add|del|use|list|goto] <name>");
    				return true;
    			}
    			Home home = getMysqlDatabase().getHome(name + "%", player.getName());
    			if(home == null) {
    				player.sendMessage(ChatColor.RED + "Cannot find home with name '" + name + "' to use.");
    				return true;
    			}
    			if(getMysqlDatabase().setActiveHome(home)) {
    				player.sendMessage(ChatColor.GREEN + "Succesfully loaded home '" + home.getName() + "' !");
    				log.info("[Homes] player " + player.getName() + " loaded home " + home.getName() + "."); 
    			} else {
    				player.sendMessage(ChatColor.RED + "Error loading home '" + home.getName() + "' please contact an admin.");
    			}
    		} else if(sub.equals("goto")) {
    			if(name.equals("")) {
    				player.sendMessage(ChatColor.RED + "Usage: /homes [add|del|use|list|goto] <name>");
    				return true;
    			}
    			Home home = getMysqlDatabase().getHome(name + "%", player.getName());
    			if(home == null) {
    				player.sendMessage(ChatColor.RED + "Cannot find home with name '" + name + "' to use.");
    				return true;
    			}
    			if(getMysqlDatabase().setActiveHome(home)) {
    				player.sendMessage(ChatColor.GOLD + "Now teleporting you to home '" + home.getName() + "' !");
    				gotoHome(player, home);
    				log.info("[Homes] player " + player.getName() + " used home " + home.getName() + "."); 
    			} else {
    				player.sendMessage(ChatColor.RED + "Error using home '" + home.getName() + "' please contact an admin.");
    			}
    		} else if(sub.equals("list")) {
    			List<Home> homes = getMysqlDatabase().getHomes(player.getName());
    			if(homes.size() == 0) {
    				player.sendMessage(ChatColor.GOLD + "No homes found.");
    				return true;
    			}
    			player.sendMessage(ChatColor.GOLD + "Your homes:");
    			String str = "";
    			for(Home home : homes)
    				str += ", " + home.getName();
    			str = str.substring(2);
    			player.sendMessage(str);
    		} else if(sub.equals("tpback")) {
    			if(tweakcraftutils == null) {
    				player.sendMessage(ChatColor.GOLD + "This feature is disabled!");
    				return true;
    			}
    			if(savehomesTCUtils.contains(player.getName())) {
    				savehomesTCUtils.remove(player.getName());
    				player.sendMessage(ChatColor.GOLD + "Now recording tpback while using homes.");
    			} else {
    				savehomesTCUtils.add(player.getName());
    				player.sendMessage(ChatColor.GOLD + "Now stopped recording tpback while using homes!");
    			}
    		} else {
    			player.sendMessage(ChatColor.RED + "Invalid command " + sub + "!");
    			player.sendMessage(ChatColor.RED + "Usage: /homes [add|del|use|list|goto] <name>");
    			return true;
    		}
    		
    	} else if(cmd.equals("home")) {
    		if(!check(player, "homes.home")) {
    			player.sendMessage(ChatColor.RED +"You do not have the required permissions to use this command.");
    			return true;
    		}
    		String name = "";
    		if(strings.length > 0) {
    			for(int i = 0; i < strings.length;i++) {
    				name += " " + strings[i];
    			}
    		}
    		if(!name.equals(""))name = name.substring(1);
    		name = name.replaceAll("%", "\\%").replaceAll("_", "\\_");
    		if(name.equals("")) {
    			Home home = getMysqlDatabase().getActiveHome(player.getName());
    			if(home == null) {
    				player.sendMessage(ChatColor.GOLD + "You have no home yet, set 1 now: /sethome!");
    			} else {
    				if(home.getName() != null && !home.getName().equals(""))player.sendMessage(ChatColor.GOLD + "Now teleporting you to home '" + home.getName() + "' !");
    				else player.sendMessage(ChatColor.GOLD + "Now teleporting you to your home!");
    				gotoHome(player, home);
    				log.info("[Homes] player " + player.getName() + " used home " + home.getName() + "."); 
    			}
    		} else {
    			Home home = getMysqlDatabase().getHome(name + "%", player.getName());
    			if(home == null) {
    				player.sendMessage(ChatColor.RED + "Cannot find home with name '" + name + "' to use.");
    				return true;
    			}
    			if(getMysqlDatabase().setActiveHome(home)) {
    				player.sendMessage(ChatColor.GOLD + "Now teleporting you to home '" + home.getName() + "' !");
    				gotoHome(player, home);
    				log.info("[Homes] player " + player.getName() + " used home " + home.getName() + "."); 
    			} else {
    				player.sendMessage(ChatColor.RED + "Error using home '" + home.getName() + "' please contact an admin.");
    			}
    		}
    	} else if(cmd.equals("sethome")) {
    		if(!check(player, "homes.sethome")) {
    			player.sendMessage(ChatColor.RED +"You do not have the required permissions to use this command.");
    			return true;
    		}
    		String name = "";
    		if(strings.length > 0) {
    			for(int i = 0; i < strings.length;i++) {
    				name += " " + strings[i];
    			}
    		}
    		if(!name.equals(""))name = name.substring(1);
    		name = name.replaceAll("%", "\\%").replaceAll("_", "\\_");
    		if(name.equals("")) {
    			Home home = new Home("", player.getName(), player.getLocation());
    			if(getMysqlDatabase().setActiveHome(home)) {
    				player.sendMessage(ChatColor.GREEN + "Home saved.");
    				log.info("[Homes] player " + player.getName() + " saved " + home.toString() + "."); 
    			} else {
    				player.sendMessage(ChatColor.RED + "Error saving home '" + home.getName() + "' please contact an admin.");
    			}
    		} else {
    			Home home = new Home(name, player.getName(), player.getLocation());
    			if(getMysqlDatabase().save(home) && getMysqlDatabase().setActiveHome(home)) {
    				player.sendMessage(ChatColor.GREEN + "Home '" + home.getName() + "' saved.");
    				log.info("[Homes] player " + player.getName() + " saved " + home.toString() + "."); 
    			} else {
    				player.sendMessage(ChatColor.RED + "Error saving home '" + home.getName() + "' please contact an admin.");
    			}
    		}
    	} else if(cmd.equals("reloadhomes")) {
    		if(!check(player, "homes.admin")) {
    			player.sendMessage(ChatColor.RED +"You do not have the required permissions to use this command.");
    			return true;
    		}
    		database = new Database(this);
    		player.sendMessage(ChatColor.GOLD + "Homes now reloaded.");
    		log.info("[Homes] player " + player.getName() + " reloaded Homes."); 
    	}
    	
        return true;
    }
    
    public void gotoHome(Player player, Home home) {
    	Location loc = home.getLocation(getServer());
        if(tweakcraftutils != null) {
            if(!savehomesTCUtils.contains(player.getName())) {
                tweakcraftutils.getTelehistory().addHistory(player.getName(), player.getLocation());
            }
        }
        player.teleport(loc);
    }
    
}
