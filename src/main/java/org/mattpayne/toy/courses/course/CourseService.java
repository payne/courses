package org.mattpayne.toy.courses.course;

import jakarta.transaction.Transactional;
import org.mattpayne.toy.courses.student.StudentRepository;
import org.mattpayne.toy.courses.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public CourseService(final CourseRepository courseRepository,
            final StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public Page<CourseDTO> findAll(final String filter, final Pageable pageable) {
        Page<Course> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = courseRepository.findAllById(longFilter, pageable);
        } else {
            page = courseRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(course -> mapToDTO(course, new CourseDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public CourseDTO get(final Long id) {
        return courseRepository.findById(id)
                .map(course -> mapToDTO(course, new CourseDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CourseDTO courseDTO) {
        final Course course = new Course();
        mapToEntity(courseDTO, course);
        return courseRepository.save(course).getId();
    }

    public void update(final Long id, final CourseDTO courseDTO) {
        final Course course = courseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(courseDTO, course);
        courseRepository.save(course);
    }

    public void delete(final Long id) {
        final Course course = courseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        studentRepository.findAllByCourses(course)
                .forEach(student -> student.getCourses().remove(course));
        courseRepository.delete(course);
    }

    private CourseDTO mapToDTO(final Course course, final CourseDTO courseDTO) {
        courseDTO.setId(course.getId());
        courseDTO.setName(course.getName());
        courseDTO.setCode(course.getCode());
        courseDTO.setCredits(course.getCredits());
        return courseDTO;
    }

    private Course mapToEntity(final CourseDTO courseDTO, final Course course) {
        course.setName(courseDTO.getName());
        course.setCode(courseDTO.getCode());
        course.setCredits(courseDTO.getCredits());
        return course;
    }

    public boolean codeExists(final String code) {
        return courseRepository.existsByCodeIgnoreCase(code);
    }

}
