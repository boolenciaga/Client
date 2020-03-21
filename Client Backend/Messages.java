import java.io.Serializable;


public class Messages implements Serializable
{
    String messageType;
    String sentByUser;

    Messages(String msgType, String sentBy)
    {
        messageType = msgType;
        sentByUser = sentBy;
    }

    Messages()
    {

    }
}


class ChatMsg extends Messages
{
    String txt;
    String channelToPublishTo;

    ChatMsg(String txt, String channel, String userName)
    {
        super("ChatMsg", userName);
        this.txt = txt;
        channelToPublishTo = channel;
    }
}
