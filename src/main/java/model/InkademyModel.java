package model;

import box.discord.result.Option;
import box.discord.result.Result;
import data.ArchiveMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

public interface InkademyModel {
    
    Result addToActiveChannels(IChannel channel);
    
    Result removeFromActiveChannels(IChannel channel);

    Option<Set<String>> getActiveChannels();

    Option<Set<String>> getAllArchivedTopics();
    
    Result putMessage(IMessage message);
    
    Result updateMessage(IMessage message);

    Result deleteMessage(IMessage message);

    Option<Queue<ArchiveMessage>> getFullArchive(String topic);

    Option<Queue<ArchiveMessage>> queryArchive(String pattern);
    
    Option<Queue<ArchiveMessage>> queryArchive(String topic, String pattern);
}
