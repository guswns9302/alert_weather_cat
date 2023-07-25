package com.exithere.rain.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // controller 하위의 모든 public 메서드
    //@Pointcut("within(com.exithere.rain.controller..*)")
    @Pointcut("within(com.exithere.rain.controller.DeviceController || com.exithere.rain.controller.AlarmController || com.exithere.rain.controller.FcstController)")
    public void loggingRequest(){}

    // POST
    // @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    // GET
    // @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")

    // pointcut 과 매칭되는 메서드의 실행 전, 후에 실행
    // @around advice 는 proceed()가 꼭 필요
    @Around("loggingRequest()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        Class cls = joinPoint.getTarget().getClass();

        Object result = null;
        try {
            result = joinPoint.proceed(joinPoint.getArgs());
            return  result;
        }
        finally {
            log.debug("-------------------------------------------------");
            log.debug("request url : {}", getRequestUrl(joinPoint, cls));
            log.debug("parameters : {}", params(joinPoint));
            log.debug("response : {}", result);
            log.debug("-------------------------------------------------");
        }
    }

    private String getRequestUrl(JoinPoint joinPoint, Class cls){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequestMapping requestMapping = (RequestMapping) cls.getAnnotation(RequestMapping.class);
        String baseUrl = requestMapping.value()[0];

        String url = Stream.of(GetMapping.class, PutMapping.class, PostMapping.class, DeleteMapping.class, RequestMapping.class)
                .filter(mappingClass -> method.isAnnotationPresent(mappingClass))
                .map(mappingClass -> getUrl(method, mappingClass, baseUrl))
                .findFirst().orElse(null);

        return url;
    }

    /* httpMETHOD + requestURI 를 반환 */
    private String getUrl(Method method, Class<? extends Annotation> annotationClass, String baseUrl){
        Annotation annotation = method.getAnnotation(annotationClass);
        String[] value;
        String httpMethod = null;
        try {
            value = (String[])annotationClass.getMethod("value").invoke(annotation);
            httpMethod = (annotationClass.getSimpleName().replace("Mapping", "")).toUpperCase();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }
        return String.format("%s %s%s", httpMethod, baseUrl, value.length > 0 ? value[0] : "") ;
    }

    private Map params(JoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            params.put(parameterNames[i], args[i]);
        }
        return params;
    }
}
