package com.fast.fsf.timetable.template;

import com.fast.fsf.timetable.domain.TimetableEntry;
import com.fast.fsf.timetable.persistence.TimetableEntryRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Excel parser for the FAST NUCES timetable format (FSC TT Spring 2026).
 *
 * SHEET STRUCTURE:
 *  Row 1 : Title
 *  Row 2 : Dept-Year column headers (BCS-2025, BCS-2024 … each spans 3 cols)
 *  Row 3 : Time-period labels (08:30-10:00, 10:00-11:30 … at cols 6, 15, 24, 33, 42, 51, 60, 69)
 *  Row 4 : Sub-period number labels (10/20/30/40/50/60 – visual only, not used)
 *  Row 5+ : Data rows
 *   - Col 1 (A): Day name (Mon/Tue/…) – vertically merged across many rows
 *   - Col 2 (B): Room name           – vertically merged across a few rows
 *   - Col 3+ : Class cells; each cell content = "Course (DeptCodeYrSection): Instructor"
 *              e.g. "OOP (BCS-2A): Hina I"  or  "DB Lab (BCS-4D): Seemab A"
 *              Lab sub-groups look like "OOP Lab (BCS-2A1): ..." or "(BCS-2H2)"
 *
 * PARSING RULES:
 *  1. Time period of a cell is determined by which time-slot column block it falls under.
 *  2. Dept+batch+section is read FROM THE CELL TEXT (not from which column).
 *     DeptCode mapping: BCS→CS, BSE→SE, BDS→DS, BAI→AI, BCY→CYS, MCS→CS, MSP→SE, MCY→CYS, MDS→DS
 *     Batch from semester number: sem 2→batch 25, 4→24, 6→23, 8→22 (based on year 2026)
 *  3. Lab sub-groups (section = "A1", "B2" etc.) → strip the digit to get base section ("A", "B").
 *     A unique entry per (day, time, dept, batch, section) is kept – no duplicates.
 */
@Component
public class ExcelTimetableProcessor extends AbstractTimetableProcessor {

    // Maps the dept code in the cell to our internal dept identifier
    private static final Map<String, String> DEPT_MAP = Map.ofEntries(
            Map.entry("BCS", "CS"),
            Map.entry("BSE", "SE"),
            Map.entry("BDS", "DS"),
            Map.entry("BAI", "AI"),
            Map.entry("BCY", "CYS"),
            Map.entry("MCS", "CS"),
            Map.entry("MSP", "SE"),
            Map.entry("MCY", "CYS"),
            Map.entry("MDS", "DS")
    );

    // "Course (DeptYrSection): Instructor"  – DeptYr e.g. BCS-2, BCS-4, BSE-2
    // Section can be "A", "B", "A1", "B2" etc.
    private static final Pattern CELL_PATTERN = Pattern.compile(
            "^(.+?)\\s*\\(([A-Z]{2,4})-?(\\d{1,2})([A-Z]\\d?)\\s*\\)\\s*(?::\\s*(.*))?$",
            Pattern.DOTALL
    );

    public ExcelTimetableProcessor(TimetableEntryRepository timetableRepository,
                                   ApplicationEventPublisher eventPublisher) {
        super(timetableRepository, eventPublisher);
    }

