/*     */ package me.OstlerDev.FFA;
/*     */ 
/*     */ import java.util.Map.Entry;
/*     */ import org.bukkit.ChatColor;
import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.HumanEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.SignChangeEvent;
/*     */ import org.bukkit.event.entity.PlayerDeathEvent;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.player.PlayerDropItemEvent;
/*     */ import org.bukkit.event.player.PlayerMoveEvent;
/*     */ import org.bukkit.event.player.PlayerQuitEvent;
/*     */ import org.bukkit.event.player.PlayerRespawnEvent;
/*     */ import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class listener
/*     */   implements Listener
/*     */ {
/*  24 */   public static FFA ffa = null;
/*     */ 
/*     */   public listener(Plugin plugin) {
/*  27 */     ffa = (FFA)plugin;
/*     */   }
/*     */   @EventHandler
/*     */   public void onMove(PlayerMoveEvent event) {
/*  32 */     Player p = event.getPlayer();
/*  33 */     if (ffa.wait6.containsKey(p))
/*  34 */       event.setCancelled(true);
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onDeath(PlayerDeathEvent event) {
/*  40 */     Entity e = event.getEntity();
/*  41 */     event.getDrops().removeAll(event.getDrops());
/*  42 */     if ((e instanceof Player)) {
/*  43 */       Player killer = ((Player) e).getKiller();
/*  44 */       if (killer != null) {
/*  45 */         int x = (ffa.playerKills.get(killer)).intValue();
				  String xString = Integer.toString(x);
/*  46 */         killer.sendMessage(xString);
/*  47 */         int newx = x + 1;
				  xString = Integer.toString(newx);
/*  48 */         killer.sendMessage(xString);
/*  49 */         ffa.playerKills.remove(killer);
/*  50 */         ffa.playerKills.put(killer, Integer.valueOf(newx));
/*  51 */         killer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.ROTTEN_FLESH, 16) });
/*  52 */         if (newx == 1) {
/*  53 */           killer.getInventory().getItem(0).setType(Material.DIAMOND_SWORD);
/*  54 */           killer.getInventory().getItem(2).setType(Material.DIAMOND_AXE);
/*  55 */         } else if (newx == 2) {
/*  56 */           killer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.BOW) });
/*  57 */           killer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.ARROW, 32) });
/*  58 */         } else if ((newx == 3) || (newx == 6) || (newx == 9) || (newx == 12) || (newx == 15) || (newx == 18) || (newx == 21) || (newx == 24)) {
/*  59 */           killer.sendMessage(ChatColor.GREEN + "You have won $150!");
/*  60 */           ffa.getServer().dispatchCommand(ffa.getServer().getConsoleSender(), "eco give " + killer.getName() + " 150");
/*  61 */           killer.getInventory().addItem(new ItemStack[] { new ItemStack(Material.ARROW, 32) });
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void respawn(PlayerRespawnEvent event) {
/*  70 */     Player p = event.getPlayer().getPlayer();
/*  71 */     if (ffa.saveItems.containsKey(p))
/*  72 */       for (Entry<Player, ItemStack[]> entry : ffa.saveItems.entrySet()) {
/*  73 */         Player pl = (Player)entry.getKey();
/*  74 */         ItemStack[] a = (ItemStack[])entry.getValue();
/*  75 */         if (pl == p) {
/*  76 */           pl.getInventory().clear();
/*  77 */           pl.getInventory().setContents(a);
/*  78 */           ffa.saveItems.remove(pl);
/*  79 */           ffa.playerKills.remove(pl);
/*  80 */           pl.sendMessage(ChatColor.GREEN + "Your inventory has been restored!");
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void inventoryClick(InventoryClickEvent event) {
/*  88 */     HumanEntity e = event.getWhoClicked();
/*  89 */     if ((e instanceof Player)) {
/*  90 */       Player p = ((Player)e).getPlayer();
/*  91 */       if (ffa.playerKills.containsKey(p)) {
/*  92 */         event.setCancelled(true);
/*  93 */         p.sendMessage(ChatColor.GREEN + "You are not allowd to move items in your inventory while in FFA");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void inventoryDrop(PlayerDropItemEvent event) {
/* 100 */     Player p = event.getPlayer();
/* 101 */     if (ffa.playerKills.containsKey(p)) {
/* 102 */       event.setCancelled(true);
/* 103 */       p.sendMessage(ChatColor.GREEN + "You are not allowd to drop items in FFA");
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void signPlace(SignChangeEvent event) {
/* 109 */     int kills = 2342;
/* 110 */     int deaths = 1239;
/* 111 */     float kd = kills / deaths;
/* 112 */     String playerName = "TomShar";
/*     */ 
/* 114 */     String one = "K:D";
/* 115 */     String two = kills + ":" + deaths;
/* 116 */     String three = Float.toString(kd);
/* 117 */     if (event.getLine(0).contains("[lb1]")) {
/* 118 */       event.setLine(0, playerName);
/* 119 */       event.setLine(1, one);
/* 120 */       event.setLine(2, two);
/* 121 */       event.setLine(3, three);
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onQuit(PlayerQuitEvent event) {
/* 127 */     Player p = event.getPlayer();
				p.setHealth(20);
				Location l = null;
				if (ffa.playerKills.containsKey(p))
					if (p.getBedSpawnLocation() != null)
						l = p.getBedSpawnLocation();
					else l = p.getCompassTarget();
				p.teleport(l);
				if (ffa.saveItems.containsKey(p))
					for (Entry<Player, ItemStack[]> entry : ffa.saveItems.entrySet()) {
						Player pl = (Player)entry.getKey();
						ItemStack[] a = (ItemStack[])entry.getValue();
						if (pl == p) {
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
/*     */   }
/*     */ }

/* Location:           /Users/Josh/Downloads/FFA.jar
 * Qualified Name:     me.TomShar.FFA.listener
 * JD-Core Version:    0.6.0
 */