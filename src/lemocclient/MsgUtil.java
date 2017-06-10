package lemocclient;

/**
 * @author lishupeng
 * @create 2017-06-10 上午 11:22
 **/
public class MsgUtil {

    /**
     * 回复私聊消息
     * @param qq
     * @param msg
     */
    public static void putMsg(String qq,String msg) {
        msg = "[CQ:face,id=14]";
        String json = "{\"act\": \"106\", \"QQID\": \"" + qq.trim() + "\", \"msg\":\"" + msg.trim() + "\"}";
        System.out.println(json);
        FXMLDocumentController.cc.send(json);
    }


    /**
     * 回复群组消息
     * @param qq
     * @param msg
     */
    public static void putGroupMsg(String qq,String msg) {
        String json = "{\"act\": \"106\", \"QQID\": \"" + qq.trim() + "\", \"msg\":\"" + msg.trim() + "\"}";
        System.out.println(json);
        FXMLDocumentController.cc.send(json);
    }

    /**
     * 回复讨论组消息
     * @param qq
     * @param msg
     */
    public static void putTaolunMsg(String qq,String msg) {
        String json = "{\"act\": \"106\", \"QQID\": \"" + qq.trim() + "\", \"msg\":\"" + msg.trim() + "\"}";
        System.out.println(json);
        FXMLDocumentController.cc.send(json);
    }



}
