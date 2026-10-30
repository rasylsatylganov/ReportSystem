package kg.home.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "DR_ENTERPRISE",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_DR_ENTERPRISE_CODE", columnNames = "CODE_ENTERPRISE")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DrEnterpriseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dr_enterprise_seq_gen")
    @SequenceGenerator(name = "dr_enterprise_seq_gen", sequenceName = "DR_ENTERPRISE_SEQ", allocationSize = 1)
    @Column(name = "ID", nullable = false, precision = 10, scale = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "TYPE_ENTERPRISE_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_DR_ENTERPRISE_TYPE")
    )
    private DrTypeEnterpriseEntity typeEnterprise;

    @Column(name = "CODE_ENTERPRISE", nullable = false, length = 20)
    private String codeEnterprise;

    @Column(name = "NAME_ENTERPRISE", nullable = false, length = 300)
    private String nameEnterprise;
}