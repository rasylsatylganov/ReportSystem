package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "DR_TYPE_POSITION",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_DR_TYPE_POSITION_NAME", columnNames = "NAME_TYPE_POSITION")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DrTypePositionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dr_type_position_seq_gen")
    @SequenceGenerator(name = "dr_type_position_seq_gen", sequenceName = "DR_TYPE_POSITION_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @Column(name = "NAME_TYPE_POSITION", nullable = false, length = 50)
    private String nameTypePosition;
}