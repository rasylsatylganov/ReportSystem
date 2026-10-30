package kg.home.demo.mapper;

import kg.home.demo.entity.DrTypePositionEntity;
import kg.home.demo.model.DrTypePosition;
import kg.home.demo.model.DrTypePositionRequest;
import org.springframework.stereotype.Component;

@Component
public class DrTypePositionMapper {

    public void applyRequest(DrTypePositionEntity entity, DrTypePositionRequest request) {
        entity.setNameTypePosition(request.nameTypePosition());
    }

    public DrTypePosition toModel(DrTypePositionEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DrTypePosition(
                entity.getId(),
                entity.getNameTypePosition()
        );
    }
}
