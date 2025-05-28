package com.yoanesber.spring.security.jwt_auth_postgresql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Role is an entity class that represents a role in the database.
 * The @Data annotation from Lombok generates getters, setters, equals, hashCode, and toString methods.
 * The @NoArgsConstructor and @AllArgsConstructor annotations are used to create constructors for the class.
 * The @Entity annotation indicates that this class is a JPA entity.
 * The @Table annotation specifies the name of the table in the database.
 */

@AllArgsConstructor // Helps create DTO objects easily (useful when converting from entities).
@Data
@Getter
@NoArgsConstructor // Required for Jackson deserialization when receiving JSON requests.
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;
    
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    public List<User> users = new ArrayList<>();

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}