package dev.sgffa.pvp;

import dev.sgffa.api.economy.EconomyApi;
import dev.sgffa.pvp.listener.BrokenLegFeature;
import dev.sgffa.pvp.listener.HigherJumpFeature;
import dev.sgffa.pvp.listener.LoseMoneyFeature;
import org.bukkit.plugin.java.JavaPlugin;

public class PvPPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    EconomyApi load = getServer().getServicesManager().load(EconomyApi.class);
    getServer().getPluginManager().registerEvents(new BrokenLegFeature(this), this);
    getServer().getPluginManager().registerEvents(new LoseMoneyFeature(this, load), this);
    getServer().getPluginManager().registerEvents(new HigherJumpFeature(this), this);
  }
}
