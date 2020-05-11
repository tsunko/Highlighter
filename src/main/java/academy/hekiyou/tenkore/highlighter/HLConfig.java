package academy.hekiyou.tenkore.highlighter;

import org.bukkit.ChatColor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HLConfig {
    
    private final Path file;
    private ChatColor color = ChatColor.YELLOW;
    private List<String> words = new CopyOnWriteArrayList<>();
    
    public HLConfig(Path file){
        this.file = file;
    }
    
    public void load(){
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(file);
            
            color = ChatColor.getByChar(reader.readLine().charAt(0));
            
            String line;
            while((line = reader.readLine()) != null){
                if(line.isEmpty())
                    continue;
                words.add(line);
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if(reader != null)
                    reader.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public void save(){
        BufferedWriter writer = null;
        try {
            if(!Files.exists(file))
                Files.createFile(file);
            writer = Files.newBufferedWriter(file);
            writer.write("" + color.getChar());
            writer.newLine();
            for(String word : words){
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public ChatColor getColor(){
        return color;
    }
    
    public void setColor(ChatColor color){
        this.color = color;
    }
    
    public boolean addWord(String word){
        word = word.toLowerCase();
        if(words.contains(word)) return false;
        words.add(word);
        return true;
    }
    
    public boolean removeWord(String word){
        word = word.toLowerCase();
        return words.remove(word);
    }
    
    public List<String> getWords(){
        return words;
    }
    
}
