package kg.home.demo.mapper;

import kg.home.demo.entity.DrTypePeriodEntity;
import kg.home.demo.model.DrTypePeriod;
import kg.home.demo.model.DrTypePeriodRequest;
import org.springframework.stereotype.Component;

@Component
public class DrTypePeriodMapper {

    public void applyRequest(DrTypePeriodEntity entity, DrTypePeriodRequest request) {
        entity.setCodeTypePeriod(request.codeTypePeriod());
        entity.setNameTypePeriod(request.nameTypePeriod());
    }

    public DrTypePeriod toModel(DrTypePeriodEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DrTypePeriod(
                entity.getId(),
                entity.getCodeTypePeriod(),
                entity.getNameTypePeriod(),
                entity.getSortIndex()
        );
    }
}
