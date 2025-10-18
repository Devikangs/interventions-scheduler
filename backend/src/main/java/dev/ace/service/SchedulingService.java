package dev.ace.service;

import dev.ace.domain.Assignment;
import dev.ace.domain.Intervention;
import dev.ace.domain.Technician;
import dev.ace.repo.AssignmentRepository;
import dev.ace.repo.InterventionRepository;
import dev.ace.repo.TechnicianRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulingService {

    private final InterventionRepository interventionRepository;
    private final TechnicianRepository technicianRepository;
    private final AssignmentRepository assignmentRepository;


    public SchedulingService(InterventionRepository interventionRepository, TechnicianRepository technicianRepository, AssignmentRepository assignmentRepository) {
        this.interventionRepository = interventionRepository;
        this.technicianRepository = technicianRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public record Conflict(Long interventionA, Long interventionB, String reason){}
    public record ConflictCluster(String location, List<Long> interventionIds, String summary){}
    public record TechConflict(Long technicianId, Long techA, Long techB, String reason){}

    public List<Conflict> detectConflicts(OffsetDateTime from, OffsetDateTime to){
        var all = interventionRepository.findOverlapping(from, to);
        Map<String,List<Intervention>> byLoc = all.stream().collect(Collectors.groupingBy(Intervention::getLocation));
        List<Conflict> out = new ArrayList<>();
        for (var entry : byLoc.entrySet()){
            var list = entry.getValue();
            for (int i=0;i<list.size();i++){
                for (int j=i+1;j<list.size();j++){
                    var a = list.get(i); var b = list.get(j);
                    boolean overlap = a.getStartAt().isBefore(b.getEndAt()) && b.getStartAt().isBefore(a.getEndAt());
                    if (overlap) out.add(new Conflict(a.getId(), b.getId(), "Same location & overlapping time"));
                }
            }
        }
        return out;
    }

    public List<ConflictCluster> detectConflictClusters(OffsetDateTime from, OffsetDateTime to){
        var list = interventionRepository.findOverlapping(from, to);
        Map<String, List<Intervention>> byLoc = list.stream().collect(Collectors.groupingBy(Intervention::getLocation));
        List<ConflictCluster> clusters = new ArrayList<>();

        for (var entry : byLoc.entrySet()){
            String location = entry.getKey();
            var items = entry.getValue();
            Map<Long, Set<Long>> g = new HashMap<>();
            for (var it : items) g.put(it.getId(), new HashSet<>());
            for (int i=0;i<items.size();i++){
                for (int j=i+1;j<items.size();j++){
                    var a = items.get(i); var b = items.get(j);
                    boolean overlap = a.getStartAt().isBefore(b.getEndAt()) && b.getStartAt().isBefore(a.getEndAt());
                    if (overlap){ g.get(a.getId()).add(b.getId()); g.get(b.getId()).add(a.getId()); }
                }
            }
            Set<Long> seen = new HashSet<>();
            for (var id : g.keySet()){
                if (seen.contains(id)) continue;
                List<Long> comp = new ArrayList<>();
                Deque<Long> dq = new ArrayDeque<>();
                dq.add(id); seen.add(id);
                while(!dq.isEmpty()){
                    var u = dq.poll();
                    comp.add(u);
                    for (var v : g.get(u)) if (seen.add(v)) dq.add(v);
                }
                if (comp.size() > 1){
                    clusters.add(new ConflictCluster(location, comp, "Overlapping at " + location + " (" + comp.size() + " interventions)"));
                }
            }
        }
        return clusters;
    }

    public List<TechConflict> detectTechnicianConflicts(OffsetDateTime from, OffsetDateTime to){
        var interventions = interventionRepository.findOverlapping(from, to).stream()
                .collect(Collectors.toMap(Intervention::getId, i -> i));
        var allAssignments = assignmentRepository.findAll().stream()
                .filter(a -> interventions.containsKey(a.getIntervention().getId()))
                .collect(Collectors.groupingBy(a -> a.getTechnician().getId()));

        List<TechConflict> out = new ArrayList<>();
        for (var e : allAssignments.entrySet()){
            Long techId = e.getKey();
            var list = e.getValue();
            for (int i=0;i<list.size();i++){
                for (int j=i+1;j<list.size();j++){
                    var ia = interventions.get(list.get(i).getIntervention().getId());
                    var ib = interventions.get(list.get(j).getIntervention().getId());
                    boolean overlap = ia.getStartAt().isBefore(ib.getEndAt()) && ib.getStartAt().isBefore(ia.getEndAt());
                    if (overlap) out.add(new TechConflict(techId, ia.getId(), ib.getId(), "Technician double-booked"));
                }
            }
        }
        return out;
    }

    public boolean technicianEligible(Technician t, Intervention i){
        return t.getSkills().containsAll(i.getRequiredSkills());
    }

    @Transactional
    public Assignment assign(Long interventionId, Long technicianId, boolean lead){
        var i = interventionRepository.findById(interventionId).orElseThrow();
        var t = technicianRepository.findById(technicianId).orElseThrow();
        if (!technicianEligible(t, i)){
            throw new IllegalArgumentException("Technician lacks required skills.");
        }
        var a = new Assignment();
        a.setIntervention(i); a.setTechnician(t); a.setLead(lead);
        return assignmentRepository.save(a);
    }

    public String buildPlainSchedule(OffsetDateTime from, OffsetDateTime to){
        return interventionRepository.findOverlapping(from, to).stream()
                .map(i -> String.format("[%s] %s (%s) %s → %s | needs: %s",
                        i.getAccelerator(), i.getTitle(), i.getLocation(),
                        i.getStartAt(), i.getEndAt(),
                        String.join(", ", i.getRequiredSkills())))
                .collect(Collectors.joining("\n"));
    }

}
