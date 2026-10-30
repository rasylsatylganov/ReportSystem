package kg.home.demo.mapper;

import kg.home.demo.entity.DrEnterpriseEntity;
import kg.home.demo.entity.DrTypeEnterpriseEntity;
import kg.home.demo.model.DrEnterprise;
import kg.home.demo.model.DrEnterpriseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DrEnterpriseMapper {


    private final JpaReferenceResolver referenceResolver;
    private final DrTypeEnterpriseMapper drTypeEnterpriseMapper;

    public void applyRequest(DrEnterpriseEntity entity, DrEnterpriseRequest request) {
        entity.setTypeEnterprise(referenceResolver.resolve(DrTypeEnterpriseEntity.class, request.typeEnterprise()));
        entity.setCodeEnterprise(request.codeEnterprise());
        entity.setNameEnterprise(request.nameEnterprise());
    }

    public DrEnterprise toModel(DrEnterpriseEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DrEnterprise(
                entity.getId(),
                drTypeEnterpriseMapper.toModel(entity.getTypeEnterprise()),
                entity.getCodeEnterprise(),
                entity.getNameEnterprise()
        );
    }
}
