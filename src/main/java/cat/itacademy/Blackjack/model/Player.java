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

/**
 * Player (MySQL via R2DBC).
 * OJO: No es JPA. No uses jakarta.persistence.* ni @Entity.
 * - @Table/@Column son de Spring Data (relational).
 * - El id se autogenera en la BD (AUTO_INCREMENT).
 * - createdAt/updatedAt usan auditoría de Spring Data (actívala en config si quieres que se rellenen solos).
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("players")
public class Player {

    /** Clave primaria (AUTO_INCREMENT en la tabla). */
    @Id
    private Long id;

    /** Nombre del jugador. Define UNIQUE en tu DDL si quieres evitar duplicados. */
    @Column("name")
    private String name;

    /** Marca de creación (requiere R2dbcAuditing para autocompletar). */
    //la fecha y hora actual de la creación del jugador
    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    /** Marca de última actualización (requiere R2dbcAuditing). */
    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
