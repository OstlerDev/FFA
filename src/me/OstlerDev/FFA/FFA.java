package me.OstlerDev.FFA;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FFA extends JavaPlugin
{
	public static FFA plugin;
	public final Logger logger = Logger.getLogger("Minecraft");

	public HashMap<Player, Integer> wait6 = new HashMap<Player, Integer>();
	public HashMap<Player, ItemStack[]> saveItems = new HashMap<Player, ItemStack[]>();
	public HashMap<Player, Location> saveLocation = new HashMap<Player, Location>();
	public HashMap<Player, Integer> playerKills = new HashMap<Player, Integer>();

	public void onDisable()
	{
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " Has Been Disabled!");
		saveConfig();
	}

	public void onEnable()
	{
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + ", Version "
				+ pdfFile.getVersion() + ", Has Been Enabled!");
		PluginManager pm = getServer().getPluginManager();
		EntityDamageEvent.getHandlerList().unregister(com.massivecraft.factions.P.p.entityListener);
		pm.registerEvents(new listener(this), this);
		plugin = this;

		getServer().getScheduler().scheduleAsyncRepeatingTask(this,
				new Runnable()
				{
					public void run()
					{
						//plugin.getServer().broadcastMessage("Dev Preview by: OstlerDev");
					}
				}, 60L, 1200L);
	}

	public int random(int x, int y)
	{
		if (x < y)
		{
			Random r = new Random();
			return x + r.nextInt(y - x + 1);
		}
		return 1;
	}

	public void rTeleport(final Player p)
	{
		int i = Bukkit.getServer().getScheduler()
				.scheduleSyncDelayedTask(this, new Runnable()
				{

					public void run()
					{	
						if (plugin.wait6.containsKey(p))
						{
							plugin.saveItems.put(p, p.getInventory()
									.getContents());
							p.sendMessage(ChatColor.GREEN
									+ "Your inventory has been saved!");
							plugin.saveLocation.put(p, p.getLocation());
							plugin.wait6.remove(p);
							Location l = null;
							int i = random(1, 5);
							String arenaloc = (String) getConfig().get(
									"FFA.spawn." + i);
							String[] arenalocarr = arenaloc.split(",");
							if (arenalocarr.length == 3)
							{
								int x = Integer.parseInt(arenalocarr[0]);
								int y = Integer.parseInt(arenalocarr[1]);
								int z = Integer.parseInt(arenalocarr[2]);
								l = new Location(getServer().getWorld("world"),
										x, y, z);
							}

							p.teleport(l);
							Inventory b = p.getInventory();
							b.clear();
							ItemStack sword = new ItemStack(Material.IRON_SWORD);
							ItemStack food = new ItemStack(
									Material.ROTTEN_FLESH, 16);
							ItemStack axe = new ItemStack(Material.IRON_AXE);
							b.addItem(new ItemStack[] { sword, food, axe });
							p.getInventory().setChestplate(
									new ItemStack(Material.IRON_CHESTPLATE));
							p.getInventory().setLeggings(
									new ItemStack(Material.IRON_LEGGINGS));
							p.getInventory().setHelmet(
									new ItemStack(Material.IRON_HELMET));
							p.getInventory().setBoots(
									new ItemStack(Material.IRON_BOOTS));
							plugin.playerKills.put(p, Integer.valueOf(0));
						}
					}
				}, 120L);
		
		plugin.wait6.put(p, i);
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args)
	{
		if ((sender instanceof Player))
		{
			Player p = (Player) sender;
			if ((commandLabel.equalsIgnoreCase("ffa"))
					&& (p.hasPermission("FFA.default")))
			{
				if(!this.playerKills.containsKey(p))
				{
				if (args.length == 0)
				{
					p.sendMessage(ChatColor.GREEN
							+ "You are about to enter the arena, please wait 6 seconds");
					if (p.getGameMode() == GameMode.CREATIVE)
						p.sendMessage("Switching you to Survival!");
					p.setGameMode(GameMode.SURVIVAL);
					rTeleport(p);
				}
				else if (args.length == 1)
				{
					if ((args[0].equalsIgnoreCase("reload"))
							&& (p.hasPermission("FFA.*")))
						reloadConfig();
					else if (args[0].equalsIgnoreCase("lb"))
						p.hasPermission("FFA.*");

				}
				else if (args.length == 2)
				{
					if ((args[0].equalsIgnoreCase("setspawn"))
							&& (p.hasPermission("FFA.*")))
					{
						String a = args[1];

						if (!getConfig().contains("FFA.spawn." + a))
						{
							getConfig().set("FFA.spawn." + a, null);
						}

						Location loc = p.getLocation();
						int blockX = loc.getBlockX();
						int blockY = loc.getBlockY();
						int blockZ = loc.getBlockZ();
						getConfig().set("FFA.spawn." + a,
								blockX + "," + blockY + "," + blockZ);
						p.sendMessage(ChatColor.GOLD + "Spawn #" + a
								+ " has been set succesfully at:" + blockX
								+ ", " + blockY + ", " + blockZ);
					}
					else if (args[0].equalsIgnoreCase("removespawn"))
					{
						String a = args[1];
						getConfig().set("FFA.spawn." + a, null);
						p.sendMessage(ChatColor.GOLD + "FFA.spawn." + a
								+ " have been set to null");
					}
				}
				else
				{
					p.sendMessage(ChatColor.RED + "Invalid command!");
				}
				}
				else
					p.sendMessage(ChatColor.RED
							+ "You can't /ffa while in battle!");
			}
			else
				p.sendMessage(ChatColor.RED
						+ "You don't have permission for this command!");
		}

		return false;
	}

	/*public boolean wait360Ticks(Player player)
	{
		int i = this.wait6.get(player);
		if (i == 0)
			return true;
		else
			this.wait6.put(player, i--);
		return false;
	}*/
}