package kg.home.demo.mapper;

import jakarta.persistence.EntityManager;
import kg.home.demo.model.IdReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaReferenceResolver {


    private final EntityManager entityManager;

    public <T> T resolve(Class<T> entityType, IdReference reference) {
        // Используем управляемую ссылку: поля входного объекта не меняют связанную запись.
        return reference != null ? entityManager.getReference(entityType, reference.id()) : null;
    }
}
