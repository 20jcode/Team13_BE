package dbdr.security.model;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import dbdr.security.service.DbdrSeucrityService;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
public class DbdrAuthAop {

    private final DbdrSeucrityService dbdrSeucrityService;

    @Before("@annotation(DbdrAuth)")
    public Object authCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DbdrAuth dbdrAuth = method.getAnnotation(DbdrAuth.class);

        Role role = dbdrAuth.targetRole();
        AuthParam authParam = dbdrAuth.authParam();
        String id = dbdrAuth.id();

        Object[] args = joinPoint.getArgs();
        for(Object arg : args) {
            if(arg instanceof Role){
                role = (Role) arg;
            }
            else if(arg instanceof AuthParam) {
                authParam = (AuthParam) arg;
            }
            else if(arg instanceof String) {
                id = (String) arg;
            }
            else {
                throw new IllegalArgumentException("권한 검사 중 잘못된 오류가 발생하였습니다.");
            }
        }

        if(!dbdrSeucrityService.hasAcesssPermission(role, authParam, id)) {
            log.info("권한검사 AOP에서 오류가 발생하였습니다.");
            throw new ApplicationException(ApplicationError.ACCESS_NOT_ALLOWED);
        }

        return joinPoint.proceed();
    }
}
