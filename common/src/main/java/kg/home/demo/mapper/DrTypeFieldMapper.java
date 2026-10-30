package kg.home.demo.mapper;

import kg.home.demo.entity.DrTypeFieldEntity;
import kg.home.demo.model.DrTypeField;
import kg.home.demo.model.DrTypeFieldRequest;
import org.springframework.stereotype.Component;

@Component
public class DrTypeFieldMapper {

    public void applyRequest(DrTypeFieldEntity entity, DrTypeFieldRequest request) {
        entity.setCodeTypeField(request.codeTypeField());
        entity.setNameTypeField(request.nameTypeField());
    }

    public DrTypeField toModel(DrTypeFieldEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DrTypeField(
                entity.getId(),
                entity.getCodeTypeField(),
                entity.getNameTypeField()
        );
    }
}
