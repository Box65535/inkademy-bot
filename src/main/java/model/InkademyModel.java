package model;

import command.Command;
import org.apache.commons.io.IOUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InkademyModel {
    
    private Set<IChannel> archivedChannels;

    public InkademyModel() {
        archivedChannels = new HashSet<>();
    }
    
    public void addToArchived(IChannel channel) {
        synchronized (this) {
            archivedChannels.add(channel);
        }
    }
    
    public void removeFromArchived(IChannel channel) {
        synchronized (this) {
            archivedChannels.remove(channel);
        }
    }

    public Set<IChannel> getArchivedChannels() {
        synchronized (this) {
            return archivedChannels;
        }
    }
    
    public boolean archive(IMessage message, String archiveName) {
        synchronized (this) {
            
            String fileName = convertToFileName(archiveName);

            File file = new File(fileName);
            try {
                if (!file.exists())
                    file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            try (FileWriter writer = new FileWriter(file.getAbsoluteFile(), true)) {
                String formatted = formatMessage(message);
                writer.write(formatted);
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            
            return true;
        }
    }

    public File getArchive(String archiveName) {
        synchronized (this) {
            
            String fileName = convertToFileName(archiveName);
            
            File archive = new File(fileName);
            if (!archive.canRead()) {
                System.err.println("Could not read " + archiveName);
                return null;
            }
            
            return archive;
        }
    }

    public List<String> listArchives() {
        synchronized (this) {
            
            String command = "ls archives";
            try {
                Process list = Runtime.getRuntime().exec(command);
                String result = IOUtils.toString(list.getInputStream(), Charset.defaultCharset()).replaceAll("\\.txt", "");
                return Command.tokenize(result);
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    
    private static String convertToFileName(String archiveName) {
        archiveName = archiveName.replaceAll("[^A-Za-z0-9_\\-\\*]", "");
        return new StringBuilder().append("archives/").append(archiveName).append(".txt").toString();
    }

    private static String formatMessage(IMessage message) {

        IGuild guild = message.getGuild();
        return new StringBuilder()
                .append(message.getAuthor().getName())
                .append(" says on [")
                .append(message.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .append("]  ")
                .append(message.getContent())
                .append("\n")
                .toString();
    }
}
