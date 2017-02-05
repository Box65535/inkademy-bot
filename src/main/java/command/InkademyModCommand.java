package command;

import bot.InkademyCoordinator;
import box.discord.command.Command;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

public interface InkademyModCommand extends Command {

    @Override
    default boolean isCommand(MessageReceivedEvent event) {
        return isInkademy(event) && isMod(event) && matchesCommand(event);
    }

    boolean matchesCommand(MessageReceivedEvent event);

    static boolean isInkademy(MessageReceivedEvent event) {
        return event.getMessage().getGuild().getID().equals(InkademyCoordinator.INKADEMY_GUILD_ID);
    }

    static boolean isMod(MessageReceivedEvent event) {

        IGuild guild = event.getMessage().getGuild();
        for (IRole role : event.getMessage().getAuthor().getRolesForGuild(guild)) {
            if (role.getName().equals("mod"))
                return true;
        }
        return false;
    }
}
