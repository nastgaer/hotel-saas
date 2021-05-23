package io.renren.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.renren.modules.sys.oauth2.OAuth2Filter;
import io.renren.modules.sys.oauth2.OAuth2Realm;

@Configuration
public class ShiroConfig {

	@Bean("securityManager")
	public SecurityManager securityManager(OAuth2Realm oAuth2Realm) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(oAuth2Realm);
		securityManager.setRememberMeManager(null);
		return securityManager;
	}

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		shiroFilter.setSecurityManager(securityManager);

		// oauth过滤
		Map<String, Filter> filters = new HashMap<>();
		filters.put("oauth2", new OAuth2Filter());
		shiroFilter.setFilters(filters);

		Map<String, String> filterMap = new LinkedHashMap<>();
		filterMap.put("/webjars/**", "anon");
		filterMap.put("/druid/**", "anon");
		filterMap.put("/app/**", "anon");
		filterMap.put("/hotel/**", "anon");
		filterMap.put("/*/hotel/**", "anon");
		filterMap.put("/*/wx/**", "anon");
		filterMap.put("/wx/user/**", "anon");
		filterMap.put("/pay/**", "anon");
		filterMap.put("/sys/login", "anon");
		filterMap.put("/sys/user/findPwd", "anon");
		filterMap.put("/hotel/hotelfriendlink/allList", "anon");
		filterMap.put("/websocket/**", "anon");
		filterMap.put("/hotel/hotelhelepmenu/helpMenus", "anon");
		filterMap.put("/hotel/hotelsystem/info", "anon");
		filterMap.put("/hotel/messageboard/save", "anon");
		filterMap.put("/hotel/common/**", "anon");
		filterMap.put("/swagger/**", "anon");
		filterMap.put("/v2/api-docs", "anon");
		filterMap.put("/swagger-ui.html", "anon");
		filterMap.put("/swagger-resources/**", "anon");
		filterMap.put("/captcha.jpg", "anon");
		filterMap.put("/aaa.txt", "anon");
		filterMap.put("/**", "oauth2");
		shiroFilter.setFilterChainDefinitionMap(filterMap);

		return shiroFilter;
	}

	@Bean("lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

}
