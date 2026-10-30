package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "DR_REFERENCE_ITEM",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_DR_REF_ITEM_CODE", columnNames = {"REFERENCE_ID", "CODE_REFERENCE_ITEM"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DrReferenceItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dr_reference_item_seq_gen")
    @SequenceGenerator(name = "dr_reference_item_seq_gen", sequenceName = "DR_REFERENCE_ITEM_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REFERENCE_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_DR_REF_ITEM_REF")
    )
    private DrReferenceEntity reference;

    @Column(name = "CODE_REFERENCE_ITEM", nullable = false, length = 100)
    private String codeReferenceItem;

    @Column(name = "NAME_REFERENCE_ITEM", nullable = false, length = 300)
    private String nameReferenceItem;

    @Column(name = "SORT_INDEX", nullable = false, precision = 19, scale = 0)
    private Long sortIndex;
}
