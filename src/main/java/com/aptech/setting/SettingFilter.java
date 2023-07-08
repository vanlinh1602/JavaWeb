package com.aptech.setting;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.aptech.common.Constants;
import com.aptech.common.entity.Menu;
import com.aptech.common.entity.setting.Setting;
import com.aptech.menu.MenuService;

@Component
@Order(-123)
public class SettingFilter implements Filter {

	@Autowired private SettingService articleService;
	
	@Autowired private MenuService menuService;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURL().toString();
		
		if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") ||
				url.endsWith(".jpg")) {
			chain.doFilter(request, response);
			return;
		}
		
		loadGeneralSettings(request);
		loadMenuSettings(request);
		
		chain.doFilter(request, response);

	}

	private void loadMenuSettings(ServletRequest request) {
		List<Menu> headerMenuItems = menuService.getHeaderMenuItems();
		request.setAttribute("headerMenuItems", headerMenuItems);

		List<Menu> footerMenuItems = menuService.getFooterMenuItems();
		request.setAttribute("footerMenuItems", footerMenuItems);		
	}

	private void loadGeneralSettings(ServletRequest request) {
		List<Setting> generalSettings = articleService.getGeneralSettings();
		
		generalSettings.forEach(setting -> {
			request.setAttribute(setting.getKey(), setting.getValue());
			
		});
		
		request.setAttribute("S3_BASE_URI", Constants.S3_BASE_URI);
	}

}
