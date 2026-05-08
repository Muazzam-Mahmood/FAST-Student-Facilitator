package com.fast.fsf.timetable.template;

import com.fast.fsf.timetable.domain.TimetableEntry;
import com.fast.fsf.timetable.persistence.TimetableEntryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV implementation of the timetable processor.
 * Parses timetable data from CSV files and maps them to TimetableEntry entities.
 */
@Component
public class CsvTimetableProcessor extends AbstractTimetableProcessor {

    public CsvTimetableProcessor(TimetableEntryRepository timetableRepository, ApplicationEventPublisher eventPublisher) {
        super(timetableRepository, eventPublisher);
    }

    /**
     * Parses the CSV input stream into a list of TimetableEntry objects.
     */
    @Override
    protected List<TimetableEntry> parseEntries(InputStream is, String ownerName, String ownerEmail) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        List<TimetableEntry> entries = new ArrayList<>();
        String line;
        boolean isFirstLine = true;
        
        while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false; // Skip header
                continue;
            }
            
            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (parts.length >= 7) {
                TimetableEntry entry = new TimetableEntry();
                java.util.function.Function<String, String> clean = (s) -> {
                    String res = s.trim();
                    if (res.startsWith("\"") && res.endsWith("\"")) res = res.substring(1, res.length() - 1).trim();
                    return res;
                };

                String dept = clean.apply(parts[0]);
                if (dept.toUpperCase().startsWith("BS ")) dept = dept.substring(3).trim();
                if (dept.toUpperCase().startsWith("MS ")) dept = dept.substring(3).trim();
                entry.setDepartment(dept);
                
                String rawBatch = clean.apply(parts[1]);
                entry.setBatch(rawBatch.length() > 2 ? rawBatch.substring(rawBatch.length() - 2) : rawBatch);
                entry.setSection(clean.apply(parts[2]));
                entry.setDayOfWeek(clean.apply(parts[3]));
                
                String timeSlot = clean.apply(parts[4]);
                String[] times = timeSlot.split("-");
                entry.setStartTime(times[0].trim());
                entry.setEndTime(times.length > 1 ? times[1].trim() : "");
                
                entry.setCourseName(clean.apply(parts[5]));
                entry.setRoomNumber(parts.length >= 7 ? clean.apply(parts[6]) : "");
                entry.setInstructorName(parts.length >= 8 ? clean.apply(parts[7]) : "TBD");
                
                entry.setOwnerName(ownerName);
                entry.setOwnerEmail(ownerEmail);
                entry.setApproved(true);
                entries.add(entry);
            }
        }
        return entries;
    }
}
