package archive;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.File;
import java.io.FileNotFoundException;

public class ArchiveReader {
    
    public static boolean archiveExists(String path) {
        File archive = new File(path);
        return archive.canRead();
    }
    
    public static synchronized void uploadArchive(IChannel channel, String path)
            throws MissingPermissionsException, FileNotFoundException, DiscordException, RateLimitException {
        channel.sendFile(new File(path));
    }
}
