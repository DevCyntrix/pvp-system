package dev.sgffa.pvp.listener;

import dev.sgffa.pvp.PvPPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ArrowBodyCountChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

/**
 * The class implements the feature that you can remove arrows from someone else's body. To avoid
 * a glitch it is necessary to cancel the arrow count change if you used a bow with infinity or
 * creative. The target player has to sneak and takes 1 heart damage.
 */
public class GrabArrowFromPlayerFeature implements Listener {

  private static final String metadataKey = "infinity-arrow-hit";
  private final PvPPlugin plugin;
  private final NamespacedKey arrowGrabTime;

  public GrabArrowFromPlayerFeature(PvPPlugin plugin) {
    this.plugin = plugin;
    this.arrowGrabTime = NamespacedKey.fromString("arrow-grab-time", plugin);
  }

  @EventHandler
  public void onArrowCountChange(ArrowBodyCountChangeEvent event) {
    if (event.isReset()) {
      return;
    }
    LivingEntity entity = event.getEntity();
    if (!entity.hasMetadata(metadataKey)) {
      return;
    }
    entity.removeMetadata(metadataKey, plugin);
    event.setCancelled(true);
  }

  @EventHandler
  public void onArrowCountChane(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof AbstractArrow arrow)) {
      return;
    }
    if (arrow.getPickupStatus() == AbstractArrow.PickupStatus.ALLOWED) {
      return;
    }
    event.getEntity().setMetadata(metadataKey, new FixedMetadataValue(plugin, true));
  }

  @EventHandler(ignoreCancelled = true)
  public void onInteractEntity(PlayerInteractEntityEvent event) {
    if (!(event.getRightClicked() instanceof Player target)) {
      return;
    }
    int arrowsInBody = target.getArrowsInBody();
    if (arrowsInBody <= 0
//        || !target.isSneaking()
    ) {
      return;
    }
    Long lastArrowGrab =
        target.getPersistentDataContainer().get(arrowGrabTime, PersistentDataType.LONG);
    if (lastArrowGrab == null) {
      lastArrowGrab = 0L;
    }

    if (System.currentTimeMillis() - lastArrowGrab <= 1000) {
      return;
    }

    arrowsInBody--;
    target.setArrowsInBody(arrowsInBody);
    Player player = event.getPlayer();
    target.damage(2, player);
    target.getPersistentDataContainer()
        .set(arrowGrabTime, PersistentDataType.LONG, System.currentTimeMillis());

    player.getInventory().addItem(new ItemStack(Material.ARROW));
    event.setCancelled(true);
  }

}
