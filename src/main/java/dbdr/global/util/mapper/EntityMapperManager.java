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

    private final ApplicationContext applicationContext;

    public <E> EntityMapper<E> getMapper(Class<E> entityType) {
        Map<String, EntityMapper> beans = applicationContext.getBeansOfType(EntityMapper.class);
        for (EntityMapper<?> mapper : beans.values()) {
            if (mapper.isSupports(entityType)) {
                return (EntityMapper<E>) mapper;
            }
        }
        log.error("EntityMapper를 찾을 수 없습니다. object : {}", entityType);
        throw new ApplicationException(ApplicationError.ILLIGAL_ARGUMENT);
    }


}
