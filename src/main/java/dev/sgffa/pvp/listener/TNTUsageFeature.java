package dev.sgffa.pvp.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.RegisteredListener;

public class TNTUsageFeature implements Listener {

  @EventHandler
  public void onPlaceTNT(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();
    if (block.getType() != Material.TNT || player.getGameMode() == GameMode.CREATIVE) {
      return;
    }
    event.setCancelled(false);
  }

  @EventHandler
  public void onBlockExplode(BlockExplodeEvent event) {
    event.blockList().clear();
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    event.blockList().clear();
  }

}
