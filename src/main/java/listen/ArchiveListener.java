package listen;

import bot.InkademyBot;
import model.InkademyModel;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

public class ArchiveListener implements IListener<MessageReceivedEvent> {
    
    private InkademyModel model;
    private IChannel channel;
    
    public ArchiveListener(InkademyModel model, IChannel channel) {
        this.model = model;
        this.channel = channel;
    }
    
    @Override
    public void handle(MessageReceivedEvent event) {
        if (event.getMessage().getChannel().equals(channel)) {
            String archiveName = event.getMessage().getChannel().getName();
            model.archive(event.getMessage(), archiveName);
        }
    }
}
