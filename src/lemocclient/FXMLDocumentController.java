/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package lemocclient;


import cc.plural.jsonij.JSON;
import cc.plural.jsonij.parser.ParserException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

//https://bitbucket.org/jmarsden/jsonij/wiki/Home
//https://www.java.com/zh_CN/download/windows_xpi.jsp
//jre1.8.0_111
// https://github.com/TooTallNate/Java-WebSocket


/**
 * @author noname
 */
public class FXMLDocumentController implements Initializable {
    public static WebSocketClient cc;
    public static boolean connected = false;
    String LemocUrl = "ws://localhost:25303";

    @FXML
    ListView msgList;

    @FXML
    ListView jsonList;

    @FXML
    TextField tQQ;

    @FXML
    TextField tInput;

    @FXML
    Button bCon;

    @FXML
    Button bSend;

    @FXML
    private void OnConAction(ActionEvent event) {
        //System.out.println("You clicked me!");
        try {
            // cc = new ChatClient(new URI(uriField.getText()), area, ( Draft ) draft.getSelectedItem() );
            //默认使用draft_17, java_websocket支持Draft_17, Draft_10, Draft_76, Draft_75
            cc = new WebSocketClient(new URI(LemocUrl), (Draft) new Draft_17()) {
                @Override
                public void onMessage(String message) {
                    AppendToJsonList(message);
                    try {
                        System.out.println("------"+message);
                        JSON json = JSON.parse(message);
                        System.out.println(String.format("%s", json.get("act")));
                        if (String.format("%s", json.get("act")).trim().equals("21")) {
                            String msg = String.format("%s", json.get("fromQQ")) + "对你说： " + String.format("%s", json.get("msg"));
                            System.out.println(msg);
                            AppendToMsgList(msg);
                            //处理私聊的消息
                            MsgUtil.putMsg(json.get("fromQQ").toString(),json.get("msg").toString());
                        } else if (String.format("%s", json.get("act")).trim().equals("2")) {
                            String msg = String.format("%s", json.get("fromQQ")) + "在群" + String.format("%s", json.get("fromGroup")) + "里说: " + String.format("%s", json.get("msg"));
                            AppendToMsgList(msg);
                            MsgUtil.putGroupMsg(json.get("fromQQ").toString(),json.get("msg").toString());
                        } else if (String.format("%s", json.get("act")).trim().equals("4")) {
                            String msg = String.format("%s", json.get("fromQQ")) + "在讨论组" + String.format("%s", json.get("fromDiscuss")) + "里说: " + String.format("%s", json.get("msg"));
                            AppendToMsgList(msg);
                            MsgUtil.putTaolunMsg(json.get("fromQQ").toString(),json.get("msg").toString());
                        }

                    } catch (ParserException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                @Override
                public void onOpen(ServerHandshake handshake) {
                    AppendToMsgList("你已经连接到: " + getURI());
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    AppendToMsgList("你已经断开连接: " + getURI() + "; Code: " + code + " " + reason);
                    bSend.setDisable(true);
                    bCon.setDisable(false);
                    tQQ.setDisable(true);
                    tInput.setDisable(true);
                    connected = false;
                }

                @Override
                public void onError(Exception ex) {
                    AppendToMsgList("崩溃 ...\n" + ex);
                    bSend.setDisable(true);
                    bCon.setDisable(false);
                    tQQ.setDisable(true);
                    tInput.setDisable(true);
                    connected = false;

                    ex.printStackTrace();
                }
            };

            bSend.setDisable(false);
            bCon.setDisable(true);
            tQQ.setDisable(false);
            tInput.setDisable(false);
            connected = true;
            cc.connect();
        } catch (URISyntaxException ex) {
            System.out.println(LemocUrl + " 不是个有效的websocket服务器地址\n");
        }
    }


    @FXML
    private void OnSendAction(ActionEvent event) {
        if (tQQ.getText() == null || tQQ.getText().trim().length() <= 0) {
            AppendToMsgList("请输入QQ号");
            return;
        }
        if (tInput.getText() == null || tInput.getText().trim().length() <= 0) {
            AppendToMsgList("请输入内容");
            return;
        }

        sendMsg(tQQ.getText(),tInput.getText());

        tInput.setText("");
        tInput.requestFocus();
    }

    /**
     * 私聊
     * @param toQQ
     * @param msg
     */
    private void sendMsg(String toQQ,String msg){
        String json = "{\"act\": \"106\", \"QQID\": \"" + toQQ.trim() + "\", \"msg\":\"" + msg.trim() + "\"}";
        AppendToJsonList(json);
        AppendToMsgList("你对" + toQQ.trim() + "说： " + msg.trim());
        System.out.println(json);
        cc.send(json);
    }

    /**
     * 获得指定文件的byte数组
     */
    public byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }



    private void AppendToMsgList(String msg) {
        Platform.runLater(new MyRunnable(msg,"1"));
    }

    private void AppendToJsonList(String msg) {
        Platform.runLater(new MyRunnable(msg,"2"));
    }


    class MyRunnable implements Runnable {
        private String msg ;
        private String type ;
        MyRunnable(String msg,String type){
            this.msg = msg;
            this.type = type;
        }

        @Override
        public void run() {
            if("1".equals(type)){
                msgList.getItems().add(msgList.getItems().size(), msg);
                msgList.scrollTo(msgList.getItems().size() - 1);
            }else{
                jsonList.getItems().add(jsonList.getItems().size(), msg);
                jsonList.scrollTo(jsonList.getItems().size() - 1);
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        WebSocketImpl.DEBUG = true;
        bSend.setDisable(true);
        bCon.setDisable(false);
        tQQ.setDisable(true);
        tInput.setDisable(true);
        connected = false;
    }


}


