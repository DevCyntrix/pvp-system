package dev.sgffa.pvp.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.sgffa.pvp.PvPPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class HigherJumpFeature implements Listener {

  private final PvPPlugin plugin;
  private final NamespacedKey key;

  public HigherJumpFeature(PvPPlugin plugin) {
    this.plugin = plugin;
    key = NamespacedKey.fromString("sneak", plugin);
  }

  @EventHandler
  public void onToggleSneak(PlayerToggleSneakEvent event) {
    if (event.isSneaking()) {
      return;
    }
    Player player = event.getPlayer();
    player.getPersistentDataContainer()
        .set(key, PersistentDataType.LONG, System.currentTimeMillis());
  }

  @EventHandler
  public void onJump(PlayerJumpEvent event) {
    Player player = event.getPlayer();
    Long sneakTime = player.getPersistentDataContainer().get(key, PersistentDataType.LONG);
    if (sneakTime == null) {
      sneakTime = 0L;
    }

    if (System.currentTimeMillis() - sneakTime > 50) {
      return;
    }
    Vector velocity = player.getVelocity();
    velocity.add(new Vector(0, 0.5, 0));
    player.setVelocity(velocity);
  }

}
