import java.io.Serializable;

public class Messages
{
    public static class ChatMsg implements Serializable
    {
        String txt;
        String channelToPublishTo;
        String sentByUser;

        ChatMsg() {}

        ChatMsg(String txt, String channel, String user)
        {
            this.txt = txt;
            channelToPublishTo = channel;
            sentByUser = user;
        }
    }
}
