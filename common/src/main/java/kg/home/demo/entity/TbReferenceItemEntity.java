package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_REFERENCE_ITEM")
@Getter
@Setter
@NoArgsConstructor
public class TbReferenceItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "tb_reference_item_seq_gen")
    @SequenceGenerator(name = "tb_reference_item_seq_gen", sequenceName = "TB_REFERENCE_ITEM_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 19, scale = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "PERIOD_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_TB_RI_PERIOD")
    )
    private DrPeriodEntity period;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ENTERPRISE_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_TB_RI_ENT")
    )
    private DrEnterpriseEntity enterprise;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REFERENCE_ITEM_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_TB_RI_ITEM")
    )
    private DrReferenceItemEntity referenceItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REPORT_IN_REFERENCE_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_TB_RI_MD_REF")
    )
    private MdReportInReferenceEntity reportInReference;

    @Column(name = "INDEX_ROW", nullable = false, precision = 10, scale = 0)
    private Long indexRow;
}