package command;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

public interface CommandHandler {

    void handle(MessageReceivedEvent event);
}
