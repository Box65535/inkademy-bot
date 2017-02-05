package data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.Item;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.Date;

@DynamoDBTable(tableName = "InkademyMessages")
public class ArchiveMessage implements Serializable, Comparable<ArchiveMessage> {
    
    public ArchiveMessage() {}
    
    public ArchiveMessage(IMessage message) {
        
        IUser author = message.getAuthor();
        IChannel channel = message.getChannel();
        
        this.id = new BigInteger(message.getID());
        this.username = author.getName();
        this.avatar = author.getAvatarURL();
        this.timestamp = Date.from(message.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()).getTime();
        this.topic = channel.getName();
        this.content = message.getContent();
    }

    public ArchiveMessage(Item queryResultItem) {

        this.id = queryResultItem.getBigInteger("MessageId");
        this.username = queryResultItem.getString("Username");
        this.avatar = queryResultItem.getString("Avatar");
        this.timestamp = queryResultItem.getLong("Timestamp");
        this.topic = queryResultItem.getString("Topic");
        this.content = queryResultItem.getString("Content");
    }
    
    public EmbedObject toEmbedMessage() {
        return new EmbedBuilder()
                .withAuthorName(this.username)
                .withAuthorIcon(this.avatar)
                .withTimestamp(this.timestamp)
                .withDescription(this.content)
                .build();
    }
    
    @Override
    public String toString() {
        return new StringBuilder(username)
                .append(" said on ")
                .append(new Date(timestamp).toString())
                .append(" : ")
                .append(content)
                .append("\n")
                .toString();
    }

    @Override
    public int compareTo(ArchiveMessage other) {
        return this.id.compareTo(other.id);
    }
    
    private BigInteger id;
    private String username;
    private String avatar;
    private Long timestamp;
    private String topic;
    private String content;

    @DynamoDBHashKey(attributeName = "Topic")
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    @DynamoDBRangeKey(attributeName = "MessageId")
    public BigInteger getId() { return id; }
    public void setId(BigInteger id) { this.id = id; }

    @DynamoDBAttribute(attributeName = "Username")
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @DynamoDBAttribute(attributeName = "Avatar")
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    @DynamoDBAttribute(attributeName = "Timestamp")
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    @DynamoDBAttribute(attributeName = "Content")
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
