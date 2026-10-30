package kg.home.demo.controller;

import kg.home.demo.controller.api.ReportTemplateControllerApi;
import kg.home.demo.model.*;
import kg.home.demo.service.ReportTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ReportTemplateController implements ReportTemplateControllerApi {
    private final ReportTemplateService service;

    @Override
    public ResponseEntity<PageResult<MdReportIn>> list(int page, int size) {
        return ResponseEntity.ok(service.findPage(PageRequest.of(page, size, Sort.by("id"))));
    }

    @Override
    public ResponseEntity<ReportTemplate> get(Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @Override
    public ResponseEntity<ReportDesignerLookups> lookups() {
        return ResponseEntity.ok(service.lookups());
    }

    @Override
    public ResponseEntity<Void> validate(ReportTemplateRequest request) {
        service.validate(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ReportTemplate> create(ReportTemplateRequest request) {
        var result = service.create(request);
        return ResponseEntity.created(URI.create("/api/incoming-report-templates/" + result.report().id())).body(result);
    }

    @Override
    public ResponseEntity<ReportTemplate> update(Long id, ReportTemplateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
