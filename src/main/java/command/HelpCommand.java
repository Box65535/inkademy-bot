package command;

import box.discord.client.Messenger;
import box.discord.command.Command;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.util.List;

public class HelpCommand implements InkademyCommand {
    
    private Messenger messenger;
    
    public HelpCommand(Messenger messenger) {
        this.messenger = messenger;
    }
    
    @Override
    public boolean matchesCommand(MessageReceivedEvent event) {
        return Command.isFirstToken(event, "!help");
    }
    
    @Override
    public void performCommand(MessageReceivedEvent event) {

        String message = new StringBuilder()
                .append("Inkademy Bot Usage:\n")
                .append(" !help                        Display usage\n")
                .append(" !create [TOPIC]              Create a new channel with archiving\n")
                .append(" !finish                      Close this channel\n")
                .append(" !archive [TOPIC]             Retrieve the archive file for topic\n")
                .append(" !query [TOPIC] [PATTERN]     Search archives for content pattern\n")
                .append(" !replay [TOPIC]              Replay channel from archive\n")
                .append(" !list                        List files in the archive\n")
                .toString();
        
        messenger.sendQuoteMessage(event.getMessage().getChannel(), message);
    }
}