    @Override
    protected List<TimetableEntry> parseEntries(InputStream is, String ownerName,
                                                String ownerEmail) throws Exception {
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();

        // ── 1. Build col → time-period map from row 3 ────────────────────────
        Map<Integer, String> colToTime = buildColToTimeMap(sheet, formatter);
        if (colToTime.isEmpty()) {
            workbook.close();
            throw new Exception(
                "Could not detect time slots in row 3. " +
                "Expected format: '08:30-10:00' at columns 6, 15, 24, 33, 42, 51, 60, 69.");
        }
        System.out.println("DEBUG: Time slots detected: " + new TreeMap<>(getTimeSlotsFromRow3(sheet, formatter)));

        // ── 2. Parse data rows (row 5 onwards) ────────────────────────────────
        List<TimetableEntry> entries = new ArrayList<>();
        // Dedup set: day+time+dept+batch+section (drop lab sub-group duplicates)
        Set<String> seen = new HashSet<>();

        String currentDay = "";
        Map<String, String> dayMap = new LinkedHashMap<>();
        dayMap.put("Mon", "MONDAY"); dayMap.put("Tue", "TUESDAY");
        dayMap.put("Wed", "WEDNESDAY"); dayMap.put("Thu", "THURSDAY");
        dayMap.put("Fri", "FRIDAY"); dayMap.put("Sat", "SATURDAY");
        dayMap.put("Sun", "SUNDAY");

        // Cache last seen room per row-window for vertical-merge inheritance
        String lastRoom = "";

        for (int r = 4; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            // Day (col 1)
            String dayRaw = getCellString(row, 0, formatter);
            if (!dayRaw.isEmpty()) {
                String key = dayRaw.substring(0, Math.min(3, dayRaw.length()));
                if (dayMap.containsKey(key)) {
                    currentDay = dayMap.get(key);
                    // Do NOT reset lastRoom — rooms span across day boundaries in the sheet layout
                }
            }
            if (currentDay.isEmpty()) continue;

            // Room (col 2) – inherit vertically
            String room = getCellString(row, 1, formatter);
            if (!room.isEmpty() && !room.equalsIgnoreCase("Room") && !room.equalsIgnoreCase("Days")) {
                lastRoom = room;
            }
            if (lastRoom.isEmpty()) continue;
            room = lastRoom;

            // Scan class cells (col 3 onwards)
            int maxDataCol = (sheet.getRow(3) != null) ? sheet.getRow(3).getLastCellNum() : 84;
            for (int c = 2; c < maxDataCol; c++) {
                Cell cell = row.getCell(c);
                if (cell == null) continue;
                String txt = formatter.formatCellValue(cell).trim();
                if (txt.isEmpty()) continue;

                String timePeriod = colToTime.getOrDefault(c, null);
                if (timePeriod == null) continue;

                Matcher m = CELL_PATTERN.matcher(txt);
                if (!m.matches()) continue;

                String courseName  = m.group(1).trim();
                String deptCode    = m.group(2).trim().toUpperCase();
                String semStr      = m.group(3).trim();
                String sectionRaw  = m.group(4).trim().toUpperCase(); // "A", "B", "A1", "B2"
                String instructor  = m.group(5) != null ? m.group(5).trim() : "Staff";
                if (instructor.isEmpty()) instructor = "Staff";

                String dept = DEPT_MAP.getOrDefault(deptCode, deptCode);

                // Batch from semester
                int batch = 24;
                try {
                    int sem = Integer.parseInt(semStr);
                    batch = 2026 - (sem + 1) / 2;
                } catch (NumberFormatException ignored) {}
                String batchStr = String.valueOf(batch % 100);

                // Strip sub-group digit from section: "A1" → "A", "B2" → "B"
                String section = sectionRaw.replaceAll("\\d+$", "");
                if (section.isEmpty()) section = sectionRaw;

                // Split "08:30-10:00" on the hyphen between two clock times
                // e.g. "08:30-10:00" → ["08:30", "10:00"]
                // Using a regex split that finds the dash between digit sequences
                String[] times = timePeriod.split("(?<=\\d{2}:\\d{2})-(?=\\d{2}:\\d{2})");
                String startTime = times[0].trim();
                String endTime   = times.length > 1 ? times[1].trim() : "";

                // Dedup key: same slot, same section shouldn't appear twice (e.g. A1 and A2)
                String dedupKey = currentDay + "|" + timePeriod + "|" + dept + "|" + batchStr + "|" + section;
                if (seen.contains(dedupKey)) {
                    System.out.println("DEBUG: Skipping duplicate: " + dedupKey + " (" + courseName + ")");
                    continue;
                }
                seen.add(dedupKey);

                TimetableEntry entry = new TimetableEntry();
                entry.setCourseName(courseName);
                entry.setDepartment(dept);
                entry.setBatch(batchStr);
                entry.setSection(section);
                entry.setInstructorName(instructor);
                entry.setDayOfWeek(currentDay);
                entry.setRoomNumber(room);
                entry.setStartTime(startTime);
                entry.setEndTime(endTime);
                entry.setOwnerName(ownerName);
                entry.setOwnerEmail(ownerEmail);
                entry.setApproved(true);
                entry.setFlagged(false);
                entries.add(entry);
            }
        }

        workbook.close();
        System.out.println("DEBUG: Total entries parsed = " + entries.size());
        return entries;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Reads row 3 for time slot labels and builds a mapping from every data column
     * to the time period it belongs to (each cell falls under the nearest time slot
     * column to its left).
     */
    private Map<Integer, String> buildColToTimeMap(Sheet sheet, DataFormatter formatter) {
        Map<Integer, String> slotCols = getTimeSlotsFromRow3(sheet, formatter);
        if (slotCols.isEmpty()) return Collections.emptyMap();

        List<Integer> sortedSlotCols = new ArrayList<>(slotCols.keySet());
        Collections.sort(sortedSlotCols);

        Map<Integer, String> colToTime = new HashMap<>();
        int maxCol = 84;
        for (int c = 2; c < maxCol; c++) {
            String time = null;
            for (int s : sortedSlotCols) {
                if (s <= c) time = slotCols.get(s);
                else break;
            }
            if (time != null) colToTime.put(c, time);
        }
        return colToTime;
    }

    private Map<Integer, String> getTimeSlotsFromRow3(Sheet sheet, DataFormatter formatter) {
        Map<Integer, String> slots = new LinkedHashMap<>();
        Row row3 = sheet.getRow(2); // 0-indexed
        if (row3 == null) return slots;
        for (int c = 0; c < row3.getLastCellNum(); c++) {
            String val = formatter.formatCellValue(row3.getCell(c)).trim();
            if (val.matches("\\d{1,2}:\\d{2}-\\d{2}:\\d{2}")) {
                slots.put(c, val);
            }
        }
        return slots;
    }

    private String getCellString(Row row, int colIdx, DataFormatter formatter) {
        if (row == null) return "";
        Cell cell = row.getCell(colIdx);
        if (cell == null) return "";
        return formatter.formatCellValue(cell).trim();
    }
}
