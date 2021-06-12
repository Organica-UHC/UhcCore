package com.gmail.val59000mc.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ProtocolUtils{

    private static ProtocolUtils protocolUtils;

    private ProtocolUtils(){
        protocolUtils = this;

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(UhcCore.getPlugin(), PacketType.Play.Server.PLAYER_INFO) {

            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacket().getPlayerInfoAction().read(0) != EnumWrappers.PlayerInfoAction.ADD_PLAYER){
                    return;
                }

                List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<>();
                List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
                PlayersManager pm = GameManager.getGameManager().getPlayersManager();

                for (PlayerInfoData playerInfoData : playerInfoDataList) {
                    if (
                            playerInfoData == null ||
                            playerInfoData.getProfile() == null ||
                            Bukkit.getPlayer(playerInfoData.getProfile().getUUID()) == null
                    ){ // Unknown player
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }

                    WrappedGameProfile profile = playerInfoData.getProfile();
                    UhcPlayer uhcPlayer;

                    try {
                        uhcPlayer = pm.getUhcPlayer(profile.getUUID());
                    }catch (UhcPlayerDoesntExistException ex){ // UhcPlayer does not exist
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }

                    // No display-name so don't change player data.
                    if (!uhcPlayer.hasNickName()){
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }

                    // The packet receiver so don't change player data.
                    if (uhcPlayer.getUuid().equals(event.getPlayer().getUniqueId())){
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }

                    profile = profile.withName(uhcPlayer.getName());

                    String value = "ewogICJ0aW1lc3RhbXAiIDogMTYyMzUyODg3NTk4MSwKICAicHJvZmlsZUlkIiA6ICI5ZjI5OTUzNzhmYTk0Y2MzOTk5Zjk0NDM5MzQ4N2MxNCIsCiAgInByb2ZpbGVOYW1lIiA6ICIwQjF0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVhNmMyZTA5MmRhOGI1MjJkNTEwODkzY2Y0Y2E0MDc0ZWI4ZGNkZjM1YjNiNzgzNDUwYWY1NDI0OWI1NTY2ZDkiCiAgICB9CiAgfQp9";
                    String signature = "GjGNKL8K7mgm56+C5kiIDbT5qs17Yl0Fy3eRrDF75HxdG9bldXpFJK4SUJJKCWw+zs+oJe81aZ4pVlQv6cV4PGDf0e1e+D9Rfh64x5KIlJem2KvXVqPZoeXeWLW4JRdAYkYZ920XwmyIePHKZz/AL0+/F1TNDvIMzmBPagBLMe1fzN70+s45/1wxEd+EIW01zm+8phqj03U0IOqDV4Y80BgCevSdpORwNU6/kc4ZpJdYycS0kB4Hdsaz1bg+IfJWDerAF64HOcbPTmlO98R9kjAcSic9acmkR08jinPdB0w3Iqb+dHUVoUJi/mrwpmETb6fwzMvhGimmwssp3Jf0hA7pGgO5mhWpWnzFI611K5Al9v5yqUwt+wd0AEssNH59L7tLkdKq2+l4Tow2J4CXvW4U5Sd8Lr/4btqI14sqW/PR/2/woyaJWYDO7LoVwnlEzuRXEDomjbau1U9T4a6FrHLu67ij5YXmB6v7tKqHwo+LBiliX5AWbgXn9OFUfoTxjQuG+QNp8wfirAMmfdKzoGIMMeAG8CeLzoQLu7k5A3jgkrQH1agZH1ArT9HaK8AW8cv9K2Z2xa62BNx2OFYMzoC8v1xR124SXV8ndNSYRhYHaL4nuV4zVDB2ystKoDINNHae/ttScbVrnr2AsLXBCFortvMpBYOlW5mla8WLDNU=";
                    profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));

                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(profile, playerInfoData.getPing(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());
                    newPlayerInfoDataList.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().write(0, newPlayerInfoDataList);
            }

        });
    }

    public static void register(){
        if (protocolUtils != null){
            ProtocolLibrary.getProtocolManager().removePacketListeners(UhcCore.getPlugin());
            protocolUtils = null;
        }

        new ProtocolUtils();
    }

    /***
     * This method is used to change the player display name using ProtocolLib
     * @param uhcPlayer The player you want to change the display-name for.
     * @param nickName The wanted nick-name, set to null to reset. (Make sure its not over 16 characters long!)
     */
    public static void setPlayerNickName(UhcPlayer uhcPlayer, String nickName){
        uhcPlayer.setNickName(nickName);

        try {
            // Make the player disappear and appear to update their name.
            updatePlayer(uhcPlayer.getPlayer());
        }catch (UhcPlayerNotOnlineException ex){
            // Don't update offline players
        }
    }

    /***
     * This method can be used to change the tab header and footer.
     * @param player The player to change the header / footer for
     * @param header The new header
     * @param footer The new footer
     */
    public static void setPlayerHeaderFooter(Player player, String header, String footer){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(header));
        packet.getChatComponents().write(1, WrappedChatComponent.fromText(footer));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }catch (InvocationTargetException ex){
            ex.printStackTrace();
        }
    }

    private static void updatePlayer(Player player){
        for (Player all : player.getWorld().getPlayers()){
            all.hidePlayer(player);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), () -> {
            for (Player all : player.getWorld().getPlayers()){
                all.showPlayer(player);
            }
        }, 1);
    }

}