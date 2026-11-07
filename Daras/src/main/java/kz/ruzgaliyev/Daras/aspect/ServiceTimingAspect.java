package kz.ruzgaliyev.Daras.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component 
public class ServiceTimingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceTimingAspect.class);
    
    @Around("execution(* kz.ruzgaliyev.Daras.service..*(..))")
    public Object wrap(ProceedingJoinPoint pjp) throws Throwable {
        logger.info("Before {}", pjp.getSignature().toShortString());

        long startTime = System.nanoTime();

        Object result;

        try { result = pjp.proceed(); return result; }
        finally {
            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            logger.info("Execution time of {}: {} milliseconds", pjp.getSignature().toShortString(), duration/1_000_000);
        }
    }


    public ServiceTimingAspect() {
        logger.info("ServiceTimingAspect initialized");
    }

}
