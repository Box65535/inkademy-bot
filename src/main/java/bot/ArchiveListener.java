package bot;

import archive.ArchiveWriter;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.MessageDeleteEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.io.IOException;

public class ArchiveListener implements IListener<MessageReceivedEvent> {
    
    private IChannel channel;
    private ArchiveWriter writer;
    
    private ArchiveListener() {}
    
    public static ArchiveListener createListener(IChannel channel, ArchiveWriter writer) {
        ArchiveListener listener = new ArchiveListener();
        listener.channel = channel;
        listener.writer = writer;
        return listener;
    }
    
    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        
        try {
            if (messageReceivedEvent.getMessage().getChannel().equals(channel))
                writer.write(messageReceivedEvent.getMessage());
        }
        catch (IOException e) {
            System.err.println("Exception when opening " + writer.getFileName());
            System.err.println(e.getMessage());
        }
    }
}
