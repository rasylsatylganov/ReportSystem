package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "MD_REPORT_IN",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_MD_REPORT_IN_CODE", columnNames = "CODE_REPORT_IN")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MdReportInEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "md_report_in_seq_gen")
    @SequenceGenerator(name = "md_report_in_seq_gen", sequenceName = "MD_REPORT_IN_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @Column(name = "CODE_REPORT_IN", nullable = false, length = 50)
    private String codeReportIn;

    @Column(name = "NAME_REPORT_IN", nullable = false, length = 300)
    private String nameReportIn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "TYPE_PERIOD_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MD_REPORT_IN_TPERIOD")
    )
    private DrTypePeriodEntity typePeriod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "TYPE_ENTERPRISE_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MD_REPORT_IN_TENT")
    )
    private DrTypeEnterpriseEntity typeEnterprise;

    @Column(
            name = "INDEX_ROW_SCAN_BEGIN",
            nullable = false,
            precision = 10,
            scale = 0
    )
    private Long indexRowScanBegin;

    @Column(
            name = "INDEX_ROW_SCAN_END",
            nullable = false,
            precision = 10,
            scale = 0
    )
    private Long indexRowScanEnd;
}