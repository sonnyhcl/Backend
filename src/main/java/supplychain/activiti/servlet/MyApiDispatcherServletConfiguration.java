/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package supplychain.activiti.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@ComponentScan(value = {
        "org.activiti.rest",
        "supplychain.web"
})
@EnableAsync
@EnableWebMvc
public class MyApiDispatcherServletConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
    //
    @Autowired
    protected ObjectMapper objectMapper;

    //
//	@Autowired
//	protected Environment environment;
//
    @Bean
    public SessionLocaleResolver localeResolver() {
        return new SessionLocaleResolver();
    }

    // webSocket  
    private static final String WEBSOCKET_SERVER = "/webSocketServer";
    private static final String ECHO = "/echo";
    // 不支持webSocket的话用sockjs  
    private static final String SOCKJS = "/sockjs/webSocketServer";

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //支持websocket 的访问链接  
        registry.addHandler(systemWebSocketHandler(), WEBSOCKET_SERVER).addInterceptors(handshakeInterceptor());
        registry.addHandler(systemWebSocketHandler(), ECHO).addInterceptors(handshakeInterceptor());
        //不支持websocket的访问链接  
        registry.addHandler(systemWebSocketHandler(), SOCKJS).addInterceptors(handshakeInterceptor()).withSockJS();
    }

    @Bean
    public WebSocketHandler systemWebSocketHandler() {
        return new SystemWebSocketHandler();
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new WebsocketHandshakeInterceptor();
    }

    // Allow serving HTML files through the default Servlet  

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }


//    @Bean
//    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
//        RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
//        requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
//        requestMappingHandlerMapping.setRemoveSemicolonContent(false);
//        requestMappingHandlerMapping.setInterceptors(getInterceptors());
//        return requestMappingHandlerMapping;
//    }
//    
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        extendMessageConverters(converters);
//        for (HttpMessageConverter<?> converter: converters) {
//            if (converter instanceof MappingJackson2HttpMessageConverter) {
//                MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter)
// converter;
//                jackson2HttpMessageConverter.setObjectMapper(objectMapper);
//                break;
//            }
//        }
//    }
}
