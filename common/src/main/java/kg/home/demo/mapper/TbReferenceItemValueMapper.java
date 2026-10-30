package kg.home.demo.mapper;

import kg.home.demo.entity.MdReportInReferenceItemEntity;
import kg.home.demo.entity.TbReferenceItemEntity;
import kg.home.demo.entity.TbReferenceItemValueEntity;
import kg.home.demo.model.TbReferenceItemValue;
import kg.home.demo.model.TbReferenceItemValueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TbReferenceItemValueMapper {


    private final JpaReferenceResolver referenceResolver;
    private final MdReportInReferenceItemMapper mdReportInReferenceItemMapper;
    private final TbReferenceItemMapper tbReferenceItemMapper;

    public void applyRequest(TbReferenceItemValueEntity entity, TbReferenceItemValueRequest request) {
        entity.setReferenceItem(referenceResolver.resolve(TbReferenceItemEntity.class, request.referenceItem()));
        entity.setReportInReferenceItem(referenceResolver.resolve(
                MdReportInReferenceItemEntity.class, request.reportInReferenceItem()));
        entity.setValueNumber(request.valueNumber());
        entity.setValueString(request.valueString());
        entity.setValueDatetime(request.valueDatetime());
        entity.setIndexRow(request.indexRow());
    }

    public TbReferenceItemValue toModel(TbReferenceItemValueEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TbReferenceItemValue(
                entity.getId(),
                tbReferenceItemMapper.toModel(entity.getReferenceItem()),
                mdReportInReferenceItemMapper.toModel(entity.getReportInReferenceItem()),
                entity.getValueNumber(),
                entity.getValueString(),
                entity.getValueDatetime(),
                entity.getIndexRow()
        );
    }
}
