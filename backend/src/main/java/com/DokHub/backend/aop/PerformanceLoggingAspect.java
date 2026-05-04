package com.DokHub.backend.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect    // AOP 관점(Aspect) 클래스
@Component // 스프링 빈으로 등록
public class PerformanceLoggingAspect {

    /**
     * Pointcut: "어디에" 이 부가 기능을 적용할 것인가?
     * execution(* com.DokHub.backend.service.ChannelService.*(..))
     * -> 해석: ChannelService 클래스에 있는 "모든" 메서드에 적용하겠다!
     */
    @Around("execution(* com.DokHub.backend.service.ChannelService.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        // 메서드 실행 전
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 실제 타겟 메서드 실행
        // joinPoint.proceed()가 호출되는 순간 getChannelsPaged 등이 실행됩니다.
        Object result = joinPoint.proceed();

        // 메서드 실행 후
        stopWatch.stop();

        // 어떤 메서드가 실행되었는지 이름 가져오기
        String methodName = joinPoint.getSignature().getName();

        // 실행 시간 로그 출력
        log.info("[DOKHUB-AOP] {} 메서드 실행 시간: {} ms", methodName, stopWatch.getTotalTimeMillis());

        return result;
    }
}
