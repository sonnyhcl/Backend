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

import supplychain.activiti.conf.MyCorsRegistration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan(value = {
		"org.activiti.rest",
		"supplychain.web"
		})
@EnableAsync
@EnableWebMvc
public class MyApiDispatcherServletConfiguration extends WebMvcConfigurerAdapter {
//
//	@Autowired
//    protected ObjectMapper objectMapper;
//    
//	@Autowired
//	protected Environment environment;
//
    @Bean
    public SessionLocaleResolver localeResolver() {
        return new SessionLocaleResolver();
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
//                MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) converter;
//                jackson2HttpMessageConverter.setObjectMapper(objectMapper);
//                break;
//            }
//        }
//    }
    
//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        // 对响应头进行CORS授权
//        MyCorsRegistration corsRegistration = new MyCorsRegistration("/api/**");
//        corsRegistration.allowedOrigins(CrossOrigin.DEFAULT_ORIGINS)
//                .allowedMethods("GET" , "POST" , "OPTIONS")
//                .allowedHeaders("Authorization")
//                .exposedHeaders("Authorization")
//                .allowCredentials(CrossOrigin.DEFAULT_ALLOW_CREDENTIALS)
//                .maxAge(CrossOrigin.DEFAULT_MAX_AGE);
//
//        // 注册CORS过滤器
//        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
//        configurationSource.registerCorsConfiguration("/api/**", corsRegistration.getCorsConfiguration());
//        CorsFilter corsFilter = new CorsFilter(configurationSource);
//        return new FilterRegistrationBean(corsFilter);
//    }
//	
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        // 配置CorsInterceptor的CORS参数
//        this._configCorsParams(registry.addMapping("/api/**"));
//    }
//
//    private void _configCorsParams(CorsRegistration corsRegistration) {
//        corsRegistration.allowedOrigins(CrossOrigin.DEFAULT_ORIGINS)
//                .allowedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.PUT.name())
//                .allowedHeaders(CrossOrigin.DEFAULT_ALLOWED_HEADERS)
//                .exposedHeaders(HttpHeaders.SET_COOKIE)
//                .allowCredentials(CrossOrigin.DEFAULT_ALLOW_CREDENTIALS)
//                .maxAge(CrossOrigin.DEFAULT_MAX_AGE);
//    }
}
