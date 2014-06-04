package me.supermaxman.donationsigns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public class DonationSigns extends JavaPlugin {
	public static DonationSigns plugin;
    public static FileConfiguration conf;
	public static final Logger log = Logger.getLogger("Minecraft");
	
	static LinkedHashMap<Integer, String> donators = new LinkedHashMap<Integer, String>();
	static LinkedHashMap<Integer, String> text = new LinkedHashMap<Integer, String>();
	static LinkedHashMap<String, Integer> signs = new LinkedHashMap<String, Integer>();
	
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		conf = plugin.getConfig();
		loadFiles();
		getServer().getPluginManager().registerEvents(new DonationSignListener(), plugin);
		log.info(getName() + " has been enabled.");
		
	}
	
	public void onDisable() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getDataFolder() + File.separator + "donators.ser"));
			oos.writeObject(donators);
			oos.close();
			ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream(getDataFolder() + File.separator + "signs.ser"));
			oos2.writeObject(signs);
			oos2.close();
			ObjectOutputStream oos3 = new ObjectOutputStream(new FileOutputStream(getDataFolder() + File.separator + "text.ser"));
			oos3.writeObject(text);
			oos3.close();
		} catch (Exception e) {
			log.warning("[" + getName() + "] Files could not be saved!");
			e.printStackTrace();
		}
		
		log.info(getName() + " has been disabled.");
	}
	
	@SuppressWarnings("unchecked")
	void loadFiles() {
		try {
			donators = (LinkedHashMap<Integer, String>) new ObjectInputStream(new FileInputStream(getDataFolder() + File.separator + "donators.ser")).readObject();
			signs = (LinkedHashMap<String, Integer>) new ObjectInputStream(new FileInputStream(getDataFolder() + File.separator + "signs.ser")).readObject();
			text = (LinkedHashMap<Integer, String>) new ObjectInputStream(new FileInputStream(getDataFolder() + File.separator + "text.ser")).readObject();
			new ObjectInputStream(new FileInputStream(getDataFolder() + File.separator + "donators.ser")).close();
			new ObjectInputStream(new FileInputStream(getDataFolder() + File.separator + "signs.ser")).close();
			new ObjectInputStream(new FileInputStream(getDataFolder() + File.separator + "text.ser")).close();
			
		} catch (Exception e) {
			log.warning("[" + getName() + "] Files could not be read! All files are now ignored.");
		}
		
	}
	
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    if(command.getName().equalsIgnoreCase("ranksign")) {
			if(args.length>1) {
				if(sender.isOp()) {
					String s = args[0];
					String t = args[1];
					LinkedHashMap<Integer, String> temp = new LinkedHashMap<Integer, String>();
					
					temp.put(1, s);
					for(Integer i : donators.keySet()) {
						if(i <= 10) {
							temp.put(i+1, donators.get(i));
						}
					}
					donators = temp;
					
					temp = new LinkedHashMap<Integer, String>();
					temp.put(1, t);
					for(Integer i : text.keySet()) {
						if(i <= 10) {
							temp.put(i+1, text.get(i));
						}
					}
					text = temp;
					
					
					if(signs.keySet()==null)return true;
					for(String sign : signs.keySet()) {
						refreshSign(sign, signs.get(sign));
					}
					sender.sendMessage(ChatColor.AQUA+"Recent Donators Updated!");
				}else {
					sender.sendMessage(ChatColor.RED+"You do not have permission for this command.");
				}
			}else {
				sender.sendMessage(ChatColor.RED+"Command used incorrectly, type /ranksign [username] [text]");
			}
	    }
		return true;
  	}
	
	public static String makeString(Location loc) {
		return loc.getWorld().getName() + "&&" + loc.getBlockX() + "&&" + loc.getBlockY() + "&&" + loc.getBlockZ(); 
	}
	
	public static Location makeLocation(String s) {
		String[] loc = s.split("&&");
		
		return new Location(DonationSigns.plugin.getServer().getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3])); 
	}
	
	public static void refreshSign(String loc, int i){
		if(DonationSigns.makeLocation(loc).getBlock().getState() instanceof Sign) {
			Sign s = (Sign) DonationSigns.makeLocation(loc).getBlock().getState();
			s.setLine(0, "Recent Buyer "+i);
			s.setLine(1, DonationSigns.text.get(i));
			s.setLine(2, DonationSigns.donators.get(i));
			refreshHead(DonationSigns.makeLocation(loc), DonationSigns.donators.get(i));
			s.update(true);
		}else {
			DonationSigns.signs.remove(loc);
		}
	}
	
	public static void refreshHead(Location loc, String n) {
		Block b = loc.getBlock().getRelative(BlockFace.UP);
		if(b.getState() instanceof Skull) {
			Skull s = (Skull) b.getState();
			try {
				s.setOwner(n);
			}catch(Exception e) {
				log.info("Player head texture could not be found!");
			}
			s.update(true);
		}
	}
	
	
	    
	
}