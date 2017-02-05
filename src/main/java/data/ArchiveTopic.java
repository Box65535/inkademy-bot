package data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import sx.blah.discord.handle.obj.IChannel;

import java.io.Serializable;
import java.math.BigInteger;

@DynamoDBTable(tableName = "InkademyTopics")
public class ArchiveTopic implements Serializable {
    
    public ArchiveTopic() {}

    public ArchiveTopic(IChannel channel, boolean active) {
        this.channelId = new BigInteger(channel.getID());
        this.topic = channel.getName();
        this.active = active;
    }
    
    private BigInteger channelId;
    private String topic;
    private Boolean active;

    @DynamoDBHashKey(attributeName = "Topic")
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    @DynamoDBRangeKey(attributeName = "ChannelId")
    public BigInteger getChannelId() { return channelId; }
    public void setChannelId(BigInteger channelId) { this.channelId = channelId; }

    @DynamoDBAttribute(attributeName = "Active")
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
