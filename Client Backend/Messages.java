import java.io.Serializable;


public class Messages implements Serializable
{
    final String messageType;
    final String sentBy;

    Messages(String msgType, String sentBy)
    {
        messageType = msgType;
        this.sentBy = sentBy;
    }
}


class ChatRoomInfoMsg extends Messages
{
    final int chatRoomPort;
    final String chatRoomName;
    final boolean hadToBeCreated;

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
    final String txt;
    final String channelToPublishTo;

    ChatMsg(String txt, String chatRoom, String userName)
    {
        super("ChatMsg", userName);
        this.txt = txt;
        channelToPublishTo = chatRoom;
    }

    //copy constructor but resets sentBy
    ChatMsg(ChatMsg srcObj, String messageFrom)
    {
        this(srcObj.txt, srcObj.channelToPublishTo, messageFrom);
    }
}
