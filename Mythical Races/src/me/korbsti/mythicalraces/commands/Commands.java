package me.korbsti.mythicalraces.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.korbsti.mythicalraces.MythicalRaces;
import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {
	
	MythicalRaces plugin;
	
	public Commands(MythicalRaces instance) {
		plugin = instance;
	}
	
	public boolean noPerm(CommandSender s, String perm) {
		if (s.hasPermission(perm))
			return false;
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString("noPerm")));
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if ("races".equals(label)) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Player command only");
				return false;
			}
			if (args.length == 0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString(
				        "invalidArgs")));
				return true;
			}
			if ("set".equalsIgnoreCase(args[0]) && args.length == 3) {
				if (noPerm(sender, "mythicalraces.race.setrace")) {
					return true;
				}
				for (String str : plugin.races) {
					if (str.equalsIgnoreCase(args[1])) {
						Player p = Bukkit.getServer().getPlayer(args[2]);
						if(p == null) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString("invalidPlayer")));

							return true;
						}
						plugin.dataManager.setPlayerLevel(p, 1);
						plugin.dataManager.setPlayerRace(p, str);
						plugin.setter.switchingRaces(p, str);
						plugin.dataManager.setCooldown(p, plugin.cooldown);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString("switchUserRace").replace("{race}", str)));
						return true;
					}
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString("invalidRace")));
				return true;
			}
			
			if ("choose".equalsIgnoreCase(args[0]) && args.length == 2) {
				for (String str : plugin.races) {
					if (str.equalsIgnoreCase(args[1])) {
						if (noPerm(sender, "mythicalraces.race." + str)) {
							return true;
						}
						Player p = (Player) sender;
						if (plugin.dataManager.hasCooldown((Player) sender) && !p.hasPermission(
						        "mythicalraces.cooldown.bypass")) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString(
							        "cooldownMessage").replace("{time}", plugin.dataManager.getCooldown(p))));
							return false;
						}
						plugin.dataManager.setPlayerLevel(p, 1);
						plugin.dataManager.setPlayerRace(p, str);
						plugin.setter.switchingRaces(p, str);
						plugin.dataManager.setCooldown(p, plugin.cooldown);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString(
						        "chosenRace").replace("{race}", str)));
						return true;
					}
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString(
				        "invalidRace")));
				return true;
			}
			if ("tree".equalsIgnoreCase(args[0])) {
				if (noPerm(sender, "mythicalraces.tree")) {
					return true;
				}
				plugin.treeGUI.openTree((Player) sender);
				return true;
			}
			if ("profile".equalsIgnoreCase(args[0])) {
				Player p = (Player) sender;
				
				if (noPerm(sender, "mythicalraces.profile")) {
					return true;
				}
				
				for (Object obj : plugin.configYaml.getList("profile")) {
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(obj).replace("{race}",
					        plugin.dataManager.getRace(p)).replace("{xp}", "" + plugin.dataManager.getPlayerXP(p))
					        .replace("{level}", "" + plugin.dataManager.getPlayerLevel(p)).replace("{xp-max}", "" + (plugin.xpPerLevel * plugin.dataManager.getPlayerLevel(p)))));
				}
				return true;
			}
			if ("list".equalsIgnoreCase(args[0])) {
				if (noPerm(sender, "mythicalraces.list.races")) {
					return true;
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString("listRaces")
				        .replace("{races}", plugin.races.toString())));
				return true;
			}
			if ("info".equalsIgnoreCase(args[0]) && args.length == 2) {
				if (noPerm(sender, "mythicalraces.info.races")) {
					return true;
				}
				for (String str : plugin.races) {
					if (str.equalsIgnoreCase(args[1])) {
						for (Object obj : plugin.configYaml.getList("races." + str + ".lore")) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', obj.toString()));
						}
						return true;
					}
				}
				
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString(
				        "invalidRace")));
				return true;
			}
			if ("gui".equalsIgnoreCase(args[0])) {
				if (noPerm(sender, "mythicalraces.gui")) {
					return true;
				}
				Player p = (Player) sender;
				plugin.guiNumber.put(p.getName(), 1);
				plugin.gui.selectRaceGUI(p);
				return true;
			}
			if ("help".equalsIgnoreCase(args[0])) {
				if (noPerm(sender, "mythicalraces.help")) {
					return true;
				}
				for (Object obj : plugin.configYaml.getList("help")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', obj.toString()));
				}
				return true;
			}
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.configYaml.getString("invalidArgs")));
			return false;
		}
		
		return false;
	}
	
}
