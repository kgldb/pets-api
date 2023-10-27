package de.agileim.pets.persist;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "pet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 256)
    private String name;

    @Size(max = 256)
    private String tag;

    @Override
    public int hashCode() {
        return 13;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PetEntity other = (PetEntity) obj;
        if (id != null && id.equals(other.getId())) {
            return true;
        }
        if (id == null && other.getId() == null) {
            return Objects.equals(name, other.name) && Objects.equals(tag, other.tag);
        }
        return false;
    }
}
