import java.io.Serializable;
import java.util.ArrayList;


class Messages implements Serializable
{
    final String messageType;
    final String sentBy;
    final String doNotSendTo;

    Messages(String msgType, String sender)
    {
        messageType = msgType;
        this.sentBy = sender;
        doNotSendTo = sender;
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


class JoinedChatMsg extends Messages
{
    JoinedChatMsg(String userName)
    {
        super("JoinedChatMsg", userName);
    }
}


class LeftChatMsg extends Messages
{
    LeftChatMsg(String userName)
    {
        super("LeftChatMsg", userName);
    }
}


class ChatMsg extends Messages
{
    final String txt;

    ChatMsg(String txt, String userName)
    {
        super("ChatMsg", userName);
        this.txt = txt;
    }

    //copy constructor but resets sentBy
    ChatMsg(ChatMsg srcObj, String messageFrom)
    {
        this(srcObj.txt, messageFrom);
    }
}


class ChatHistoryMsg extends Messages
{
    ArrayList<ChatMsg> chatHistory;
    String receivingName;

    ChatHistoryMsg(ArrayList<ChatMsg> chatLog, String roomName, String sendTo)
    {
        super("ChatHistoryMsg", roomName);
        chatHistory = (ArrayList<ChatMsg>) chatLog.clone();
        receivingName = sendTo;
    }
}
