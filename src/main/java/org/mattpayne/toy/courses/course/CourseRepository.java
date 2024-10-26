package org.mattpayne.toy.courses.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findAllById(Long id, Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = :id")
    Optional<Course> findByIdWithStudents(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students")
    List<Course> findAllWithStudents();
}
