package com.infy.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.infy.exception.TaxTrackerException;

@Component
@Aspect
public class LoggingAspect {
	
	public static final Log LOGGER = LogFactory.getLog(LoggingAspect.class);
			
	@AfterThrowing(pointcut = "execution(* com.infy.TaxTracker.service.*Impl.*(..))", throwing = "exception")
    public void logServiceException(TaxTrackerException exception)
    {
    	LOGGER.error(exception.getMessage(), exception);
    }
}
