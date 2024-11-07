package dbdr.global.util.mapper;

import dbdr.global.exception.ApplicationError;
import dbdr.global.exception.ApplicationException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntityMapperManager {

    private ApplicationContext applicationContext;

    public EntityMapper getMapper(Class<?> object) {
        Map<String, EntityMapper> beans = applicationContext.getBeansOfType(EntityMapper.class);
        for (EntityMapper mapper : beans.values()) {
            if (mapper.isSupports(object)) {
                return mapper;
            }
        }
        log.error("EntityMapper를 찾을 수 없습니다. object : {}", object);
        throw new ApplicationException(ApplicationError.ILLIGAL_ARGUMENT);
    }
}
