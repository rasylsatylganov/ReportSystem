package kg.home.demo.mapper;

import kg.home.demo.entity.DrTypeEnterpriseEntity;
import kg.home.demo.entity.DrTypePeriodEntity;
import kg.home.demo.entity.MdReportInEntity;
import kg.home.demo.model.MdReportIn;
import kg.home.demo.model.MdReportInRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MdReportInMapper {


    private final JpaReferenceResolver referenceResolver;
    private final DrTypeEnterpriseMapper drTypeEnterpriseMapper;
    private final DrTypePeriodMapper drTypePeriodMapper;

    public void applyRequest(MdReportInEntity entity, MdReportInRequest request) {
        entity.setCodeReportIn(request.codeReportIn());
        entity.setNameReportIn(request.nameReportIn());
        entity.setTypePeriod(referenceResolver.resolve(DrTypePeriodEntity.class, request.typePeriod()));
        entity.setTypeEnterprise(referenceResolver.resolve(DrTypeEnterpriseEntity.class, request.typeEnterprise()));
        entity.setIndexRowScanBegin(request.indexRowScanBegin());
        entity.setIndexRowScanEnd(request.indexRowScanEnd());
    }

    public MdReportIn toModel(MdReportInEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MdReportIn(
                entity.getId(),
                entity.getCodeReportIn(),
                entity.getNameReportIn(),
                drTypePeriodMapper.toModel(entity.getTypePeriod()),
                drTypeEnterpriseMapper.toModel(entity.getTypeEnterprise()),
                entity.getIndexRowScanBegin(),
                entity.getIndexRowScanEnd()
        );
    }
}
