package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@BatchSize(size = 100)
@Table(
        name = "DR_TYPE_ENTERPRISE",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_DR_TYPE_ENT_CODE", columnNames = "CODE_TYPE_ENTERPRISE")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DrTypeEnterpriseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dr_type_enterprise_seq_gen")
    @SequenceGenerator(name = "dr_type_enterprise_seq_gen", sequenceName = "DR_TYPE_ENTERPRISE_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @Column(name = "CODE_TYPE_ENTERPRISE", nullable = false, length = 50)
    private String codeTypeEnterprise;

    @Column(name = "NAME_TYPE_ENTERPRISE", nullable = false, length = 200)
    private String nameTypeEnterprise;

    @Column(name = "SORT_INDEX", nullable = false, precision = 19, scale = 0)
    private Long sortIndex;
}
