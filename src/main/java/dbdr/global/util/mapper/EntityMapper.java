package dbdr.global.util.mapper;

public interface EntityMapper {
    <D, E> D toRequest(E entity, Class<D> dtoClass);
    <D, E> D toResponse(E entity, Class<D> dtoClass);
    <D, E> E toEntity(D dto, Class<E> entityClass);
    boolean isSupports(Class<?> object);
}
