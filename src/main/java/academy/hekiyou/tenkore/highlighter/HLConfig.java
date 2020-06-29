package academy.hekiyou.tenkore.highlighter;

import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *  Represents a player's highlight configuration
 *
 *  The format is as follows:
 *
 *  single-char-for-chat-color
 *  word1
 *  word2
 *  word3
 */
public class HLConfig {
 
    private static final Logger LOGGER = Logger.getLogger("Highlighter");
    private final Path pathLoc;
    private ChatColor highlightColor;
    private Pattern pattern;
    private List<String> words = new ArrayList<>();
    
    public HLConfig(Path path){
        this.pathLoc = path;
    }
    
    public void load(){
        try (BufferedReader reader = Files.newBufferedReader(pathLoc)) {
            String line = reader.readLine();
            if(line == null || line.length() <= 0){
                highlightColor = ChatColor.YELLOW;
                LOGGER.warning(pathLoc.getFileName() + " had bad formatting; using default yellow and no words.");
                return;
            }
            
            highlightColor = ChatColor.getByChar(line);
            
            while((line = reader.readLine()) != null)
                if(!line.isEmpty())
                    words.add(line);
        } catch (IOException exc){
            LOGGER.log(Level.SEVERE, "Failed to load " + pathLoc.getFileName(), exc);
        }
    }
    
    public void save(){
        try(BufferedWriter writer = Files.newBufferedWriter(pathLoc, StandardOpenOption.CREATE)){
            writer.write(highlightColor.getChar());
            writer.newLine();
            for(String word : words){
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException exc){
            LOGGER.log(Level.SEVERE, "Failed to save " + pathLoc.getFileName(), exc);
        }
    }
    
    public ChatColor getHighlightColor(){
        return highlightColor;
    }
    
    public void setHighlightColor(ChatColor color){
        this.highlightColor = color;
    }
    
    public List<String> getWords(){
        return words;
    }
    
    public Pattern getPattern(){
        return pattern;
    }
    
    public boolean hasPattern(){
        return pattern != null;
    }
    
    public boolean addWord(String word){
        words.add(word.toLowerCase());
        rebuildRegex();
        return true;
    }
    
    public boolean removeWord(String word){
        word = word.toLowerCase();
        boolean success = words.remove(word);
        rebuildRegex();
        return success;
    }
    
    private void rebuildRegex(){
        if(words.isEmpty()){
            pattern = null;
        } else {
            pattern = Pattern.compile("\\b(?i)(" + String.join("|", words) + ")\\b");
        }
    }
    
}
