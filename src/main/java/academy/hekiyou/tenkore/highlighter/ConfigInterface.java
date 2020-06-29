package academy.hekiyou.tenkore.highlighter;

import academy.hekiyou.door.annotations.BranchingCommand;
import academy.hekiyou.door.annotations.GlobAll;
import academy.hekiyou.door.annotations.Module;
import academy.hekiyou.door.annotations.RegisterCommand;
import academy.hekiyou.door.model.Invoker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Module
public class ConfigInterface {
    
    @RegisterCommand(
            permission = "highlighter.use",
            description = "Modifies a players given highlighting configuration",
            alias = "hl"
    )
    @BranchingCommand
    public void highlighter(Invoker invoker){}
    
    public void highlighter$add(Invoker invoker, @GlobAll String words){
        HLConfig config = HighlighterPlugin.getConfiguration(invoker.as(Player.class).getUniqueId());
        for(String word : words.split(" ")){
            if(config.addWord(word)){
                invoker.sendMessage(ChatColor.GREEN + "Added word \"%s\"", word);
            } else {
                invoker.sendMessage(ChatColor.RED + "\"%s\" was already being highlighted", word);
            }
        }
        config.save();
    }
    
    public void highlighter$remove(Invoker invoker, @GlobAll String words){
        HLConfig config = HighlighterPlugin.getConfiguration(invoker.as(Player.class).getUniqueId());
        for(String word : words.split(" ")){
            if(config.removeWord(word)){
                invoker.sendMessage(ChatColor.GREEN + "Removed word \"%s\"", word);
            } else {
                invoker.sendMessage(ChatColor.RED + "\"%s\" was not being highlighted", word);
            }
        }
        config.save();
    }
    
    public void highlighter$list(Invoker invoker){
        HLConfig config = HighlighterPlugin.getConfiguration(invoker.as(Player.class).getUniqueId());
        invoker.sendMessage(ChatColor.GREEN + "Currently highlighting:");
        for(String word : config.getWords())
            invoker.sendMessage(ChatColor.GREEN + "- " + word);
        config.save();
    }
    
    public void highlighter$setcolor(Invoker invoker, String color){
        HLConfig config = HighlighterPlugin.getConfiguration(invoker.as(Player.class).getUniqueId());
        ChatColor newColor = ChatColor.getByChar(color.charAt(0));
        if(newColor != null){
            config.setHighlightColor(newColor);
            invoker.sendMessage(ChatColor.GREEN + "Set your highlighting color to " + (newColor + newColor.name()));
        } else {
            invoker.sendMessage(ChatColor.RED + "No such chat color by that character.");
        }
    }
    
}
