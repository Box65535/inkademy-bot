package listen;

import command.Command;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

import java.util.Set;

public class CommandListener implements IListener<MessageReceivedEvent> {
    
    private Set<Command> commands;
    
    public CommandListener(Set<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        
        if (!isInkademy(event))
            return;
        if (!isVoiced(event))
            return;
        
        for (Command command : commands) {
            if (command.isCommand(event))
                command.performCommand(event);
        }
    }

    private static boolean isInkademy(MessageReceivedEvent event) {
        return event.getMessage().getGuild().getName().equals("The Inkademy");
    }

    private static boolean isVoiced(MessageReceivedEvent event) {

        IGuild guild = event.getMessage().getGuild();
        for (IRole role : event.getMessage().getAuthor().getRolesForGuild(guild)) {
            if (role.getName().equals("voiced"))
                return true;
        }
        return false;
    }
}
