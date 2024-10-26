package org.mattpayne.toy.courses.student;

import java.util.List;
import org.mattpayne.toy.courses.course.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findAllById(Long id, Pageable pageable);

    Student findFirstByCourses(Course course);

    List<Student> findAllByCourses(Course course);

}
