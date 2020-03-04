package build.dream.auth.configurations;

import build.dream.auth.constants.ConfigurationKeys;
import build.dream.common.utils.ConfigurationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        String connectTimeout = ConfigurationUtils.getConfiguration(ConfigurationKeys.REST_TEMPLATE_CONNECT_TIMEOUT);
        String readTimeout = ConfigurationUtils.getConfiguration(ConfigurationKeys.REST_TEMPLATE_READ_TIMEOUT);

        RestTemplate restTemplate = null;
        if (StringUtils.isNotBlank(connectTimeout) && StringUtils.isNotBlank(readTimeout)) {
            SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
            simpleClientHttpRequestFactory.setConnectTimeout(Integer.parseInt(connectTimeout));
            simpleClientHttpRequestFactory.setReadTimeout(Integer.parseInt(readTimeout));
            restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
        } else {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
}
