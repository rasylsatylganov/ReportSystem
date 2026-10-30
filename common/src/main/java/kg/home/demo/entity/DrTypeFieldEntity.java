package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "DR_TYPE_FIELD",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_DR_TYPE_FIELD_CODE",
                        columnNames = "CODE_TYPE_FIELD"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DrTypeFieldEntity extends BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "dr_type_field_seq_gen"
    )
    @SequenceGenerator(
            name = "dr_type_field_seq_gen",
            sequenceName = "DR_TYPE_FIELD_SEQ",
            allocationSize = 1
    )
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @Column(name = "CODE_TYPE_FIELD", nullable = false, length = 20)
    private String codeTypeField;

    @Column(name = "NAME_TYPE_FIELD", nullable = false, length = 100)
    private String nameTypeField;
}