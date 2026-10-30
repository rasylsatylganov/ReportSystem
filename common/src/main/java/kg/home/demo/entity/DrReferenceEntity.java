package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "DR_REFERENCE",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_DR_REFERENCE_CODE", columnNames = "CODE_REFERENCE")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DrReferenceEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dr_reference_seq_gen")
    @SequenceGenerator(name = "dr_reference_seq_gen", sequenceName = "DR_REFERENCE_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @Column(name = "CODE_REFERENCE", nullable = false, length = 50)
    private String codeReference;

    @Column(name = "NAME_REFERENCE", nullable = false, length = 300)
    private String nameReference;

    @Column(name = "SORT_INDEX", nullable = false, precision = 19, scale = 0)
    private Long sortIndex;
}
