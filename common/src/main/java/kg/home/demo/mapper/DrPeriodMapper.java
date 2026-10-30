package kg.home.demo.mapper;

import kg.home.demo.entity.DrPeriodEntity;
import kg.home.demo.entity.DrTypePeriodEntity;
import kg.home.demo.model.DrPeriod;
import kg.home.demo.model.DrPeriodRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DrPeriodMapper {


    private final JpaReferenceResolver referenceResolver;
    private final DrTypePeriodMapper drTypePeriodMapper;

    public void applyRequest(DrPeriodEntity entity, DrPeriodRequest request) {
        entity.setTypePeriod(referenceResolver.resolve(DrTypePeriodEntity.class, request.typePeriod()));
        entity.setCodePeriod(request.codePeriod());
        entity.setNamePeriod(request.namePeriod());
        entity.setDateBegin(request.dateBegin());
        entity.setDateEnd(request.dateEnd());
    }

    public DrPeriod toModel(DrPeriodEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DrPeriod(
                entity.getId(),
                drTypePeriodMapper.toModel(entity.getTypePeriod()),
                entity.getCodePeriod(),
                entity.getNamePeriod(),
                entity.getDateBegin(),
                entity.getDateEnd(),
                entity.getSortIndex()
        );
    }
}
