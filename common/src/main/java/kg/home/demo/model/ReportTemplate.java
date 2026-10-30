package kg.home.demo.model;

import java.util.List;

public record ReportTemplate(
        MdReportIn report,
        List<MdReportInReference> references,
        List<MdReportInReferenceItem> fields,
        String revision
) {}
