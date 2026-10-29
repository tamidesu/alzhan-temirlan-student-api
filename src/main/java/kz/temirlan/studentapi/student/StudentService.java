package kz.temirlan.studentapi.student;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        Student student = new Student(request.fullName(), request.email(), request.groupName(), request.studentNumber());
        return toResponse(studentRepository.save(student));
    }

    public List<StudentResponse> findAll() {
        return studentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public StudentResponse findById(Long id) {
        return toResponse(requireStudent(id));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = requireStudent(id);
        student.updateDetails(request.fullName(), request.email(), request.groupName(), request.studentNumber());
        return toResponse(student);
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.delete(requireStudent(id));
    }

    private Student requireStudent(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(student.getId(), student.getFullName(), student.getEmail(),
                student.getGroupName(), student.getStudentNumber());
    }
}
