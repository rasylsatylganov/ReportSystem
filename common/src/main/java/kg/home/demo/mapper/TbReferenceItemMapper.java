package kg.home.demo.mapper;

import kg.home.demo.entity.DrEnterpriseEntity;
import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.entity.DrReferenceItemEntity;
import kg.home.demo.entity.MdReportInReferenceEntity;
import kg.home.demo.entity.TbReferenceItemEntity;
import kg.home.demo.model.TbReferenceItem;
import kg.home.demo.model.TbReferenceItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TbReferenceItemMapper {


    private final JpaReferenceResolver referenceResolver;
    private final DrEnterpriseMapper drEnterpriseMapper;
    private final DrPeriodMapper drPeriodMapper;
    private final DrReferenceItemMapper drReferenceItemMapper;
    private final MdReportInReferenceMapper mdReportInReferenceMapper;

    public void applyRequest(TbReferenceItemEntity entity, TbReferenceItemRequest request) {
        entity.setPeriod(referenceResolver.resolve(DrPeriodEntity.class, request.period()));
        entity.setEnterprise(referenceResolver.resolve(DrEnterpriseEntity.class, request.enterprise()));
        entity.setReferenceItem(referenceResolver.resolve(DrReferenceItemEntity.class, request.referenceItem()));
        entity.setReportInReference(referenceResolver.resolve(
                MdReportInReferenceEntity.class, request.reportInReference()));
        entity.setIndexRow(request.indexRow());
    }

    public TbReferenceItem toModel(TbReferenceItemEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TbReferenceItem(
                entity.getId(),
                drPeriodMapper.toModel(entity.getPeriod()),
                drEnterpriseMapper.toModel(entity.getEnterprise()),
                drReferenceItemMapper.toModel(entity.getReferenceItem()),
                mdReportInReferenceMapper.toModel(entity.getReportInReference()),
                entity.getIndexRow()
        );
    }
}
