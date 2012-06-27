/*     */ package me.OstlerDev.FFA;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Random;
/*     */ import java.util.logging.Logger;

import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
import org.bukkit.GameMode;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public class FFA extends JavaPlugin
/*     */ {
/*     */   public static FFA plugin;
/*  21 */   public final Logger logger = Logger.getLogger("Minecraft");
/*     */ 
/*  23 */   public HashMap<Player, Integer> wait6 = new HashMap<Player, Integer>();
/*  24 */   public HashMap<Player, ItemStack[]> saveItems = new HashMap<Player, ItemStack[]>();
/*  25 */   public HashMap<Player, Integer> playerKills = new HashMap<Player, Integer>();
/*     */ 
/*     */   public void onDisable()
/*     */   {
/*  29 */     PluginDescriptionFile pdfFile = getDescription();
/*  30 */     this.logger.info(pdfFile.getName() + " Has Been Disabled!");
/*  31 */     saveConfig();
/*     */   }
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  36 */     PluginDescriptionFile pdfFile = getDescription();
/*  37 */     this.logger.info(pdfFile.getName() + ", Version " + pdfFile.getVersion() + ", Has Been Enabled!");
/*  38 */     PluginManager pm = getServer().getPluginManager();
/*  39 */     pm.registerEvents(new listener(this), this);
/*  40 */     plugin = this;
/*     */ 
/*  42 */     getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
/*     */     {
/*     */       public void run() {
/*  45 */         //FFA.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "#######################################");
/*  46 */         //FFA.plugin.getServer().broadcastMessage(ChatColor.AQUA + "Dev Preview #2 by OstlerDev");
/*  47 */         //FFA.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "#######################################");
/*     */       }
/*     */     }
/*     */     , 60L, 1200L);
/*     */   }
/*     */ 
/*     */   public int random(int x, int y) {
/*  53 */     if (x < y) {
/*  54 */       Random r = new Random();
/*  55 */       return x + r.nextInt(y - x + 1);
/*     */     }
/*  57 */     return 1;
/*     */   }
/*     */ 
/*     */   public void rTeleport(final Player p)
/*     */   {
/*  62 */     Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
 
    		public void run() {
    			plugin.saveItems.put(p, p.getInventory().getContents());
    			/*  64 */     p.sendMessage(ChatColor.GREEN + "Your inventory has been saved!");
    			/*     */     plugin.wait6.remove(p);
    			/*  66 */     Location l = null;
    			/*  67 */     int i = random(1, 20);
    			/*  68 */     String arenaloc = (String)getConfig().get("FFA.spawn." + i);
    			/*  69 */     String[] arenalocarr = arenaloc.split(",");
    			/*  70 */     if (arenalocarr.length == 3) {
    			/*  71 */       int x = Integer.parseInt(arenalocarr[0]);
    			/*  72 */       int y = Integer.parseInt(arenalocarr[1]);
    			/*  73 */       int z = Integer.parseInt(arenalocarr[2]);
    			/*  74 */       l = new Location(getServer().getWorld("world"), x, y, z);
    			/*     */     }
    						
    			/*  77 */     p.teleport(l);
    			/*  78 */     Inventory b = p.getInventory();
    			/*  79 */     b.clear();
    			/*  80 */     ItemStack sword = new ItemStack(Material.IRON_SWORD);
    			/*  81 */     ItemStack food = new ItemStack(Material.ROTTEN_FLESH, 16);
    			/*  82 */     ItemStack axe = new ItemStack(Material.IRON_AXE);
    			/*  83 */     b.addItem(new ItemStack[] { sword, food, axe });
    			/*  84 */     p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
    			/*  85 */     p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
    			/*  86 */     p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
    			/*  87 */     p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
    			/*  88 */     plugin.playerKills.put(p, Integer.valueOf(0));
    }
}, 120L);
/*     */   }
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
/*  92 */     if ((sender instanceof Player)) {
/*  93 */       Player p = (Player)sender;
/*  94 */       if ((commandLabel.equalsIgnoreCase("ffa")) && (!this.playerKills.containsKey(p)) && (p.hasPermission("FFA.default"))) {
/*  95 */         if (args.length == 0) {
/*  96 */           p.sendMessage(ChatColor.GREEN + "You are about to enter the arena, please wait 6 seconds");
/*     */ 			if (p.getGameMode() == GameMode.CREATIVE) p.sendMessage("Switching you to Survival!");
					p.setGameMode(GameMode.SURVIVAL);
					this.wait6.put(p, 600);
/*  98 */           rTeleport(p);
/*     */         }
/* 108 */         else if (args.length == 1) {
/* 109 */           if ((args[0].equalsIgnoreCase("reload")) && (p.hasPermission("FFA.*")))
/* 110 */             reloadConfig();
/* 111 */           else if (args[0].equalsIgnoreCase("lb")) p.hasPermission("FFA.*");
/*     */ 
/*     */         }
/* 114 */         else if (args.length == 2) {
/* 115 */           if ((args[0].equalsIgnoreCase("setspawn")) && (p.hasPermission("FFA.*"))) {
/* 116 */             String a = args[1];
/*     */ 			
/* 118 */             if (!getConfig().contains("FFA.spawn." + a)) {
/* 119 */               getConfig().set("FFA.spawn." + a, null);
/*     */             }
/*     */ 
/* 122 */             Location loc = p.getLocation();
/* 123 */             int blockX = loc.getBlockX();
/* 124 */             int blockY = loc.getBlockY();
/* 125 */             int blockZ = loc.getBlockZ();
/* 126 */             getConfig().set("FFA.spawn." + a, blockX + "," + blockY + "," + blockZ);
/* 127 */             p.sendMessage(ChatColor.GOLD + "Spawn #" + a + " has been set succesfully at:" + blockX + ", " + blockY + ", " + blockZ);
/* 128 */           } else if (args[0].equalsIgnoreCase("removespawn")) {
/* 129 */             String a = args[1];
/* 130 */             getConfig().set("FFA.spawn." + a, null);
/* 131 */             p.sendMessage(ChatColor.GOLD + "FFA.spawn." + a + " have been set to null");
/*     */           }
/*     */         } else {
/* 134 */           p.sendMessage(ChatColor.GREEN + "Invalid command!");
/*     */         }
/*     */       }
/* 137 */       else p.sendMessage(ChatColor.GREEN + "You can't /ffa while in battle!");
/*     */     }
/*     */ 
/* 140 */     return false;
/*     */   }

			public boolean wait360Ticks(Player player){
				int i = this.wait6.get(player);
				if (i == 0) return true;
				else this.wait6.put(player, i--);
				return false;
			}
/*     */ }