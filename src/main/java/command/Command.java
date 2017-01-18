package command;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public interface Command {
    
    boolean isCommand(MessageReceivedEvent event);
    
    void performCommand(MessageReceivedEvent event);

    static List<String> tokenize(String message) {
        return Arrays.asList(message.split("\\s+"));
    }
    
    static boolean isFirstToken(MessageReceivedEvent event, String token) {
        List<String> command = Command.tokenize(event.getMessage().getContent());
        if (command.size() > 0)
            return command.get(0).equals(token);
        else
            return false;
    }
}
