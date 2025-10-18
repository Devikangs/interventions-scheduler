package dev.ace.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Intervention {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max=140)
    private String title;
    @NotBlank @Size(max=80)
    private String location;
    @NotBlank @Size(max=40)
    private String accelerator;

    @NotNull
    private OffsetDateTime startAt;
    @NotNull
    private OffsetDateTime endAt;

    @NotBlank
    private String type;
    @NotBlank
    private String status;

    @ElementCollection
    @CollectionTable(name="intervention_skills",joinColumns=@JoinColumn(name="intervention_id"))
    @Column(name="skill")
    private Set<@NotBlank String> requiredSkills;

    @Column(length = 1000)
    private String notes;

    public Intervention() {
    }

    public Intervention(
            Long id,
            String title,
            String location,
            String accelerator,
            OffsetDateTime startAt,
            OffsetDateTime endAt,
            String type,
            String status,
            Set<@NotBlank String> requiredSkills,
            String notes) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.accelerator = accelerator;
        this.startAt = startAt;
        this.endAt = endAt;
        this.type = type;
        this.status = status;
        this.requiredSkills = requiredSkills;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAccelerator() {
        return accelerator;
    }

    public void setAccelerator(String accelerator) {
        this.accelerator = accelerator;
    }

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(OffsetDateTime startAt) {
        this.startAt = startAt;
    }

    public OffsetDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(OffsetDateTime endAt) {
        this.endAt = endAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Intervention that = (Intervention) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(location, that.location) && Objects.equals(accelerator, that.accelerator) && Objects.equals(startAt, that.startAt) && Objects.equals(endAt, that.endAt) && Objects.equals(type, that.type) && Objects.equals(status, that.status) && Objects.equals(requiredSkills, that.requiredSkills) && Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, location, accelerator, startAt, endAt, type, status, requiredSkills, notes);
    }
}
