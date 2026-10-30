package kg.home.demo.service;

import kg.home.demo.entity.MdReportInEntity;

import java.util.List;
import java.util.Optional;

public interface MdReportInService {

    List<MdReportInEntity> getAllMdReportIns();

    Optional<MdReportInEntity> getMdReportInById(Long id);

    MdReportInEntity createMdReportIn(MdReportInEntity mdReportIn);

    MdReportInEntity updateMdReportIn(Long id, MdReportInEntity mdReportIn);

    void deleteMdReportIn(Long id);
}
