package listen;

import model.InkademyModel;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageDeleteEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.MessageUpdateEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Set;

public class ArchiveListener {
    
    private InkademyModel model;
    private IChannel channel;
    private EventDispatcher dispatcher;
    private MessageReceivedListener receivedListener;
    private MessageUpdatedListener updatedListener;
    private MessageDeletedListener deletedListener;
    
    public ArchiveListener(InkademyModel model, IChannel channel, EventDispatcher dispatcher) {
        this.model = model;
        this.channel = channel;
        this.dispatcher = dispatcher;
        this.receivedListener = new MessageReceivedListener();
        this.updatedListener = new MessageUpdatedListener();
        this.deletedListener = new MessageDeletedListener();
    }
    
    public void listen() {
        dispatcher.registerListener(receivedListener);
        dispatcher.registerListener(updatedListener);
        dispatcher.registerListener(deletedListener);
    }
    
    public void unlisten() {
        dispatcher.unregisterListener(receivedListener);
        dispatcher.unregisterListener(updatedListener);
        dispatcher.unregisterListener(deletedListener);
    }
    
    private class MessageReceivedListener implements IListener<MessageReceivedEvent> {
        @Override
        public void handle(MessageReceivedEvent event) {
            if (event.getMessage().getChannel().equals(channel))
                model.putMessage(event.getMessage());
        }
    }
    
    private class MessageUpdatedListener implements IListener<MessageUpdateEvent> {
        @Override
        public void handle(MessageUpdateEvent event) {
            if (event.getNewMessage().getChannel().equals(channel)) {
                model.deleteMessage(event.getOldMessage());
                model.putMessage(event.getNewMessage());
            }
        }
    }

    private class MessageDeletedListener implements IListener<MessageDeleteEvent> {
        @Override
        public void handle(MessageDeleteEvent event) {
            if (event.getMessage().getChannel().equals(channel))
                model.deleteMessage(event.getMessage());
        }
    }
}
