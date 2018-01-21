package supplychain.activiti.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SystemWebSocketHandler implements WebSocketHandler {
    public static final String USERNAME = "userName";
    private static Logger logger = LoggerFactory.getLogger(SystemWebSocketHandler.class);
    protected final static List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<WebSocketSession>());

    public SystemWebSocketHandler() {
    }

    // 连接建立后处理
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        logger.info("webSocket连接已建立");
        sessions.add(webSocketSession);
        //sendMessageToAll(new TextMessage("Connect ok"));
        logger.info("getId : " + webSocketSession.getId());
        logger.info("getLocalAddress : " + webSocketSession.getLocalAddress().toString());
        logger.info("getTextMessageSizeLimit :" + webSocketSession.getTextMessageSizeLimit());

    }

    // 接收客户端消息，并发送出去
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        logger.info("发送消息" + webSocketSession.getId() + "-" + webSocketMessage.toString());
    }

    // 抛出异常时处理
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        sessions.remove(webSocketSession);
        logger.info("webSocket异常处理" + throwable.getMessage());
        // throw new SystemException(throwable);
    }

    // 连接关闭后处理
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("webSocket连接已关闭......" + closeStatus.getReason());
        sessions.remove(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToAll(TextMessage message) {
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                //throw new SystemException(e.getMessage());
                System.out.println("System Exception");
            }
        }
    }


}
