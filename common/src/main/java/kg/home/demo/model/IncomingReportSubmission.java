package kg.home.demo.model;

public record IncomingReportSubmission(Long periodId, String periodName,
                                       Long enterpriseId, String enterpriseName, String enterpriseCode) {}
