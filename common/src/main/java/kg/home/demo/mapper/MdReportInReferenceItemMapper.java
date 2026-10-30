package kg.home.demo.mapper;

import kg.home.demo.entity.DrReferenceItemEntity;
import kg.home.demo.entity.DrTypeFieldEntity;
import kg.home.demo.entity.MdReportInEntity;
import kg.home.demo.entity.MdReportInReferenceItemEntity;
import kg.home.demo.model.MdReportInReferenceItem;
import kg.home.demo.model.MdReportInReferenceItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MdReportInReferenceItemMapper {


    private final JpaReferenceResolver referenceResolver;
    private final DrReferenceItemMapper drReferenceItemMapper;
    private final DrTypeFieldMapper drTypeFieldMapper;
    private final MdReportInMapper mdReportInMapper;

    public void applyRequest(MdReportInReferenceItemEntity entity, MdReportInReferenceItemRequest request) {
        entity.setReportIn(referenceResolver.resolve(MdReportInEntity.class, request.reportIn()));
        entity.setReferenceItem(referenceResolver.resolve(DrReferenceItemEntity.class, request.referenceItem()));
        entity.setTypeFieldValue(referenceResolver.resolve(DrTypeFieldEntity.class, request.typeFieldValue()));
        entity.setIndexColumn(request.indexColumn());
        entity.setOrderSort(request.orderSort());
        entity.setIsRequired(request.isRequired());
    }

    public MdReportInReferenceItem toModel(MdReportInReferenceItemEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MdReportInReferenceItem(
                entity.getId(),
                mdReportInMapper.toModel(entity.getReportIn()),
                drReferenceItemMapper.toModel(entity.getReferenceItem()),
                drTypeFieldMapper.toModel(entity.getTypeFieldValue()),
                entity.getIndexColumn(),
                entity.getOrderSort(),
                entity.getIsRequired()
        );
    }
}
