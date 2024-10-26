package org.mattpayne.toy.courses.student;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.mattpayne.toy.courses.course.Course;
import org.mattpayne.toy.courses.course.CourseRepository;
import org.mattpayne.toy.courses.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentService(final StudentRepository studentRepository,
            final CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public Page<StudentDTO> findAll(final String filter, final Pageable pageable) {
        Page<Student> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = studentRepository.findAllById(longFilter, pageable);
        } else {
            page = studentRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(student -> mapToDTO(student, new StudentDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public StudentDTO get(final Long id) {
        return studentRepository.findById(id)
                .map(student -> mapToDTO(student, new StudentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final StudentDTO studentDTO) {
        final Student student = new Student();
        mapToEntity(studentDTO, student);
        return studentRepository.save(student).getId();
    }

    public void update(final Long id, final StudentDTO studentDTO) {
        final Student student = studentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(studentDTO, student);
        studentRepository.save(student);
    }

    public void delete(final Long id) {
        studentRepository.deleteById(id);
    }

    private StudentDTO mapToDTO(final Student student, final StudentDTO studentDTO) {
        studentDTO.setId(student.getId());
        studentDTO.setName(student.getName());
        studentDTO.setEmail(student.getEmail());
        studentDTO.setCourses(student.getCourses().stream()
                .map(course -> course.getId())
                .toList());
        return studentDTO;
    }

    private Student mapToEntity(final StudentDTO studentDTO, final Student student) {
        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        final List<Course> courses = courseRepository.findAllById(
                studentDTO.getCourses() == null ? Collections.emptyList() : studentDTO.getCourses());
        if (courses.size() != (studentDTO.getCourses() == null ? 0 : studentDTO.getCourses().size())) {
            throw new NotFoundException("one of courses not found");
        }
        student.setCourses(new HashSet<>(courses));
        return student;
    }

}
