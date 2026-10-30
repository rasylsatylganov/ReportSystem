package kg.home.demo.repository;

import kg.home.demo.entity.MdReportInReferenceItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MdReportInReferenceItemRepository extends JpaRepository<MdReportInReferenceItemEntity, Long> {
    List<MdReportInReferenceItemEntity> findByReportIn_IdOrderById(Long reportId);
}
