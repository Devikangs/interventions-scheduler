package dev.ace.api;

import dev.ace.domain.Assignment;
import dev.ace.domain.Technician;
import dev.ace.repo.TechnicianRepository;
import dev.ace.service.SchedulingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/v1/technicians")
public class TechnicianController {
    private final TechnicianRepository technicianRepository;
    private final SchedulingService schedulingService;


    public TechnicianController(TechnicianRepository technicianRepository, SchedulingService schedulingService) {
        this.technicianRepository = technicianRepository;
        this.schedulingService = schedulingService;
    }

    @GetMapping
    public List<Technician> list(){
        return technicianRepository.findAll();
    }

    @PostMapping
    public Technician create(
            @RequestBody @Valid Technician t
    ){
        return technicianRepository.save(t);
    }

    public Assignment assign(
            @RequestParam Long interventionId,
            @RequestParam Long technicianId,
            @RequestParam(defaultValue = "false") Boolean lead
    ){
        return schedulingService.assign(interventionId,technicianId,lead);
    }
}
