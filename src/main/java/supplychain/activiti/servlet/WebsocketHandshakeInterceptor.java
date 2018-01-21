package supplychain.activiti.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class WebsocketHandshakeInterceptor implements HandshakeInterceptor {

    private static Logger logger = LoggerFactory.getLogger(WebsocketHandshakeInterceptor.class);

    public WebsocketHandshakeInterceptor() {

    }

    @Override
    public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2, Exception arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler webSocket,
                                   Map<String, Object> map) throws Exception {
        // TODO Auto-generated method stub

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            //使用userName区分WebSocketHandler，以便定向发送消息
            // User loginUser= (User) servletRequest.getSession().getAttribute("loginUser");
            //存入数据，方便在hander中获取，这里只是在方便在webSocket中存储了数据，并不是在正常的httpSession中存储，想要在平时使用的session中获得这里的数据，需要使用session 来存储一下
            //   if(null !=loginUser){
            //  map.put("userName", loginUser.getUserLogin());
            //    logger.info("当前的登陆者为：{}", loginUser.getUserLogin());
            //  }else{
            logger.error("没有获取session中的当前登陆者信息");
            //  }
        }

        return true;
    }

}
