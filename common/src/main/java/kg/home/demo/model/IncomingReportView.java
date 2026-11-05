package kg.home.demo.model;

import java.util.List;

public record IncomingReportView(
        MdReportIn report,
        DrPeriod period,
        DrEnterprise enterprise,
        List<Column> columns,
        PageResult<Row> rows
) {
    public record Column(String key, String name, String groupKey, String groupName,
                         String dataType, boolean dimension) {}

    public record Row(Long indexRow, List<Cell> cells) {}

    // Values are strings so NUMBER(28,6) and large group IDs retain their precision in JavaScript.
    public record Cell(String value, String groupKey) {}
}
