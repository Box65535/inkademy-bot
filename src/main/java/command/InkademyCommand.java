package command;

import box.discord.command.Command;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

public interface InkademyCommand extends Command {
    
    @Override
    default boolean isCommand(MessageReceivedEvent event) {
        return isInkademy(event) && isVoiced(event) && matchesCommand(event);
    }
    
    boolean matchesCommand(MessageReceivedEvent event);

    static boolean isInkademy(MessageReceivedEvent event) {
        return event.getMessage().getGuild().getName().equals("The Inkademy");
    }
    
    static boolean isVoiced(MessageReceivedEvent event) {
        
        IGuild guild = event.getMessage().getGuild();
        for (IRole role : event.getMessage().getAuthor().getRolesForGuild(guild)) {
            if (role.getName().equals("voiced")) 
                return true;
        }
        return false;
    }
}
