import java.io.Serializable;


public class Messages implements Serializable
{
    String messageType;
    String sentBy;

    Messages(String msgType, String sentBy)
    {
        messageType = msgType;
        this.sentBy = sentBy;
    }

    Messages()
    {

    }
}


class ChatRoomInfoMsg extends Messages
{
    int chatRoomPort;
    String chatRoomName;
    boolean hadToBeCreated;

    ChatRoomInfoMsg(int portNum, String roomName, boolean isBrandNewRoom)
    {
        super("ChatRoomInfo", "SeverManager");
        chatRoomPort = portNum;
        chatRoomName = roomName;
        hadToBeCreated = isBrandNewRoom;
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

    //copy constructor but resets sentBy
    ChatMsg(ChatMsg srcObj, String messageFrom)
    {
        this(srcObj.txt, srcObj.channelToPublishTo, messageFrom);
    }
}
