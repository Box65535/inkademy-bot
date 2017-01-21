package command;

import bot.InkademyBot;
import box.discord.command.Command;
import box.discord.client.Messenger;
import model.InkademyModel;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.util.List;

public class FinishCommand implements InkademyCommand {
    
    private InkademyBot bot;
    private InkademyModel model;
    private Messenger messenger;
    
    public FinishCommand(InkademyBot bot, InkademyModel model, Messenger messenger) {
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
        
        if (!model.getArchivedChannels().contains(channel)) {
            messenger.sendMessage(channel, "This is not an archived channel");
            messenger.sendMessage(channel, "Only use !finish in channels created by me");
            return;
        }
        
        if (!messenger.removeChannel(channel).isSuccess()) {
            messenger.sendMessage(channel, "Could not create channel " + channel.getName());
            messenger.sendMessage(channel, "Check my server permissions");
            return;
        }
        
        bot.unlistenToChannel(channel);
    }
}
