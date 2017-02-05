package command;

import bot.InkademyCoordinator;
import box.discord.command.Command;
import box.discord.client.Messenger;
import box.discord.result.Option;
import model.InkademyModel;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.util.List;
import java.util.Set;

public class FinishCommand implements InkademyCommand {
    
    private InkademyCoordinator bot;
    private InkademyModel model;
    private Messenger messenger;
    
    public FinishCommand(InkademyCoordinator bot, InkademyModel model, Messenger messenger) {
        this.bot = bot;
        this.model = model;
        this.messenger = messenger;
    }
    
    @Override
    public boolean matchesCommand(MessageReceivedEvent event) {
        return Command.isFirstToken(event, "!finish");
    }

    @Override
    public void performCommand(MessageReceivedEvent event) {

        IChannel channel = event.getMessage().getChannel();
        List<String> tokens = Command.tokenize(event.getMessage().getContent());
        if (tokens.size() != 1) {
            messenger.sendMessage(channel, "Incorrect number of arguments. Try !help.");
            return;
        }

        Option<Set<String>> activeChannels = model.getActiveChannels();
        if (activeChannels.isFailure()) {
            messenger.sendMessage(channel, "Could not connect to database");
            messenger.sendMessage(channel, "Cannot verify that this is a valid channel");
            return;
        }
                
        if (!activeChannels.get().contains(channel.getID())) {
            messenger.sendMessage(channel, "This is not an archived channel");
            messenger.sendMessage(channel, "Only use !finish in channels created by me");
            return;
        }
        
        if (messenger.removeChannel(channel).isFailure()) {
            messenger.sendMessage(channel, "Could not remove channel " + channel.getName());
            messenger.sendMessage(channel, "Check my server permissions");
            return;
        }
        
        bot.unlistenToChannel(channel);
    }
}
