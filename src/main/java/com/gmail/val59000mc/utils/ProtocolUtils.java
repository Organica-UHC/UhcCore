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

                    String value = "ewogICJ0aW1lc3RhbXAiIDogMTYxODQwOTM1NDA1OCwKICAicHJvZmlsZUlkIiA6ICI5NTE0MjkzYTI4NzM0MWYwYmIyZDg3NWI3N2Q4NmM1MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6ZXJleCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MjlhYmQ5MTY0MzAzYjJjZTIwNmYwZGY0YjAyNTg2OTBkOWFiYjRlMzZiYTk2OGZiNWFjZmVjNWMwZmQ3YzUwIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=";
                    String signature = "MVj9BiEEs2FiivQn73DOFS+D9HNRyggqUucSf0R1WOzXi9+tSsLvNjOGhqJepGJOuG52T9nuk5QfoExeOXgGzN8NgAnGLXA4Tr9Lpckh38UeUUhK4E3Ggcw7mHBLtAUyHUAr88+f8jjkVE0Tw8qLb/bCpWTcH2BlbGXxKbPaz4gXhrWFNxTXvsa5+QnOqb9w1Lxx1eSrkVQoSVrhDXE0lHah3bCoA8HPdVtDK46XntLy2x5Ya0EVeK0Y990LPzxOUEtVnjUJO53Uy4n09FXpDdMrdDY8w9OLC546RR8nwQO9617MBRVgUX5OuZYNMrkr2BWjCRBOe3CKDr48wdt3DH7mpH/oUs2CwRMUssvgCg+ys27Wb4pp7pH3Bu54sLp2Xt7fVR2eKjCZiH0HtTjCk4fk12o+DC2gRXSOPX19ow/p+bPgVQScBH271irGL/V+tuUKd5xe55qnSXpMruObWK8q4w6iTz6svgXPAIy0SVEWcBgXeLMFxqV2+oKGfY+i1Z3LmmenLcz4tH+5hN+85KVthJwsbR5mzSBkr+UDx7AXfGaoXlm3giw+l834UjtU3UYUSSsvyJhrZi7Tt6fHnwVGC+T6tuoOha/peEUFNIBe03KPMavTsykux5Itwd8kXb9BUw+uFHao+2fmwy5mBmcHDW3GOanBWMs3+rljcNQ=";
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