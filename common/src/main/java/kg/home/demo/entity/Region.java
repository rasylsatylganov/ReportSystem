package kg.home.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "REGION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "region_seq_gen")
    @SequenceGenerator(
            name = "region_seq_gen",
            sequenceName = "REGION_SEQ",
            allocationSize = 1
    )
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CODE_REGION", length = 3, nullable = false)
    private String codeRegion;

    @Column(name = "NAME_REGION", length = 100, nullable = false)
    private String nameRegion;

    @Column(name = "ORDER_NUMBER")
    private Integer orderNumber;

    // 👇 Связь "один регион → много стран"
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Country> countries;
}
