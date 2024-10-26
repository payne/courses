package org.mattpayne.toy.courses.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findAllById(Long id, Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);

}
