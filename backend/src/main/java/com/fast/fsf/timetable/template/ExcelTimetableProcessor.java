package com.fast.fsf.timetable.template;

import com.fast.fsf.timetable.domain.TimetableEntry;
import com.fast.fsf.timetable.persistence.TimetableEntryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Excel implementation of the timetable processor.
 * Uses Apache POI to parse timetable data from Excel (.xlsx) files.
 * Handles merged cells and complex grid layouts.
 */
@Component
public class ExcelTimetableProcessor extends AbstractTimetableProcessor {

    public ExcelTimetableProcessor(TimetableEntryRepository timetableRepository, ApplicationEventPublisher eventPublisher) {
        super(timetableRepository, eventPublisher);
    }

    /**
     * Parses the Excel input stream into a list of TimetableEntry objects.
     */
    @Override
    protected List<TimetableEntry> parseEntries(InputStream is, String ownerName, String ownerEmail) throws Exception {
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        
        Map<Integer, String> timeSlots = new HashMap<>();
        for (int r = 0; r < 20; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            for (int c = 0; c < 50; c++) {
                String val = formatter.formatCellValue(row.getCell(c)).trim();
                if (val.matches("\\d{1,2}(?::\\d{2})?\\s*[-–—to]\\s*\\d{1,2}(?::\\d{2})?")) {
                    timeSlots.put(c, val.replace(" ", ""));
                    System.out.println("DEBUG: Detected Time Slot at col " + c + ": " + val);
                }
            }
        }
        
        if (timeSlots.isEmpty()) {
            workbook.close();
            throw new Exception("Could not detect time slots (e.g. 08:30-10:00) in the header rows. Check if the format is correct (e.g. 08:30 - 10:00).");
        }

        List<TimetableEntry> entries = new ArrayList<>();
        String currentDay = "";
        List<String> dayNamesShort = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        List<String> dayNamesFull = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

        for (int r = 0; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            String dayCol = formatter.formatCellValue(row.getCell(0)).trim();
            if (!dayCol.isEmpty()) {
                String tempDay = dayCol.split("\\s+")[0].trim();
                for (int i = 0; i < dayNamesShort.size(); i++) {
                    if (tempDay.equalsIgnoreCase(dayNamesShort.get(i)) || tempDay.toUpperCase().startsWith(dayNamesShort.get(i).toUpperCase())) {
                        currentDay = dayNamesFull.get(i);
                        break;
                    }
                }
            }

            if (currentDay.isEmpty()) continue;

            String room = formatter.formatCellValue(row.getCell(1)).trim();
            if (room.isEmpty() || room.equalsIgnoreCase("Room") || room.equalsIgnoreCase("Periods")) continue;

            for (Map.Entry<Integer, String> slot : timeSlots.entrySet()) {
                int col = slot.getKey();
                String time = slot.getValue();
                String cellContent = getCellValueHandlingMerges(sheet, r, col, formatter);
                if (cellContent == null) continue; // Skip merged cells after the first one

                if (!cellContent.isEmpty()) {
                    // More flexible regex: Course Name, then optional (Dept-SemSection), then optional : Instructor
                    // Example matches: "OOP (CS-2A) : Muazzam", "Calculus (MT-1B)", "English"
                    Pattern p = Pattern.compile("([^\\(:]+)(?:\\s*\\(([^\\)-]+)-?(\\d?)([A-Z]?)\\))?\\s*(?::\\s*(.+))?");
                    Matcher m = p.matcher(cellContent);
                    
                    if (m.find()) {
                        entry.setCourseName(m.group(1).trim());
                        
                        String rawDept = m.group(2) != null ? m.group(2).trim().toUpperCase() : "CS";
                        List<String> targetDepts = new ArrayList<>();
                        
                        // Shared departments that should show up for everyone
                        List<String> sharedPrefixes = Arrays.asList("MT", "MG", "SS", "HM", "HS", "HU", "MTH", "SL");
                        boolean isShared = sharedPrefixes.stream().anyMatch(rawDept::startsWith);
                        
                        if (isShared) {
                            targetDepts.addAll(Arrays.asList("CS", "SE", "AI", "DS", "CYS"));
                        } else {
                            String dept = "CS";
                            if (rawDept.contains("SE")) dept = "SE";
                            else if (rawDept.contains("AI")) dept = "AI";
                            else if (rawDept.contains("DS")) dept = "DS";
                            else if (rawDept.contains("CYS")) dept = "CYS";
                            targetDepts.add(dept);
                        }
                        
                        String batchVal = "24";
                        try {
                            String semStr = m.group(3);
                            int sem = (semStr != null && !semStr.isEmpty()) ? Integer.parseInt(semStr) : 4; // Default to 4 for Spring
                            int batchYear = 2026 - (sem + 1) / 2;
                            batchVal = String.valueOf(batchYear).substring(2);
                        } catch (Exception e) {
                            batchVal = "24";
                        }
                        
                        String sectionVal = m.group(4) != null ? m.group(4) : "A";
                        String instructorVal = m.group(5) != null ? m.group(5).trim() : "Staff";
                        
                        for (String d : targetDepts) {
                            TimetableEntry entry = new TimetableEntry();
                            entry.setCourseName(m.group(1).trim());
                            entry.setDepartment(d);
                            entry.setBatch(batchVal);
                            entry.setSection(sectionVal);
                            entry.setInstructorName(instructorVal);
                            entry.setDayOfWeek(currentDay);
                            entry.setRoomNumber(room);
                            
                            String[] times = time.split("-|–|to");
                            entry.setStartTime(times[0].trim());
                            entry.setEndTime(times.length > 1 ? times[times.length - 1].trim() : "");
                            
                            entry.setOwnerName(ownerName);
                            entry.setOwnerEmail(ownerEmail);
                            entry.setApproved(true);
                            entries.add(entry);
                        }
                    }
                }
            }
        }
        workbook.close();
        return entries;
    }

    private String getCellValueHandlingMerges(Sheet sheet, int rowIdx, int colIdx, DataFormatter formatter) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.isInRange(rowIdx, colIdx)) {
                // Only return content for the very first cell of the merged region to avoid duplicates
                if (rowIdx == region.getFirstRow() && colIdx == region.getFirstColumn()) {
                    Row r = sheet.getRow(region.getFirstRow());
                    if (r == null) return "";
                    Cell c = r.getCell(region.getFirstColumn());
                    return formatter.formatCellValue(c).trim();
                } else {
                    return null; // Skip subsequent cells in the merge
                }
            }
        }
        Row row = sheet.getRow(rowIdx);
        if (row == null) return "";
        Cell cell = row.getCell(colIdx);
        return formatter.formatCellValue(cell).trim();
    }
}
