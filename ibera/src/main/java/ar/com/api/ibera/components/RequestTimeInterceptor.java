package ar.com.api.ibera.components;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ar.com.api.ibera.repositories.LogRepository;


@Component("requestTimeInterceptor")
public class RequestTimeInterceptor extends HandlerInterceptorAdapter{

	private static final Log LOG = LogFactory.getLog(RequestTimeInterceptor.class);
	
	@Autowired
	@Qualifier("logRepository")
	private LogRepository logRepository;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		request.setAttribute("startTime", System.currentTimeMillis());
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long startTime = (long) request.getAttribute("startTime");
		String url = request.getRequestURL().toString();
		String username = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null && auth.isAuthenticated()){
			username = auth.getName();
			ar.com.api.ibera.models.Log log = new ar.com.api.ibera.models.Log(new Date(), auth.getDetails().toString(), username, url);
			logRepository.save(log);
		}
		
		LOG.info("--REQUEST URL : '"+ url + "' -- TOTAL TIME : '" + (System.currentTimeMillis() - startTime) + "'MS");
	}

	

	
	
}
