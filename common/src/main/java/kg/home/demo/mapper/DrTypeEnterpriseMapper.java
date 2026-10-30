package kg.home.demo.mapper;

import kg.home.demo.entity.DrTypeEnterpriseEntity;
import kg.home.demo.model.DrTypeEnterprise;
import kg.home.demo.model.DrTypeEnterpriseRequest;
import org.springframework.stereotype.Component;

@Component
public class DrTypeEnterpriseMapper {

    public void applyRequest(DrTypeEnterpriseEntity entity, DrTypeEnterpriseRequest request) {
        entity.setCodeTypeEnterprise(request.codeTypeEnterprise());
        entity.setNameTypeEnterprise(request.nameTypeEnterprise());
    }

    public DrTypeEnterprise toModel(DrTypeEnterpriseEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DrTypeEnterprise(
                entity.getId(),
                entity.getCodeTypeEnterprise(),
                entity.getNameTypeEnterprise(),
                entity.getSortIndex()
        );
    }
}
