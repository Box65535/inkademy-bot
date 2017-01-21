package bot;

import box.discord.command.Command;
import box.discord.command.CommandListener;
import command.*;
import box.discord.client.SynchronizedMessenger;
import listen.ArchiveListener;
import box.discord.client.Messenger;
import model.InkademyModel;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InkademyBot {
    
    private IDiscordClient client;
    private InkademyModel model;
    private CommandListener commandListener;
    private Map<IChannel, ArchiveListener> archivedChannels;
    
    public InkademyBot(IDiscordClient client) {
        
        this.client = client;
        model = new InkademyModel();
        
        archivedChannels = new HashMap<>();
        for (IChannel channel : model.getArchivedChannels()) {
            ArchiveListener listener = new ArchiveListener(model, channel);
            archivedChannels.put(channel, listener);
            client.getDispatcher().registerListener(listener);
        }

        Messenger messenger = new SynchronizedMessenger(client);
        Set<Command> commands = new HashSet<>();
        commands.add(new HelpCommand(messenger));
        commands.add(new ListCommand(model, messenger));
        commands.add(new CreateCommand(this, messenger));
        commands.add(new ArchiveCommand(model, messenger));
        commands.add(new FinishCommand(this, model, messenger));
        commandListener = new CommandListener(commands);
        
        client.getDispatcher().registerListener(commandListener);
    }
    
    public void listenToChannel(IChannel channel) {
        model.addToArchived(channel);
        ArchiveListener listener = new ArchiveListener(model, channel);
        archivedChannels.put(channel, listener);
        client.getDispatcher().registerListener(listener);
    }
    
    public void unlistenToChannel(IChannel channel) {
        model.removeFromArchived(channel);
        ArchiveListener listener = archivedChannels.remove(channel);
        client.getDispatcher().unregisterListener(listener);
    }
}
