package academy.hekiyou.tenkore.highlighter;

import academy.hekiyou.door.FrontDoor;
import academy.hekiyou.tenkore.plugin.TenkorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class HighlighterPlugin extends TenkorePlugin implements Listener {
    
    private static final int MAX_SIZE = 50;
    private static Path dataFolder;
    private static Map<UUID, HLConfig> playerConfigs = new LinkedHashMap<UUID, HLConfig>(){
        
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest){
            if(size() > MAX_SIZE){
                ((HLConfig)eldest.getValue()).save();
                return true;
            } else {
                return false;
            }
        }
        
    };
    
    @Override
    public void enable(){
        dataFolder = getStorageDir();
        
        try {
            if(!Files.exists(dataFolder))
                Files.createDirectory(dataFolder);
        } catch (IOException exc){
            throw new RuntimeException(exc);
        }
    
        FrontDoor.load(ConfigInterface.class);
        Bukkit.getPluginManager().registerEvents(this, (JavaPlugin)getCore());
    }
    
    @Override
    public void disable(){
        for(HLConfig config : playerConfigs.values())
            config.save();
        playerConfigs.clear();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST) // last person to receive events
    public void catchChatEvent(AsyncPlayerChatEvent event){
        String message = event.getMessage();
        
        for(Player player : event.getRecipients()){
            if(player == null)
                continue;
            
            String toSend = message;
            ChatColor lastColor = getLastChatColor(toSend);
            HLConfig config = HighlighterPlugin.getConfiguration(player.getUniqueId());
            
            for(String word : config.getWords())
                toSend = toSend.replaceAll("\\b(?i)(" + word + ")\\b", config.getColor() + "$1" + lastColor);
            
            player.sendMessage(String.format(event.getFormat(), event.getPlayer().getDisplayName(), toSend));
        }
        event.getRecipients().clear();
    }
    
    public static HLConfig getConfiguration(UUID uuid){
        HLConfig configuration = playerConfigs.get(uuid);
        if(configuration == null){
            Path file = Paths.get(dataFolder.toString(), uuid.toString() + ".conf");
            configuration = new HLConfig(file);
            if(Files.exists(file))
                configuration.load();
            playerConfigs.put(uuid, configuration);
        }
        return configuration;
    }
    
    public static ChatColor getLastChatColor(String message){
        int lastColorChar = message.lastIndexOf(ChatColor.COLOR_CHAR);
        if(lastColorChar == -1) return ChatColor.RESET;
        char color = message.charAt(lastColorChar + 1);
        return ChatColor.getByChar(color);
    }
    
}
