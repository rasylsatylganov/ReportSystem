package kg.home.demo.service;

import kg.home.demo.entity.MdReportIn;

import java.util.List;
import java.util.Optional;

public interface MdReportInService {

    List<MdReportIn> getAllMdReportIns();

    Optional<MdReportIn> getMdReportInById(Long id);

    MdReportIn createMdReportIn(MdReportIn mdReportIn);

    MdReportIn updateMdReportIn(Long id, MdReportIn mdReportIn);

    void deleteMdReportIn(Long id);
}
