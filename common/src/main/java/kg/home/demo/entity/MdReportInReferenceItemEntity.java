package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "MD_REPORT_IN_REFERENCE_ITEM",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_MD_RIRI_REPORT_ITEM", columnNames = {"REPORT_IN_ID", "REFERENCE_ITEM_ID"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MdReportInReferenceItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "md_report_in_ref_item_seq_gen")
    @SequenceGenerator(name = "md_report_in_ref_item_seq_gen", sequenceName = "MD_REPORT_IN_REF_ITEM_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REPORT_IN_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MD_RIRI_REPORT")
    )
    private MdReportInEntity reportIn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REFERENCE_ITEM_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MD_RIRI_ITEM")
    )
    private DrReferenceItemEntity referenceItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "TYPE_FIELD_VALUE_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MD_RIRI_TFIELD")
    )
    private DrTypeFieldEntity typeFieldValue;

    @Column(name = "INDEX_COLUMN", nullable = false, precision = 10, scale = 0)
    private Long indexColumn;

    @Column(name = "ORDER_SORT", precision = 10, scale = 0)
    private Long orderSort;

    @Column(name = "IS_REQUIRED", nullable = false, precision = 1, scale = 0)
    private Integer isRequired = 1;
}