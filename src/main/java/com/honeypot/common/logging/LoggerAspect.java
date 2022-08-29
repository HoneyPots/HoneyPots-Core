package com.honeypot.common.logging;

import com.honeypot.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Slf4j
public class LoggerAspect {

    @Pointcut("execution(* com.honeypot..*Api.*(..))")
    public void loggerPointCut() {
    }

    @Around("loggerPointCut()")
    public Object methodLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();

        String controllerName = proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = proceedingJoinPoint.getSignature().getName();

        Map<String, Object> params = new HashMap<>();

        try {
            params.put("controller", controllerName);
            params.put("handlerMethod", methodName);
            params.put("params", getParams(request));
            params.put("logTime", LocalDateTime.now());
            params.put("requestUri", request.getRequestURI());
            params.put("httpMethod", request.getMethod());
            params.put("authorizedMemberId", SecurityUtils.getCurrentMemberId().orElse(null));
        } catch (Exception e) {
            log.error("LoggerAspect error", e);
        }
        log.info("params : {}", params);

        return result;

    }

    private static JSONObject getParams(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            String replaceParam = param.replaceAll("\\.", "-");
            jsonObject.put(replaceParam, request.getParameter(param));
        }
        return jsonObject;
    }
}