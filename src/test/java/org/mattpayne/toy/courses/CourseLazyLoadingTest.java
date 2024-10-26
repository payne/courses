package org.mattpayne.toy.courses;


import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mattpayne.toy.courses.course.Course;
import org.mattpayne.toy.courses.course.CourseRepository;
import org.mattpayne.toy.courses.student.Student;
import org.mattpayne.toy.courses.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles("test")
class CourseLazyLoadingTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Create a course
        Course course = Course.builder()
            .name("Java Programming")
            .code("CS101")
            .credits(3)
            .build();
        courseRepository.save(course);

        // Create and enroll three students
        List<Student> students = Arrays.asList(
            Student.builder().name("John Doe").email("john@test.com").build(),
            Student.builder().name("Jane Smith").email("jane@test.com").build(),
            Student.builder().name("Bob Wilson").email("bob@test.com").build()
        );
        studentRepository.saveAll(students);

        // Enroll students in the course
        students.forEach(student -> student.enrollInCourse(course));
        studentRepository.saveAll(students);

        // Clear persistence context to ensure clean lazy loading tests
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should demonstrate lazy loading behavior when accessing students")
    void shouldDemonstrateLazyLoading() {
        // First, get the course without loading students
        Course course = courseRepository.findById(1L).orElseThrow();

        // Verify course is loaded but students are not
        assertTrue(Hibernate.isInitialized(course));
        assertFalse(Hibernate.isInitialized(course.getStudents()));

        // Log the moment before accessing students
        log.info("About to access students - lazy loading will occur");

        // Access the students collection - this will trigger lazy loading
        int studentCount = course.getStudents().size();

        // Verify students are now loaded
        assertTrue(Hibernate.isInitialized(course.getStudents()));
        assertEquals(3, studentCount);
    }

    @Test
    @DisplayName("Should avoid N+1 problem using JOIN FETCH")
    void shouldAvoidNPlusOneProblem() {
        // Using regular findAll() would cause N+1 problem
        List<Course> coursesWithNPlusOne = courseRepository.findAll();

        // Clear persistence context
        entityManager.clear();

        // Using JOIN FETCH to avoid N+1 problem
        List<Course> coursesWithJoinFetch = courseRepository.findAllWithStudents();

        // Verify students are already loaded without additional queries
        assertTrue(Hibernate.isInitialized(coursesWithJoinFetch.get(0).getStudents()));
        assertEquals(3, coursesWithJoinFetch.get(0).getStudents().size());
    }

    @Test
    @DisplayName("Should demonstrate LazyInitializationException when session is closed")
    void shouldThrowLazyInitializationException() {
        // Get course reference
        Course course = courseRepository.findById(1L).orElseThrow();

        // Clear persistence context to simulate session closing
        entityManager.clear();

        // Attempting to access students after session is closed
        assertThrows(LazyInitializationException.class, () -> {
            // This should throw LazyInitializationException
            course.getStudents().size();
        });
    }

    @Test
    @DisplayName("Should demonstrate proper lazy loading in service layer")
    @Transactional(readOnly = true)
    void shouldHandleLazyLoadingInService() {
        // Get course with students eagerly loaded
        Course course = courseRepository.findByIdWithStudents(1L).orElseThrow();

        // Access students within transaction
        Set<Student> students = course.getStudents();

        // Perform assertions
        assertNotNull(students);
        assertEquals(3, students.size());
        assertTrue(students.stream()
            .map(Student::getName)
            .collect(Collectors.toSet())
            .containsAll(Arrays.asList("John Doe", "Jane Smith", "Bob Wilson")));
    }
}

