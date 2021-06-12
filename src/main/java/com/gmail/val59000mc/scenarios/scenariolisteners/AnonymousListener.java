package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.events.PlayerStartsPlayingEvent;
import com.gmail.val59000mc.events.UhcGameStateChangedEvent;
import com.gmail.val59000mc.events.UhcPlayerStateChangedEvent;
import com.gmail.val59000mc.players.PlayerState;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.scenarios.Scenario;
import com.gmail.val59000mc.scenarios.ScenarioListener;
import com.gmail.val59000mc.utils.ProtocolUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

public class AnonymousListener extends ScenarioListener{

    @Override
    public void onEnable(){
        if (!getConfiguration().getProtocolLibLoaded()){
            Bukkit.broadcastMessage(ChatColor.RED + "[UhcCore] For Anonymous ProtocolLib needs to be installed!");
            getScenarioManager().removeScenario(Scenario.ANONYMOUS);
            return;
        }

        for (UhcPlayer uhcPlayer : getPlayersManager().getAllPlayingPlayers()){
            ProtocolUtils.setPlayerNickName(uhcPlayer, getPlayerNickName(uhcPlayer.getName()));
            getScoreboardManager().updatePlayerTab(uhcPlayer);
        }
    }

    @Override
    public void onDisable(){
        if (!getConfiguration().getProtocolLibLoaded()){
            return; // Never enabled so don't disable.
        }

        for (UhcPlayer uhcPlayer : getPlayersManager().getAllPlayingPlayers()){
            ProtocolUtils.setPlayerNickName(uhcPlayer, null);
            getScoreboardManager().updatePlayerTab(uhcPlayer);
        }
    }

    @EventHandler
    public void onGameStarted(PlayerStartsPlayingEvent e){
        UhcPlayer uhcPlayer = e.getUhcPlayer();

        ProtocolUtils.setPlayerNickName(uhcPlayer, getPlayerNickName(uhcPlayer.getName()));
        getScoreboardManager().updatePlayerTab(uhcPlayer);
    }

    @EventHandler
    public void onGameStateChanged(UhcGameStateChangedEvent e){
        for (UhcPlayer uhcPlayer : getPlayersManager().getPlayersList()){
            if (uhcPlayer.hasNickName()) {
                ProtocolUtils.setPlayerNickName(uhcPlayer, null);
                getScoreboardManager().updatePlayerTab(uhcPlayer);
            }
        }
    }

    @EventHandler
    public void onUhcPlayerStateChange(UhcPlayerStateChangedEvent e){
        if (e.getNewPlayerState() == PlayerState.DEAD){
            UhcPlayer player = e.getPlayer();

            // clear nick
            ProtocolUtils.setPlayerNickName(player, null);
            getScoreboardManager().updatePlayerTab(player);
        }
    }

    private String getPlayerNickName(String name){
        if (name.length() > 8){
            name = name.substring(0, 8);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\uF800");
        sb.append("§k");
        sb.append(name.toUpperCase().replace('I', '='));

        while (sb.length() < 13){
            sb.append("A");
        }
        sb.append("§r");

        sb.append("\uF820");
        return sb.toString();
    }

}