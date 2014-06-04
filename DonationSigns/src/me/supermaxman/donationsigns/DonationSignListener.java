package me.supermaxman.donationsigns;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

	
public class DonationSignListener implements Listener {
	
	
	@EventHandler
	public void onSignchange(SignChangeEvent e) {
		if(e.getLine(0).contains("[donator") && e.getPlayer().isOp()) {
			e.setCancelled(true);
			try {
				int i = Integer.parseInt(e.getLine(0).substring(8, 9));
				DonationSigns.signs.put(DonationSigns.makeString(e.getBlock().getLocation()), i);
				DonationSigns.refreshSign(DonationSigns.makeString(e.getBlock().getLocation()), i);
			}catch(Exception ex) {
				e.getPlayer().sendMessage(ChatColor.RED+"Incorrect number");
			}

		}
	}
	
	
}
