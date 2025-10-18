package dev.ace.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"intervention_id","technician_id"}))
public class Assignment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name="intervention_id") @NotNull
    private Intervention intervention;

    @ManyToOne(optional = false) @JoinColumn(name="technician_id") @NotNull
    private Technician technician;

    @NotNull
    private Boolean lead;

    public Assignment() {
    }

    public Assignment(Long id, Intervention intervention, Technician technician, Boolean lead) {
        this.id = id;
        this.intervention = intervention;
        this.technician = technician;
        this.lead = lead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Intervention getIntervention() {
        return intervention;
    }

    public void setIntervention(Intervention intervention) {
        this.intervention = intervention;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }

    public Boolean getLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(id, that.id) && Objects.equals(intervention, that.intervention) && Objects.equals(technician, that.technician) && Objects.equals(lead, that.lead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, intervention, technician, lead);
    }
}
