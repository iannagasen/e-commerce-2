package dev.agasen.ecom.discovery;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.cloud.netflix.eureka.http.EurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestTemplateDiscoveryClientOptionalArgs;
import org.springframework.cloud.netflix.eureka.http.RestTemplateTransportClientFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;

@Configuration
public class DiscoveryClientOptionalArgConfiguration {
  /**
   * THIS IS TO FIX THE FOLLOWING ERROR:
   * <b> https://github.com/spring-cloud/spring-cloud-netflix/issues/4177 </b>
   * org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'scopedTarget.eurekaClient' defined in class path resource [org/springframework/cloud/netflix/eureka/EurekaClientAutoConfiguration$RefreshableEurekaClientConfiguration.class]: Unsatisfied dependency expressed through method 'eurekaClient' parameter 3: No qualifying bean of type 'com.netflix.discovery.shared.transport.jersey.TransportClientFactories<?>' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
        at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:795) ~[spring-beans-6.1.10.jar:6.1.10]        
        at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:542) ~[spring-beans-6.1.10.jar:6.1.10]

      org.springframework.context.ApplicationContextException: Failed to start bean 'eurekaAutoServiceRegistration'
        at org.springframework.context.support.DefaultLifecycleProcessor.doStart(DefaultLifecycleProcessor.java:288) ~[spring-context-6.1.10.jar:6.1.10]
      Caused by: java.lang.NullPointerException: Cannot invoke "org.springframework.cloud.netflix.eureka.CloudEurekaClient.getApplications()" because the return value of "org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration.getEurekaClient()" is null
   */

	@Bean
	@ConditionalOnClass(name = { "org.springframework.web.client.RestTemplate", "org.glassfish.jersey.client.JerseyClient" })
	@ConditionalOnMissingBean(value = { AbstractDiscoveryClientOptionalArgs.class }, search = SearchStrategy.CURRENT)
	public RestTemplateDiscoveryClientOptionalArgs restTemplateDiscoveryClientOptionalArgs(EurekaClientHttpRequestFactorySupplier eurekaClientHttpRequestFactorySupplier) {
		return new RestTemplateDiscoveryClientOptionalArgs(eurekaClientHttpRequestFactorySupplier);
	}

	@Bean
	@ConditionalOnClass(name = { "org.springframework.web.client.RestTemplate", "org.glassfish.jersey.client.JerseyClient" })
	@ConditionalOnMissingBean(value = { TransportClientFactories.class }, search = SearchStrategy.CURRENT)
	public RestTemplateTransportClientFactories restTemplateTransportClientFactories(RestTemplateDiscoveryClientOptionalArgs optionalArgs) {
		return new RestTemplateTransportClientFactories(optionalArgs);
	}
}