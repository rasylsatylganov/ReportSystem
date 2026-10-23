package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_REFERENCE_ITEM_VALUE")
@Getter
@Setter
@NoArgsConstructor
public class TbReferenceItemValue {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tb_reference_item_value_seq_gen"
    )
    @SequenceGenerator(
            name = "tb_reference_item_value_seq_gen",
            sequenceName = "TB_REFERENCE_ITEM_VALUE_SEQ",
            allocationSize = 1
    )
    @Column(name = "ID", nullable = false, precision = 19, scale = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REFERENCE_ITEM_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_TB_RIV_RI")
    )
    private TbReferenceItem referenceItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REPORT_IN_REFERENCE_ITEM_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_TB_RIV_MD_ITEM")
    )
    private MdReportInReferenceItem reportInReferenceItem;

    @Column(name = "VALUE_NUMBER", precision = 28, scale = 6)
    private BigDecimal valueNumber;

    @Column(name = "VALUE_STRING", length = 2000)
    private String valueString;

    @Column(name = "VALUE_DATETIME", columnDefinition = "DATE")
    private LocalDateTime valueDatetime;

    @Column(name = "INDEX_ROW", nullable = false, precision = 10, scale = 0)
    private Long indexRow;
}