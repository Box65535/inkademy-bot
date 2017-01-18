package command;

import discord.Messenger;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.util.List;

public class HelpCommand implements Command {
    
    private Messenger messenger;
    
    public HelpCommand(Messenger messenger) {
        this.messenger = messenger;
    }
    
    @Override
    public boolean isCommand(MessageReceivedEvent event) {
        return Command.isFirstToken(event, "!help");
    }
    
    @Override
    public void performCommand(MessageReceivedEvent event) {

        String message = new StringBuilder()
                .append("Inkademy Bot Usage:\n")
                .append("  !help                  Display usage\n")
                .append("  !create channel        Create a new archivable channel\n")
                .append("  !finish                Close and archive this channel\n")
                .append("  !archive channel       Retrieve the archive file for a channel\n")
                .append("  !list                  List files in the archive\n")
                .toString();
        
        messenger.sendQuoteMessage(event.getMessage().getChannel(), message);
    }
}
