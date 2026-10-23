package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "DR_PERIOD",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_DR_PERIOD_TYPE_CODE", columnNames = {"TYPE_PERIOD_ID", "CODE_PERIOD"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DrPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dr_period_seq_gen")
    @SequenceGenerator(name = "dr_period_seq_gen", sequenceName = "DR_PERIOD_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "TYPE_PERIOD_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_DR_PERIOD_TYPE")
    )
    private DrTypePeriod typePeriod;

    @Column(name = "CODE_PERIOD", nullable = false, length = 20)
    private String codePeriod;

    @Column(name = "NAME_PERIOD", nullable = false, length = 200)
    private String namePeriod;

    @Column(name = "DATE_BEGIN", nullable = false)
    private LocalDate dateBegin;

    @Column(name = "DATE_END", nullable = false)
    private LocalDate dateEnd;
}