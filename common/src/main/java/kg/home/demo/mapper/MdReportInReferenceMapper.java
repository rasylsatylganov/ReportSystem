package kg.home.demo.mapper;

import kg.home.demo.entity.DrReferenceEntity;
import kg.home.demo.entity.DrTypePositionEntity;
import kg.home.demo.entity.MdReportInEntity;
import kg.home.demo.entity.MdReportInReferenceEntity;
import kg.home.demo.model.MdReportInReference;
import kg.home.demo.model.MdReportInReferenceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MdReportInReferenceMapper {


    private final JpaReferenceResolver referenceResolver;
    private final DrReferenceMapper drReferenceMapper;
    private final DrTypePositionMapper drTypePositionMapper;
    private final MdReportInMapper mdReportInMapper;

    public void applyRequest(MdReportInReferenceEntity entity, MdReportInReferenceRequest request) {
        entity.setReportIn(referenceResolver.resolve(MdReportInEntity.class, request.reportIn()));
        entity.setReference(referenceResolver.resolve(DrReferenceEntity.class, request.reference()));
        entity.setTypePosition(referenceResolver.resolve(DrTypePositionEntity.class, request.typePosition()));
        entity.setIndexNum(request.indexNum());
        entity.setOrderSort(request.orderSort());
    }

    public MdReportInReference toModel(MdReportInReferenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MdReportInReference(
                entity.getId(),
                mdReportInMapper.toModel(entity.getReportIn()),
                drReferenceMapper.toModel(entity.getReference()),
                drTypePositionMapper.toModel(entity.getTypePosition()),
                entity.getIndexNum(),
                entity.getOrderSort()
        );
    }
}
