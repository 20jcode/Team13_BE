package dbdr.global.util.mapper;

public interface EntityMapper<E> {
    <D> D toRequest(E entity);
    <D> D toResponse(E entity);
    <D> E toEntity(D dto);
    boolean isSupports(Class<?> object);
    //<D, E> : 제네릭 타입, 이거 쓰겠다는 뜻
    //D : DTO
    //E : Entity
}
