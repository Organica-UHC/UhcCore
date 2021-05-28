package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PermaKillListener extends ScenarioListener{

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        World world = Bukkit.getWorld(getGameManager().getConfiguration().getOverworldUuid());
        if (world != null) world.setTime(world.getTime() + 12000);
    }

}