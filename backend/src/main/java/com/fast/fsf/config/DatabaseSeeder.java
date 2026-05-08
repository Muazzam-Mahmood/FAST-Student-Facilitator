package com.fast.fsf.config;

import com.fast.fsf.admin.domain.Admin;
import com.fast.fsf.pastpapers.domain.PastPaper;
import com.fast.fsf.timetable.domain.TimetableEntry;
import com.fast.fsf.admin.persistence.AdminRepository;
import com.fast.fsf.pastpapers.persistence.PastPaperRepository;
import com.fast.fsf.timetable.persistence.TimetableEntryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for seeding the database with initial data.
 */
@Configuration
public class DatabaseSeeder {

    /**
     * Seed the admins table with the 5 FSF developer/admin accounts.
     * Runs first (Order 1). Only inserts if not already present so
     * re-starts don't duplicate rows.
     */
    @Bean
    @Order(1)
    CommandLineRunner seedAdmins(AdminRepository adminRepo) {
        return args -> {
            System.out.println("DEBUG: Clearing and re-seeding FSF admins table...");
            adminRepo.deleteAll();

            adminRepo.saveAll(Arrays.asList(
                new Admin("l240363@lhr.nu.edu.pk", "Admin L240363"),
                new Admin("l243063@lhr.nu.edu.pk", "Admin L243063"),
                new Admin("l243023@lhr.nu.edu.pk", "Admin L243023"),
                new Admin("l243019@lhr.nu.edu.pk", "Admin L243019"),
                new Admin("l240001@lhr.nu.edu.pk", "Admin L240001"),
                new Admin("l243095@lhr.nu.edu.pk", "Admin L243095")
            ));

            System.out.println("DEBUG: Admins seeded. Count=" + adminRepo.count());
            adminRepo.findAll().forEach(a ->
                System.out.println("DEBUG:   -> '" + a.getEmail() + "'")
            );
        };
    }

}
