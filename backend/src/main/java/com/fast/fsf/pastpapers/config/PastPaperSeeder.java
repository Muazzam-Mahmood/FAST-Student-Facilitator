package com.fast.fsf.pastpapers.config;

import com.fast.fsf.pastpapers.domain.PastPaper;
import com.fast.fsf.pastpapers.persistence.PastPaperRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class PastPaperSeeder implements CommandLineRunner {

    private final PastPaperRepository paperRepository;

    public PastPaperSeeder(PastPaperRepository paperRepository) {
        this.paperRepository = paperRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (paperRepository.count() > 0) {
            // Clearing existing papers to ensure exact match with test requirements
            paperRepository.deleteAll();
        }

        System.out.println("DEBUG [PastPaperSeeder]: Seeding past papers...");

        paperRepository.saveAll(Arrays.asList(
            createPaper("Database Systems", "CS-3005", "Fall 2023", "MIDTERM", "Dr. Ali", "https://drive.google.com/drive/folders/1b8syVaHAJ1jCM70t8LvxRqeaAoGeHyK9", true),
            createPaper("Applied Physics", "PHY-1001", "Fall 2023", "FINAL", "Dr. Usman", "https://drive.google.com/drive/folders/1Iy6uJGHFmvTd3pMe1jkKuEFUkCOc0IJN", true),
            createPaper("Calculus", "MTH-1001", "Spring 2023", "MIDTERM", "Dr. Kamran", "https://drive.google.com/drive/folders/1PvyVrVdYE5DaMN1LGM-Zk5UmECXbcPvd", true),
            createPaper("Discrete Structures", "CS-1005", "Fall 2023", "FINAL", "Dr. Irfan", "https://drive.google.com/drive/folders/1VhK2MaXjLo-O5oGzOM6v5-kDYg94Ry54", true),
            createPaper("Cloud Computing", "CS-4020", "Fall 2023", "MIDTERM", "Dr. Hassan", "https://drive.google.com/drive/folders/1qHoYQsuz-jkgLdozkh1HQb_DcTbPdWBR", true),
            createPaper("Digital Logic Design", "CS-2001", "Fall 2023", "MIDTERM", "Dr. Asghar", "https://drive.google.com/drive/folders/1SZ2HkZJ02xq9oy5_RdFOeAur7IiSvHaN", true),
            createPaper("Digital Logic Design Lab", "CS-2001L", "Fall 2023", "QUIZ", "Dr. Asghar", "https://drive.google.com/drive/folders/1MtjPz-sLc0WhQFeQHmsnRUUxwpBdfjAv", true),
            createPaper("Islamic Studies", "HUM-1001", "Fall 2023", "FINAL", "Dr. Bilal", "https://drive.google.com/drive/folders/1mw8pSWsPhIFM9rRcSQQWF-OfYKvqz8WE", true),
            createPaper("Linear Algebra", "MTH-1002", "Fall 2023", "MIDTERM", "Dr. Hira", "https://drive.google.com/drive/folders/1SUkRnSiQkyVHohHoIDXOZ6T_gWkFHyrF", true),
            createPaper("Probability and Statistics", "MTH-2001", "Fall 2023", "FINAL", "Dr. Sadaf", "https://drive.google.com/drive/folders/1knOsNuexBD1a86aFrgHUp4gym6U6ja1V", true),
            createPaper("Object Oriented Programming", "CS-1004", "Spring 2024", "MIDTERM", "Dr. Naveed", "https://drive.google.com/drive/folders/OOP_PENDING", false)
        ));
    }

    private PastPaper createPaper(String name, String code, String sem, String type, String instructor, String link, boolean approved) {
        PastPaper p = new PastPaper();
        p.setCourseName(name);
        p.setCourseCode(code);
        p.setSemesterYear(sem);
        p.setExamType(type);
        p.setInstructorName(instructor);
        p.setGoogleDriveLink(link);
        p.setApproved(approved);
        p.setUploadedAt(LocalDateTime.now());
        p.setOwnerEmail("admin@nu.edu.pk");
        p.setOwnerName("FSF Admin");
        return p;
    }
}
