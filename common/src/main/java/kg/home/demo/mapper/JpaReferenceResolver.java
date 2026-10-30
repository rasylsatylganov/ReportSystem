package kg.home.demo.mapper;

import jakarta.persistence.EntityManager;
import kg.home.demo.exception.InvalidRequestException;
import kg.home.demo.model.IdReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaReferenceResolver {


    private final EntityManager entityManager;

    public <T> T resolve(Class<T> entityType, IdReference reference) {
        if (reference == null || reference.id() == null || reference.id() <= 0) {
            throw new InvalidRequestException("Укажите положительный идентификатор связанной записи.");
        }
        // Используем управляемую ссылку: поля входного объекта не меняют связанную запись.
        return entityManager.getReference(entityType, reference.id());
    }
}
