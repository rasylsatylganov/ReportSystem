package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "MD_REPORT_IN_REFERENCE",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_MD_RIR_REPORT_REF", columnNames = {"REPORT_IN_ID", "REFERENCE_ID"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MdReportInReference {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "md_report_in_reference_seq_gen")
    @SequenceGenerator(name = "md_report_in_reference_seq_gen", sequenceName = "MD_REPORT_IN_REFERENCE_SEQ", allocationSize = 1
    )
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REPORT_IN_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MD_RIR_REPORT")
    )
    private MdReportIn reportIn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REFERENCE_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MD_RIR_REF")
    )
    private DrReference reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "TYPE_POSITION_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MD_RIR_POS")
    )
    private DrTypePosition typePosition;

    @Column(name = "INDEX_NUM", nullable = false, precision = 10, scale = 0)
    private Long indexNum;

    @Column(name = "ORDER_SORT", precision = 10, scale = 0)
    private Long orderSort;
}