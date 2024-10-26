package org.mattpayne.toy.courses;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mattpayne.toy.courses.course.Course;
import org.mattpayne.toy.courses.course.CourseRepository;
import org.mattpayne.toy.courses.student.Student;
import org.mattpayne.toy.courses.student.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
// Should only run when the NotTesting profile is active
@Profile("!test")
public class DataInitializer implements CommandLineRunner {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Only initialize if no data exists
        if (studentRepository.count() > 0 || courseRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        log.info("Starting data initialization...");

        // Create Courses
        Course java = Course.builder()
            .name("Java Programming")
            .code("CS101")
            .credits(3)
            .build();

        Course python = Course.builder()
            .name("Python for Data Science")
            .code("CS102")
            .credits(4)
            .build();

        Course algorithms = Course.builder()
            .name("Algorithms and Data Structures")
            .code("CS201")
            .credits(4)
            .build();

        courseRepository.saveAll(List.of(java, python, algorithms));
        log.info("Saved {} courses", 3);

        // Create Students
        Student john = Student.builder()
            .name("John Doe")
            .email("john.doe@university.com")
            .build();

        Student jane = Student.builder()
            .name("Jane Smith")
            .email("jane.smith@university.com")
            .build();

        Student bob = Student.builder()
            .name("Bob Wilson")
            .email("bob.wilson@university.com")
            .build();

        // Save students first
        studentRepository.saveAll(List.of(john, jane, bob));
        log.info("Saved {} students", 3);

        // Enroll students in courses
        john.enrollInCourse(java);
        john.enrollInCourse(python);

        jane.enrollInCourse(java);
        jane.enrollInCourse(algorithms);

        bob.enrollInCourse(python);
        bob.enrollInCourse(algorithms);
        bob.enrollInCourse(java);

        // Save the enrollments
        studentRepository.saveAll(List.of(john, jane, bob));
        log.info("Created course enrollments");

        log.info("Data initialization completed!");

        // Log some statistics
        logDataStatistics();
    }

    private void logDataStatistics() {
        log.info("=== System Statistics ===");
        log.info("Total Students: {}", studentRepository.count());
        log.info("Total Courses: {}", courseRepository.count());

        // Get enrollment statistics for each course
        courseRepository.findAll().forEach(course -> {
            log.info("Course: {} - Enrolled Students: {}",
                course.getName(),
                course.getStudents().size());
        });
    }
}
