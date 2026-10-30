package kg.home.demo.mapper;

import kg.home.demo.entity.DrReferenceEntity;
import kg.home.demo.entity.DrReferenceItemEntity;
import kg.home.demo.model.DrReferenceItem;
import kg.home.demo.model.DrReferenceItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DrReferenceItemMapper {


    private final JpaReferenceResolver referenceResolver;
    private final DrReferenceMapper drReferenceMapper;

    public void applyRequest(DrReferenceItemEntity entity, DrReferenceItemRequest request) {
        entity.setReference(referenceResolver.resolve(DrReferenceEntity.class, request.reference()));
        entity.setCodeReferenceItem(request.codeReferenceItem());
        entity.setNameReferenceItem(request.nameReferenceItem());
    }

    public DrReferenceItem toModel(DrReferenceItemEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DrReferenceItem(
                entity.getId(),
                drReferenceMapper.toModel(entity.getReference()),
                entity.getCodeReferenceItem(),
                entity.getNameReferenceItem(),
                entity.getSortIndex()
        );
    }
}
