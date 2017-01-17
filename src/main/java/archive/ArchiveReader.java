package archive;

import org.apache.commons.io.IOUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ArchiveReader {
    
    public static boolean archiveExists(String path) {
        File archive = new File(path);
        return archive.canRead();
    }
    
    public static synchronized void uploadArchive(IChannel channel, String path)
            throws MissingPermissionsException, FileNotFoundException, DiscordException, RateLimitException {
        channel.sendFile(new File(path));
    }
    
    public static synchronized void listArchives(IChannel channel)
        throws MissingPermissionsException, FileNotFoundException, DiscordException, RateLimitException {
        
        String command = "ls archives";

        try {
            Process list = Runtime.getRuntime().exec(command);
            String result = IOUtils.toString(list.getInputStream(), Charset.defaultCharset());
            channel.sendMessage(result.replaceAll("\\.txt", ""));
        }
        catch (IOException e) {
            System.err.println("Container failed to run " + command);
            e.printStackTrace();
        }
    }
    
    public static synchronized void grepArchives(IChannel channel, String pattern, String path)
            throws MissingPermissionsException, FileNotFoundException, DiscordException, RateLimitException {
        
        String command = new StringBuilder()
                .append("grep ")
                .append(pattern)
                .append(" ")
                .append(path)
                .append(" > query.txt")
                .toString();
        
        System.out.println(command);
        
        try {
            Process grep = Runtime.getRuntime().exec(command);
            String error = IOUtils.toString(grep.getErrorStream(), Charset.defaultCharset());
            if (!error.isEmpty())
                channel.sendMessage(error);
            uploadArchive(channel, "query.txt");
        }
        catch (IOException e) {
            System.err.println("Container failed to run " + command);
            e.printStackTrace();
        }
    }
}
