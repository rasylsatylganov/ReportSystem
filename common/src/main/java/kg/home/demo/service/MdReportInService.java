package kg.home.demo.service;

import kg.home.demo.model.MdReportIn;
import kg.home.demo.model.MdReportInRequest;

import java.util.List;
import java.util.Optional;

public interface MdReportInService {

    List<MdReportIn> getAllMdReportIns();

    Optional<MdReportIn> getMdReportInById(Long id);

    MdReportIn createMdReportIn(MdReportInRequest request);

    MdReportIn updateMdReportIn(Long id, MdReportInRequest request);

    void deleteMdReportIn(Long id);
}
