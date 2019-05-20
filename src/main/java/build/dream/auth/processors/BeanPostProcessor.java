package build.dream.auth.processors;

import build.dream.common.utils.ObjectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.access.expression.ExpressionBasedFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by liuyandong on 2017/6/21.
 */
@Component
public class BeanPostProcessor implements org.springframework.beans.factory.config.BeanPostProcessor {
    private FilterSecurityInterceptor authorizationServerFilterSecurityInterceptor;
    private FilterSecurityInterceptor resourceServerFilterSecurityInterceptor;
    private FilterSecurityInterceptor webSecurityFilterSecurityInterceptor;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof FilterSecurityInterceptor) {
            FilterSecurityInterceptor filterSecurityInterceptor = (FilterSecurityInterceptor) bean;
            FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource = filterSecurityInterceptor.getSecurityMetadataSource();
            ExpressionBasedFilterInvocationSecurityMetadataSource expressionBasedFilterInvocationSecurityMetadataSource = (ExpressionBasedFilterInvocationSecurityMetadataSource) filterInvocationSecurityMetadataSource;
            Class<DefaultFilterInvocationSecurityMetadataSource> defaultFilterInvocationSecurityMetadataSourceClass = DefaultFilterInvocationSecurityMetadataSource.class;
            Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = obtainRequestMap(defaultFilterInvocationSecurityMetadataSourceClass, expressionBasedFilterInvocationSecurityMetadataSource);

            Set<RequestMatcher> requestMatchers = requestMap.keySet();

            List<String> patterns = new ArrayList<String>();
            AnyRequestMatcher anyRequestMatcher = null;
            for (RequestMatcher requestMatcher : requestMatchers) {
                if (requestMatcher instanceof AntPathRequestMatcher) {
                    AntPathRequestMatcher antPathRequestMatcher = (AntPathRequestMatcher) requestMatcher;
                    String pattern = antPathRequestMatcher.getPattern();
                    patterns.add(pattern);
                }

                if (requestMatcher instanceof AnyRequestMatcher) {
                    anyRequestMatcher = (AnyRequestMatcher) requestMatcher;
                }
            }

            if (CollectionUtils.isNotEmpty(patterns) && patterns.contains("/oauth/token") && patterns.contains("/oauth/token_key") && patterns.contains("/oauth/check_token") && anyRequestMatcher == null) {
                authorizationServerFilterSecurityInterceptor = filterSecurityInterceptor;
            }

            if (CollectionUtils.isNotEmpty(patterns) && patterns.contains("/api/index") && anyRequestMatcher == null) {
                resourceServerFilterSecurityInterceptor = filterSecurityInterceptor;
            }

            if (CollectionUtils.isNotEmpty(patterns) && patterns.contains("/auth/index") && patterns.contains("/auth/login") && patterns.contains("/oauth/logout") && patterns.contains("/favicon.ico") && anyRequestMatcher != null) {
                webSecurityFilterSecurityInterceptor = filterSecurityInterceptor;
            }

            if (ObjectUtils.isNotNull(authorizationServerFilterSecurityInterceptor) && ObjectUtils.isNotNull(resourceServerFilterSecurityInterceptor) && ObjectUtils.isNotNull(webSecurityFilterSecurityInterceptor)) {
                int a = 100;
            }
        }
        return bean;
    }

    private Map<RequestMatcher, Collection<ConfigAttribute>> obtainRequestMap(Class<DefaultFilterInvocationSecurityMetadataSource> defaultFilterInvocationSecurityMetadataSourceClass, DefaultFilterInvocationSecurityMetadataSource defaultFilterInvocationSecurityMetadataSource) {
        try {
            Field requestMapField = defaultFilterInvocationSecurityMetadataSourceClass.getDeclaredField("requestMap");
            requestMapField.setAccessible(true);
            return (Map<RequestMatcher, Collection<ConfigAttribute>>) requestMapField.get(defaultFilterInvocationSecurityMetadataSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
