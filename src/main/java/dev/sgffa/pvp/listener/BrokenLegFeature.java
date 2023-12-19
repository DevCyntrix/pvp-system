package dev.sgffa.pvp.listener;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AllArgsConstructor
public class BrokenLegFeature implements Listener {

  private final JavaPlugin plugin;

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
      return;
    }
    if (event.getDamage() <= 4) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    int duration = (int) (20 * 10 + 20 * 60 * Math.random());
    if (duration > 20 * 30) {
      player.addPotionEffect(
          new PotionEffect(PotionEffectType.CONFUSION, 20 * 15, 0, false, false, false));
    }

    // TODO: Make a transition of this effect

    player.addPotionEffect(
        new PotionEffect(PotionEffectType.SLOW, duration, 0, false, false, false));
    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 0.8F);
    player.playSound(player.getLocation(), Sound.BLOCK_METAL_BREAK, 1.0F, 0.2F);

    Particle.DustOptions options = new Particle.DustOptions(Color.RED, 2);

    long end = System.currentTimeMillis() + duration * 50L;
    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, (task) -> {
      World world = player.getWorld();
      world.spawnParticle(Particle.REDSTONE, player.getLocation(), 3, options);
      world.spawnParticle(Particle.BLOCK_CRACK, player.getLocation(), 6, Bukkit.createBlockData(
          Material.REDSTONE_BLOCK));
      //player.sendHurtAnimation((float) (360 * Math.random() - 180F));

      if (System.currentTimeMillis() >= end) {
        task.cancel();
      }
    }, 0, 2);

  }


}
