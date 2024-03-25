package com.neoxygen.neokits.cooldowns;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CooldownManager {
    private static PlayersContainer playersContainer;
    public static PlayersContainer getPlayersContainer() {
        return playersContainer;
    }
    public static void setPlayersContainer(PlayersContainer playersContainer) {
        CooldownManager.playersContainer = playersContainer;
    }

    public static void saveCooldowns(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.writeString(Paths.get("config/cooldowns.json"), gson.toJson(playersContainer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadCooldowns(){
        try{
            Gson gson = new Gson();
            String jsonString = new String(Files.readAllBytes(Paths.get("config/cooldowns.json")));
            playersContainer = gson.fromJson(jsonString, PlayersContainer.class);
            if (playersContainer == null){
                playersContainer = new PlayersContainer();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setCooldown(String playerName, String kitname, long cooldownInSeconds)
    {
        Optional<Player> playerOptional = playersContainer.getPlayers().stream()
                .filter(player -> player.getName().equals(playerName)).findFirst();
        long cooldownEnd = getCurrentTime() + cooldownInSeconds;
        if (playerOptional.isPresent()){
            Player player = playerOptional.get();
        }
    }
    public static void updateOrAddCooldown(String playerName, String kitName, long cooldown){
        Optional<Player> playerOpt = playersContainer.getPlayers().stream()
                .filter(player -> player.getName().equals(playerName))
                .findFirst();
        long cooldownEnd = getCurrentTime() + cooldown;
        if (playerOpt.isPresent()) {
            // Если игрок найден, обновляем или добавляем кулдаун
            Player player = playerOpt.get();
            updateOrAddKitCooldown(player, kitName, (int) cooldownEnd);
        } else {
            // Если игрок не найден, создаем нового игрока с кулдауном
            Player newPlayer = new Player();
            newPlayer.setName(playerName);
            List<KitsCooldown> cooldownList = new ArrayList<>();
            cooldownList.add(new KitsCooldown(kitName, cooldownEnd));
            newPlayer.setKitsCooldown(cooldownList);
            playersContainer.getPlayers().add(newPlayer);
        }
    }
    private static void updateOrAddKitCooldown(Player player, String kitName, int cooldownEnd) {
        Optional<KitsCooldown> cooldownOpt = player.getKitsCooldown().stream()
                .filter(cooldown -> cooldown.getName().equals(kitName))
                .findFirst();
        if (cooldownOpt.isPresent()) {
            // Обновление существующего кулдауна
            cooldownOpt.get().setCooldownEnd(cooldownEnd);
        } else {
            // Добавление нового кулдауна
            player.getKitsCooldown().add(new KitsCooldown(kitName, cooldownEnd));
        }
    }
    public static long getRemainCooldwon(String playerName, String kitName){
        for (Player player : CooldownManager.playersContainer.getPlayers()){
            if (player.getName().equals(playerName)){
                Optional<KitsCooldown> cooldownOptional = player.getKitsCooldown().stream()
                        .filter(cooldown1 -> cooldown1.getName().equals(kitName)).findFirst();
                if (cooldownOptional.isPresent()){
                    return cooldownOptional.get().getCooldownEnd() - getCurrentTime();
                }
                else
                    return -1;
            }
        }
        return -1;
    }
    public static boolean isOnCooldown(String playerName, String kitName){
        Optional<Player> playerOptional = playersContainer.getPlayers().stream()
                .filter(player -> player.getName().equals(playerName)).findFirst();
        if (playerOptional.isPresent()){
            Player player = playerOptional.get();
            return player.getKitsCooldown().stream()
                    .anyMatch(cooldown -> cooldown.getName().equals(kitName) && cooldown.getCooldownEnd() > getCurrentTime());
        }
        return false;
    }
    private static int getCurrentTime(){
        return (int) (System.currentTimeMillis()/1000);
    }
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event){
        loadCooldowns();
    }
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event){
        saveCooldowns();
    }
}
