package kg.home.demo.entity;

import jakarta.persistence.Column;
import kg.home.demo.mapper.DrTypePeriodMapper;
import kg.home.demo.model.DrTypePeriodRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseEntityTest {

    @ParameterizedTest
    @MethodSource("entityTypes")
    void creationFillsBothDatesInBishkekTime(Class<? extends BaseEntity> entityType) throws Exception {
        BaseEntity entity = entityType.getConstructor().newInstance();
        LocalDateTime before = this.bishkekNow();

        entity.onCreate();

        assertEquals(entity.getCreatedAt(), entity.getUpdatedAt());
        assertFalse(entity.getCreatedAt().isBefore(before));
        assertFalse(entity.getCreatedAt().isAfter(this.bishkekNow()));
    }

    @Test
    void updatePreservesCreationTimeAndRefreshesModificationTime() throws Exception {
        DrTypePeriodEntity entity = new DrTypePeriodEntity();
        LocalDateTime createdAt = LocalDateTime.of(2020, 1, 1, 10, 0);
        LocalDateTime updatedAt = createdAt.plusDays(1);
        this.setAuditField(entity, "createdAt", createdAt);
        this.setAuditField(entity, "updatedAt", updatedAt);
        LocalDateTime before = this.bishkekNow();

        entity.onUpdate();

        assertEquals(createdAt, entity.getCreatedAt());
        assertTrue(entity.getUpdatedAt().isAfter(updatedAt));
        assertFalse(entity.getUpdatedAt().isBefore(before));
        assertFalse(entity.getUpdatedAt().isAfter(this.bishkekNow()));
    }

    @Test
    void auditColumnsAreRequiredAndCreationTimeCannotBeUpdated() throws Exception {
        Column createdAt = BaseEntity.class.getDeclaredField("createdAt").getAnnotation(Column.class);
        Column updatedAt = BaseEntity.class.getDeclaredField("updatedAt").getAnnotation(Column.class);

        assertEquals("CREATED_AT", createdAt.name());
        assertEquals("UPDATED_AT", updatedAt.name());
        assertFalse(createdAt.nullable());
        assertFalse(updatedAt.nullable());
        assertFalse(createdAt.updatable());
        assertTrue(updatedAt.updatable());
        assertThrows(NoSuchMethodException.class,
                () -> DrTypePeriodEntity.class.getMethod("setCreatedAt", LocalDateTime.class));
        assertThrows(NoSuchMethodException.class,
                () -> DrTypePeriodEntity.class.getMethod("setUpdatedAt", LocalDateTime.class));
    }

    @Test
    void requestMappingDoesNotOverwriteAuditFields() {
        DrTypePeriodEntity entity = new DrTypePeriodEntity();
        entity.onCreate();
        LocalDateTime createdAt = entity.getCreatedAt();
        LocalDateTime updatedAt = entity.getUpdatedAt();

        new DrTypePeriodMapper().applyRequest(entity, new DrTypePeriodRequest("MON", "Месячный"));

        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }

    static List<Class<? extends BaseEntity>> entityTypes() {
        return List.of(DrEnterpriseEntity.class, DrPeriodEntity.class,
                DrReferenceEntity.class, DrReferenceItemEntity.class,
                DrTypeEnterpriseEntity.class, DrTypeFieldEntity.class,
                DrTypePeriodEntity.class, DrTypePositionEntity.class,
                MdReportInEntity.class, MdReportInReferenceEntity.class,
                MdReportInReferenceItemEntity.class, TbReferenceItemEntity.class,
                TbReferenceItemValueEntity.class);
    }

    private LocalDateTime bishkekNow() {
        return LocalDateTime.now(ZoneId.of("Asia/Bishkek"));
    }

    private void setAuditField(BaseEntity entity, String fieldName, LocalDateTime value) throws Exception {
        Field field = BaseEntity.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(entity, value);
    }
}
