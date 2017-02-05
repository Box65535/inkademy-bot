package bot;

import box.discord.command.Command;
import box.discord.command.CommandListener;
import box.discord.coordinate.BotCoordinator;
import box.discord.result.Result;
import box.discord.result.Option;
import command.*;
import box.discord.client.SynchronizedMessenger;
import listen.ArchiveListener;
import box.discord.client.Messenger;
import model.InkademyModel;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InkademyCoordinator implements BotCoordinator {
    
    private InkademyModel model;
    private EventDispatcher dispatcher;
    private Map<IChannel, ArchiveListener> listeners;
    
    public static final String INKADEMY_GUILD_ID = "233081202997854208";
    public static final String INKADEMY_BOOT_CHANNEL_ID = "233105383907000320";
    
    public InkademyCoordinator(InkademyModel model) {
        this.model = model;
        this.listeners = new HashMap<>();
    }
    
    public void begin(IDiscordClient client) {

        this.dispatcher = client.getDispatcher();
        
        IGuild inkademy = client.getGuildByID(INKADEMY_GUILD_ID);
        IChannel bootChannel = inkademy.getChannelByID(INKADEMY_BOOT_CHANNEL_ID);
        
        Messenger messenger = new SynchronizedMessenger(client);
        
        Set<Command> commands = new HashSet<>();
        commands.add(new CreateCommand(this, messenger));
        commands.add(new FinishCommand(this, model, messenger));
        commands.add(new ListCommand(model, messenger));
        commands.add(new ArchiveCommand(model, messenger));
        commands.add(new QueryCommand(model, messenger));
        commands.add(new ReplayCommand(this, model, messenger));
        commands.add(new HelpCommand(messenger));
        commands.add(new ShutdownCommand(this, messenger));
        CommandListener commandListener = new CommandListener(commands);
        
        try {
            messenger.sendMessage(bootChannel, "Receiving Inkademy Guild");

            Option<Set<String>> channels = model.getActiveChannels();
            messenger.sendMessage(bootChannel, "Receiving Channels");
            if (channels.isFailure()) {
                messenger.sendMessage(bootChannel, "Cannot connect with Database to find Channels");
                channels.throwException();
            } else if (channels.get().isEmpty()) {
                messenger.sendMessage(bootChannel, "No channels currently under active archive");
            } else {
                for (String channelId : model.getActiveChannels().get()) {
                    IChannel channel = inkademy.getChannelByID(channelId);
                    listeners.put(channel, new ArchiveListener(model, channel, dispatcher));
                    messenger.sendMessage(bootChannel, "Listening to channel " + channel.getName());
                }
            }

            messenger.sendMessage(bootChannel, "Confirming Illuminati");
            if (!client.getOurUser().getRolesForGuild(inkademy).stream().anyMatch(r -> r.getName().equals("illuminati")))
                messenger.sendMessage(bootChannel, "Check my server roles");
            
            messenger.sendMessage(bootChannel, "Launching Dispatchers");
            dispatcher.unregisterListener(this);

            for (ArchiveListener listener : listeners.values())
                listener.listen();
            
            dispatcher.registerListener(commandListener);
            
            messenger.sendMessage(bootChannel, "Inkademy Bot 1.0 Fully Online");
        }
        catch (Exception e) {
            e.printStackTrace();
            messenger.sendMessage(bootChannel, "Fatal error on startup, Exiting");
            System.exit(1);
        }
    }
    
    public void end(IDiscordClient client) {
        for (ArchiveListener listener : listeners.values())
            listener.unlisten();
        System.exit(0);
    }

    public void listenToChannel(IChannel channel) {
        model.addToActiveChannels(channel);
        ArchiveListener listener = new ArchiveListener(model, channel, dispatcher);
        listeners.put(channel, listener);
        listener.listen();
    }

    public void unlistenToChannel(IChannel channel) {
        model.removeFromActiveChannels(channel);
        listeners.remove(channel).unlisten();
    }
}
