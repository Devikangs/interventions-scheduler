package dev.ace.api;

import dev.ace.service.AiReportService;
import dev.ace.service.SchedulingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController @RequestMapping("/api/v1/reports")
public class ReportController {
    private final AiReportService aiReportService;
    private final SchedulingService schedulingService;

    public ReportController(AiReportService aiReportService, SchedulingService schedulingService) {
        this.aiReportService = aiReportService;
        this.schedulingService = schedulingService;
    }

    public record ReportResponse(String report){}

    @PostMapping("/schedule")
    public ReportResponse scheduleReport(
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)OffsetDateTime to,
            @RequestParam(required = false) String constraints
            ){
        var plainText =  schedulingService.buildPlainSchedule(from,to);
        var text = aiReportService.summarizeSchedule(plainText,constraints);
        return new ReportResponse(text);
    }
}
