package bot;

import command.CommandHandler;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

public class CommandListener implements IListener<MessageReceivedEvent> {

    private CommandHandler handler;

    private CommandListener() {}

    public static CommandListener createListener(CommandHandler handler) {
        CommandListener listener = new CommandListener();
        listener.handler = handler;
        return listener;
    }
    
    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        handler.handle(messageReceivedEvent);
    }
}
