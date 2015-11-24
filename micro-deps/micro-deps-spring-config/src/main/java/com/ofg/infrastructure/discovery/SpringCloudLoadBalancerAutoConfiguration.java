package com.ofg.infrastructure.discovery;

import com.ofg.config.BasicProfiles;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Profile(BasicProfiles.SPRING_CLOUD)
public class SpringCloudLoadBalancerAutoConfiguration {

    // TODO: Remove once https://github.com/spring-cloud/spring-cloud-zookeeper/issues/53 is resolved
    @Bean
    @Primary
    public RestTemplateCustomizer restTemplateCustomizerForRibbon(
            final LoadBalancerInterceptor loadBalancerInterceptor) {
        return new RestTemplateCustomizer() {
            @Override
            public void customize(RestTemplate restTemplate) {
                List<ClientHttpRequestInterceptor> list = new ArrayList<>();
                list.add(loadBalancerInterceptor);
                restTemplate.setInterceptors(list);
            }
        };
    }

    @Bean
    @Primary
    public LoadBalancerInterceptor serviceNameResolvingRibbonInterceptor(LoadBalancerClient loadBalancerClient) {
        return new ServiceNameResolvingLoadBalancerInterceptor(loadBalancerClient);
    }

    static class ServiceNameResolvingLoadBalancerInterceptor extends LoadBalancerInterceptor {

        private final Map<String, InetAddress> urlCache = new ConcurrentHashMap<>();

        public ServiceNameResolvingLoadBalancerInterceptor(LoadBalancerClient loadBalancer) {
            super(loadBalancer);
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            final URI originalUri = request.getURI();
            String serviceName = originalUri.getHost();
            if (isNotADependency(serviceName)) {
                return executeRequest(request, body);
            }
            return super.intercept(request, body, execution);
        }

        private ClientHttpResponse executeRequest(HttpRequest request, byte[] body) throws IOException {
            ClientHttpRequest delegate = new SimpleClientHttpRequestFactory().createRequest(request.getURI(), request.getMethod());
            delegate.getHeaders().putAll(request.getHeaders());
            if(body.length > 0) {
                StreamUtils.copy(body, delegate.getBody());
            }
            return delegate.execute();
        }

        private boolean isNotADependency(String serviceName) {
            return urlCache.containsKey(serviceName) || isAValidUrl(serviceName);
        }

        private boolean isAValidUrl(String serviceName) {
            InetAddress url = UrlValidator.isValidUrl(serviceName);
            if (url != null) {
                urlCache.put(serviceName, url);
                return true;
            }
            return false;
        }
    }

}
