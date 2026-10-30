package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "DR_TYPE_PERIOD",
        uniqueConstraints = @UniqueConstraint(
                name = "UQ_DR_TYPE_PERIOD_CODE",
                columnNames = "CODE_TYPE_PERIOD"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class DrTypePeriodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dr_type_period_seq_gen")
    @SequenceGenerator(
            name = "dr_type_period_seq_gen",
            sequenceName = "DR_TYPE_PERIOD_SEQ",
            allocationSize = 1
    )
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @Column(name = "CODE_TYPE_PERIOD", nullable = false, length = 3)
    private String codeTypePeriod;

    @Column(name = "NAME_TYPE_PERIOD", nullable = false, length = 100)
    private String nameTypePeriod;

    @Column(name = "SORT_INDEX", nullable = false, precision = 19, scale = 0)
    private Long sortIndex;
}
