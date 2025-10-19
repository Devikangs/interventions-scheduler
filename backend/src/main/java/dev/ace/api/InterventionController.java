package dev.ace.api;

import dev.ace.domain.Intervention;
import dev.ace.repo.InterventionRepository;
import dev.ace.service.SchedulingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import java.time.OffsetDateTime;
import java.util.List;

@RestController @RequestMapping("/api/v1/interventions")
public class InterventionController {

    private final InterventionRepository interventionRepository;
    private final SchedulingService schedulingService;

    public InterventionController(InterventionRepository interventionRepository, SchedulingService schedulingService) {
        this.interventionRepository = interventionRepository;
        this.schedulingService = schedulingService;
    }

    @GetMapping
    public List<Intervention> list(){
        return interventionRepository.findAll();
    }

    @PostMapping
    public Intervention create(
            @RequestBody @Valid Intervention i
    ){
        System.out.println(i);
        return interventionRepository.save(i);
    }

    @GetMapping("/window")
    public List<Intervention> within(
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ){
        return interventionRepository.findOverlapping(from,to);
    }

    @GetMapping("/conflicts")
    public List<SchedulingService.Conflict> conflicts(
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ){
        return schedulingService.detectConflicts(from,to);
    }

    @GetMapping("/conflict-clusters")
    public List<SchedulingService.ConflictCluster> conflictClusters(
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ){
        return schedulingService.detectConflictClusters(from,to);
    }

    @GetMapping("/tech-conflicts")
    public List<SchedulingService.TechConflict> techConflicts(
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ){
        return schedulingService.detectTechnicianConflicts(from,to);
    }


}
