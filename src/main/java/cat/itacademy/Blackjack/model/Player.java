package cat.itacademy.Blackjack.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("players")
public class Player {


    @Id
    private Long id;


    @Column("name")
    private String name;



    @CreatedDate
    @Column("created_at")
    private Instant createdAt;


    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
