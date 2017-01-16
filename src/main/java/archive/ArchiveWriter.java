package archive;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ArchiveWriter {
    
    private String fileName;
    
    private ArchiveWriter() {}
    
    public static ArchiveWriter createWriter(String fileName) {
        ArchiveWriter writer = new ArchiveWriter();
        writer.fileName = fileName;
        return writer;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public synchronized void write(IMessage message) throws IOException {
        
        File file = new File(fileName);
        if (!file.exists()) 
            file.createNewFile();
        
        try (FileWriter writer = new FileWriter(file.getAbsoluteFile(), true);) {
            String formatted = formatMessage(message);
            writer.write(formatted);
        }
        catch (IOException e) {
            System.err.println("Exception when writing to " + fileName);
            System.err.println(e.getMessage());
        }
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
