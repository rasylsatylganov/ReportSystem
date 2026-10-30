package kg.home.demo.mapper;

import kg.home.demo.entity.DrReferenceEntity;
import kg.home.demo.model.DrReference;
import kg.home.demo.model.DrReferenceRequest;
import org.springframework.stereotype.Component;

@Component
public class DrReferenceMapper {

    public void applyRequest(DrReferenceEntity entity, DrReferenceRequest request) {
        entity.setCodeReference(request.codeReference());
        entity.setNameReference(request.nameReference());
    }

    public DrReference toModel(DrReferenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DrReference(
                entity.getId(),
                entity.getCodeReference(),
                entity.getNameReference()
        );
    }
}
