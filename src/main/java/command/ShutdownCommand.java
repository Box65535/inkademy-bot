package command;

import bot.InkademyCoordinator;
import box.discord.client.Messenger;
import box.discord.command.Command;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class ShutdownCommand implements InkademyModCommand {
    
    InkademyCoordinator bot;
    Messenger messenger;
    
    public ShutdownCommand(InkademyCoordinator bot, Messenger messenger) {
        this.bot = bot;
        this.messenger = messenger;
    }

    @Override
    public boolean matchesCommand(MessageReceivedEvent event) {
        return Command.isFirstToken(event, "!shutdown");
    }

    @Override
    public void performCommand(MessageReceivedEvent event) {
        messenger.sendMessage(event.getMessage().getChannel(), "Shutting down");
        bot.end(event.getClient());
    }
}
