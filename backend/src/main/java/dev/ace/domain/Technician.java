package dev.ace.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Objects;
import java.util.Set;

@Entity
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 80)
    private String name;

    @ElementCollection
    @CollectionTable(name="technician_skills", joinColumns = @JoinColumn(name="technician_id"))
    private Set<@NotBlank String> skills;

    @NotBlank
    private String homeBase;

    public Technician() {
    }

    public Technician(Long id, String name, Set<@NotBlank String> skills, String homeBase) {
        this.id = id;
        this.name = name;
        this.skills = skills;
        this.homeBase = homeBase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public String getHomeBase() {
        return homeBase;
    }

    public void setHomeBase(String homeBase) {
        this.homeBase = homeBase;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Technician that = (Technician) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(skills, that.skills) && Objects.equals(homeBase, that.homeBase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, skills, homeBase);
    }
}
