package me.OstlerDev.FFA;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.P;

public class listener implements Listener
{
	public static FFA ffa = null;

	public listener(Plugin plugin)
	{
		ffa = (FFA) plugin;
	}

	/*@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event)
	{	
		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent eevent = (EntityDamageByEntityEvent) event;

			if (eevent.getDamager() instanceof Player)
			{
				if (ffa.saveItems.containsKey((Player) eevent.getDamager()))
				{
					for(RegisteredListener listener : event.getHandlers().getRegisteredListeners())
					{
						if(listener.getPlugin() instanceof P)
						{
							//toregister = listener;
							event.getHandlers().unregister(listener);
							Bukkit.getPluginManager().callEvent(event);
							EntityDamageEvent.getHandlerList().register(listener);
						}
					}
				}
			}
		}
	}*/
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event.isCancelled()) return;

		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
			
			if (sub.getEntity() instanceof Player)
			{
				Player attacker = null;
				if(sub.getDamager() instanceof Player)
					attacker = (Player) sub.getDamager();
				if(sub.getDamager() instanceof Arrow && ((Arrow) sub.getDamager()).getShooter() instanceof Player)
					attacker = (Player) ((Arrow) sub.getDamager()).getShooter();

				if(attacker != null)
				{
					if (ffa.saveItems.containsKey((Player) sub.getDamager()) && ffa.saveItems.containsKey((Player) sub.getEntity()))
					{
						return;
					}
				}
			}

			if (!P.p.entityListener.canDamagerHurtDamagee(sub, true))
			{
				event.setCancelled(true);
			}
		}
		// TODO: Add a no damage at all flag??
		/*else if (Conf.safeZonePreventAllDamageToPlayers && isPlayerInSafeZone(event.getEntity()))
		{
			// Players can not take any damage in a Safe Zone
			event.setCancelled(true);
		}*/
	}

	/*@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage2(EntityDamageEvent event)
	{
		if(toregister != null)
		{
			EntityDamageEvent.getHandlerList().register(toregister);
			toregister = null;
		}
	}*/

	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{
		Player p = event.getPlayer();

		if (ffa.wait6.containsKey(p))
		{
			if (event.getFrom().getBlockX() != event.getTo().getBlockX()
					|| event.getFrom().getBlockZ() != event.getTo().getBlockZ())
			{
				Bukkit.getScheduler().cancelTask(ffa.wait6.get(p));
				ffa.wait6.remove(p);
				event.getPlayer().sendMessage(
						ChatColor.RED
								+ "Teleport cancelled due to player movement.");
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event)
	{
		Entity e = event.getEntity();
		event.getDrops().removeAll(event.getDrops());
		if ((e instanceof Player))
		{
			Player killer = ((Player) e).getKiller();
			if (killer != null)
			{
				int x = (ffa.playerKills.get(killer)).intValue();
				//String xString = Integer.toString(x);
				//killer.sendMessage(xString);
				int newx = x + 1;
				//xString = Integer.toString(newx);
				//killer.sendMessage(xString);
				ffa.playerKills.remove(killer);
				ffa.playerKills.put(killer, Integer.valueOf(newx));
				killer.getInventory().addItem(
						new ItemStack[] { new ItemStack(Material.ROTTEN_FLESH,
								16) });
				if (newx == 1)
				{
					killer.getInventory().getItem(0)
							.setType(Material.DIAMOND_SWORD);
					killer.getInventory().getItem(2)
							.setType(Material.DIAMOND_AXE);
				}
				else if (newx == 2)
				{
					killer.getInventory().addItem(
							new ItemStack[] { new ItemStack(Material.BOW) });
					killer.getInventory()
							.addItem(
									new ItemStack[] { new ItemStack(
											Material.ARROW, 32) });
				}
				else if ((newx == 3) || (newx == 6) || (newx == 9)
						|| (newx == 12) || (newx == 15) || (newx == 18)
						|| (newx == 21) || (newx == 24))
				{
					killer.sendMessage(ChatColor.GREEN + "You have won $150!");
					ffa.getServer().dispatchCommand(
							ffa.getServer().getConsoleSender(),
							"eco give " + killer.getName() + " 150");
					killer.getInventory()
							.addItem(
									new ItemStack[] { new ItemStack(
											Material.ARROW, 32) });
				}
			}
		}
	}

	@EventHandler
	public void respawn(PlayerRespawnEvent event)
	{
		Player p = event.getPlayer().getPlayer();
		if (ffa.saveItems.containsKey(p))
		{
			Entry<Player, ItemStack[]>[] entries = (Entry<Player, ItemStack[]>[]) ffa.saveItems.entrySet().toArray(new Entry[0]);
			for (int i = 0;i<ffa.saveItems.entrySet().size();i++)
			{
				Entry<Player, ItemStack[]> entry = entries[i];
				
				Player pl = (Player) entry.getKey();
				ItemStack[] a = (ItemStack[]) entry.getValue();
				if (pl == p)
				{
					pl.getInventory().clear();
					pl.getInventory().setContents(a);
					ffa.saveItems.remove(pl);
					ffa.playerKills.remove(pl);
					pl.sendMessage(ChatColor.GREEN + "Your inventory has been restored!");
				}
			}
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent event)
	{
		HumanEntity e = event.getWhoClicked();
		if ((e instanceof Player))
		{
			Player p = ((Player) e).getPlayer();
			if (ffa.playerKills.containsKey(p))
			{
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You are not allowed to move items in your inventory while in FFA.");
			}
		}
	}

	@EventHandler
	public void inventoryDrop(PlayerDropItemEvent event)
	{
		Player p = event.getPlayer();
		if (ffa.playerKills.containsKey(p))
		{
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "You are not allowed to drop items in FFA.");
		}
	}

	@EventHandler
	public void signPlace(SignChangeEvent event)
	{
		int kills = 2342;
		int deaths = 1239;
		float kd = kills / deaths;
		String playerName = "TomShar";

		String one = "K:D";
		String two = kills + ":" + deaths;
		String three = Float.toString(kd);
		if (event.getLine(0).contains("[lb1]"))
		{
			event.setLine(0, playerName);
			event.setLine(1, one);
			event.setLine(2, two);
			event.setLine(3, three);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		p.setHealth(20);
		Location l = ffa.saveLocation.get(p);
		p.teleport(l);
		ffa.saveLocation.remove(p);
		if (ffa.saveItems.containsKey(p))
		{
			Entry<Player, ItemStack[]>[] entries = (Entry<Player, ItemStack[]>[]) ffa.saveItems.entrySet().toArray(new Entry[0]);
			for (int i = 0;i<ffa.saveItems.entrySet().size();i++)
			{
				Entry<Player, ItemStack[]> entry = entries[i];
				Player pl = (Player) entry.getKey();
				ItemStack[] a = (ItemStack[]) entry.getValue();
				if (pl == p)
				{
					pl.getInventory().clear();
					pl.getInventory().setBoots(new ItemStack(Material.AIR));
					pl.getInventory().setLeggings(new ItemStack(Material.AIR));
					pl.getInventory().setChestplate(new ItemStack(Material.AIR));
					pl.getInventory().setHelmet(new ItemStack(Material.AIR));
					pl.getInventory().setContents(a);
					ffa.saveItems.remove(pl);
					ffa.playerKills.remove(pl);
					pl.sendMessage(ChatColor.GREEN + "Your inventory has been restored!");
				}
			}
		}
	}
}