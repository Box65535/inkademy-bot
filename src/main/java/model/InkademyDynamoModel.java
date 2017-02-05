package model;

import box.discord.result.Option;
import box.discord.result.Result;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.Select;
import data.ArchiveMessage;
import data.ArchiveTopic;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.util.*;

public class InkademyDynamoModel implements InkademyModel {

    private Table topics;
    private Table messages;
    private DynamoDBMapper mapper;

    public InkademyDynamoModel(AmazonDynamoDBClient client) {
        mapper = new DynamoDBMapper(client);
        topics = new DynamoDB(client).getTable("InkademyTopics");
        messages = new DynamoDB(client).getTable("InkademyMessages");
    }

    public Result addToActiveChannels(IChannel channel) {
        synchronized (topics) {
            try {
                ArchiveTopic topic = new ArchiveTopic(channel, true);
                mapper.save(topic);
                return Result.Success();
            }
            catch (Exception e) {
                e.printStackTrace();
                return Result.Failure(e);
            }
        }
    }

    public Result removeFromActiveChannels(IChannel channel) {
        synchronized (topics) {
            try {
                ArchiveTopic topic = new ArchiveTopic(channel, false);
                mapper.save(topic);
                return Result.Success();
            }
            catch (Exception e) {
                e.printStackTrace();
                return Result.Failure(e);
            }
        }
    }

    public Option<Set<String>> getActiveChannels() {
        synchronized (topics) {
            try {
                Set<String> channelIds = new HashSet<>();
                ScanSpec scanSpec = new ScanSpec()
                        .withScanFilters(new ScanFilter("Active").eq(1))
                        .withSelect(Select.SPECIFIC_ATTRIBUTES)
                        .withAttributesToGet("ChannelId");
                ItemCollection<ScanOutcome> scanResults = topics.scan(scanSpec);
                for (Item item : scanResults)
                    channelIds.add(item.getBigInteger("ChannelId").toString());
                return Result.Success(channelIds);
            }
            catch (Exception e) {
                e.printStackTrace();
                return Result.Failure(e);
            }
        }
    }

    public Option<Set<String>> getAllArchivedTopics() {
        synchronized (topics) {
            try {
                Set<String> topicSet = new HashSet<>();
                ScanSpec scanSpec = new ScanSpec()
                        .withSelect(Select.SPECIFIC_ATTRIBUTES)
                        .withAttributesToGet("Topic");
                ItemCollection<ScanOutcome> scanResults = topics.scan(scanSpec);
                for (Item item : scanResults)
                    topicSet.add(item.getString("Topic").toString());
                return Result.Success(topicSet);
            }
            catch (Exception e) {
                e.printStackTrace();
                return Result.Failure(e);
            }
        }
    }

    public Result putMessage(IMessage message) {
        synchronized (messages) {
            try {
                mapper.save(new ArchiveMessage(message));
                return Result.Success();
            }
            catch (Exception e) {
                e.printStackTrace();
                return Result.Failure(e);
            }
        }
    }

    public Result updateMessage(IMessage message) {
        return putMessage(message);
    }
    
    public Result deleteMessage(IMessage message) {
        synchronized (messages) {
            try {
                mapper.delete(new ArchiveMessage(message));
                return Result.Success();
            }
            catch (Exception e) {
                e.printStackTrace();
                return Result.Failure(e);
            }
        }
    }

    public Option<Queue<ArchiveMessage>> getFullArchive(String topic) {
        synchronized (messages) {
            try {
                Queue<ArchiveMessage> messageQueue = new LinkedList<>();
                QuerySpec querySpec = new QuerySpec()
                        .withSelect(Select.ALL_ATTRIBUTES)
                        .withHashKey("Topic", topic);
                ItemCollection<QueryOutcome> queryResults = messages.query(querySpec);
                for (Item item : queryResults)
                    messageQueue.add(new ArchiveMessage(item));
                return Result.Success(messageQueue);
            }
            catch (Exception e) {
                e.printStackTrace();
                return Result.Failure(e);
            }
        }
    }

    @Override
    public Option<Queue<ArchiveMessage>> queryArchive(String pattern) {
        synchronized (messages) {
            try {
                Queue<ArchiveMessage> messageQueue = new LinkedList<>();
                ScanSpec scanSpec = new ScanSpec()
                        .withSelect(Select.ALL_ATTRIBUTES)
                        .withScanFilters(new ScanFilter("Content").contains(pattern))
                        .withMaxResultSize(5);
                ItemCollection<ScanOutcome> scanResults = topics.scan(scanSpec);
                for (Item item : scanResults)
                    messageQueue.add(new ArchiveMessage(item));
                return Result.Success(messageQueue);
            }
            catch (Exception e) {
                e.printStackTrace();
                return Result.Failure(e);
            }
        }
    }

    @Override
    public Option<Queue<ArchiveMessage>> queryArchive(String topic, String pattern) {
        synchronized (messages) {
            try {
                Queue<ArchiveMessage> messageQueue = new LinkedList<>();
                QuerySpec querySpec = new QuerySpec()
                        .withSelect(Select.ALL_ATTRIBUTES)
                        .withHashKey("Topic", topic)
                        .withQueryFilters(new QueryFilter("Content").contains(pattern))
                        .withScanIndexForward(false)
                        .withMaxResultSize(5);
                ItemCollection<QueryOutcome> queryResults = messages.query(querySpec);
                for (Item item : queryResults)
                    messageQueue.add(new ArchiveMessage(item));
                return Result.Success(messageQueue);
            }
            catch (Exception e) {
                e.printStackTrace();
                return Result.Failure(e);
            }
        }
    }
}
