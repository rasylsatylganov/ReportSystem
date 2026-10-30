package kg.home.demo.repository;

import kg.home.demo.entity.MdReportInReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MdReportInReferenceRepository extends JpaRepository<MdReportInReferenceEntity, Long> {
    List<MdReportInReferenceEntity> findByReportIn_IdOrderById(Long reportId);
}
