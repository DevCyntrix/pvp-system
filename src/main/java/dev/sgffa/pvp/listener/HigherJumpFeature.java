package dev.sgffa.pvp.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.sgffa.pvp.PvPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class HigherJumpFeature implements Listener {

  private final PvPPlugin plugin;
  private final NamespacedKey sneakTimeKey;
  private final NamespacedKey superJumpTimeKey;

  public HigherJumpFeature(PvPPlugin plugin) {
    this.plugin = plugin;
    sneakTimeKey = NamespacedKey.fromString("sneak", plugin);
    superJumpTimeKey = NamespacedKey.fromString("super-jump", plugin);
  }

  @EventHandler
  public void onToggleSneak(PlayerToggleSneakEvent event) {
    Player player = event.getPlayer();
    if (event.isSneaking()) {
      return;
    }
    player.getPersistentDataContainer()
        .set(sneakTimeKey, PersistentDataType.LONG, System.currentTimeMillis());
  }

  @EventHandler
  public void onJump(PlayerJumpEvent event) {
    Player player = event.getPlayer();
    Long sneakTime = player.getPersistentDataContainer().get(sneakTimeKey, PersistentDataType.LONG);
    if (sneakTime == null) {
      sneakTime = 0L;
    }

    long time = System.currentTimeMillis();

    if (time - sneakTime > 50) {
      return;
    }
    Long superJumpTime =
        player.getPersistentDataContainer().get(superJumpTimeKey, PersistentDataType.LONG);
    if (superJumpTime == null) {
      superJumpTime = 0L;
    }

    if (time - superJumpTime < 3000) {
      return;
    }

    Vector velocity = player.getVelocity();
    Vector multiply = player.getLocation().getDirection().multiply(0.5);
    velocity.add(multiply);
    player.setVelocity(velocity);
    player.setFoodLevel(Math.max(player.getFoodLevel() - 3, 0));
    player.getPersistentDataContainer()
        .set(superJumpTimeKey, PersistentDataType.LONG, System.currentTimeMillis());
    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 5, false, false, false));
    player.setExhaustion(0.0F);
    player.setPose(Pose.CROAKING, true);

    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      player.setPose(Pose.STANDING);
    }, 20);

  }

}
