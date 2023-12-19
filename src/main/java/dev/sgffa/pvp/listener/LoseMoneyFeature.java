package dev.sgffa.pvp.listener;

import dev.sgffa.api.economy.EconomyApi;
import java.util.List;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public class LoseMoneyFeature implements Listener {

  private final List<EntityDamageEvent.DamageCause> causes =
      List.of(EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.FREEZE,
          EntityDamageEvent.DamageCause.DROWNING, EntityDamageEvent.DamageCause.PROJECTILE,
          EntityDamageEvent.DamageCause.ENTITY_ATTACK,
          EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
          EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK, EntityDamageEvent.DamageCause.POISON);

  private final JavaPlugin plugin;
  private final EconomyApi economyApi;

  @EventHandler
  public void onHurt(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    double random = Math.random();

    if (random > 0.5) {
      return;
    }

    if (event.getFinalDamage() <= 3) {
      return;
    }

    if (!causes.contains(event.getCause())) {
      return;
    }

    economyApi.getBalance(player.getUniqueId()).thenApply(balance -> Math.max(balance, 0))
        .thenApply(balance -> balance * 0.001D * Math.random()).whenComplete((amount, err) -> {
          economyApi.addBalance(player.getUniqueId(), -amount).thenRunAsync(() -> {
            ItemStack itemStack = new ItemStack(Material.GOLD_NUGGET);
            itemStack.setAmount(amount.intValue());
            World world = player.getWorld();
            world.dropItemNaturally(player.getLocation(), itemStack, item -> {
              item.setPickupDelay(20 * 3);
              item.customName(Component.text(economyApi.format(amount)).color(NamedTextColor.GOLD));
              item.setCustomNameVisible(true);
              item.setCanMobPickup(false);
              item.setMetadata("coin-item", new FixedMetadataValue(plugin, amount));
            });
          }, runnable -> Bukkit.getScheduler().runTask(plugin, runnable));
        });


  }

  @EventHandler
  public void onItemMerge(ItemMergeEvent event) {
    Item entity = event.getEntity();
    if (!entity.hasMetadata("coin-item")) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onPickUp(EntityPickupItemEvent event) {
    Item item = event.getItem();
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    event.setCancelled(true);
    player.playPickupItemAnimation(item);
    item.remove();

    item.getMetadata("coin-item").stream().findFirst().ifPresent(metadataValue -> {
      player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.8F);
      double amount = metadataValue.asDouble();
      economyApi.addBalance(player.getUniqueId(), amount);
    });
  }
}
