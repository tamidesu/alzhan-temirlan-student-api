package kz.temirlan.studentapi.student;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void createSavesStudentWithoutAnIdAndReturnsGeneratedId() {
        StudentRequest request = request();
        Student persisted = student(7L);
        when(studentRepository.save(any(Student.class))).thenReturn(persisted);

        StudentResponse response = studentService.create(request);

        ArgumentCaptor<Student> captured = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captured.capture());
        assertNull(captured.getValue().getId());
        assertEquals(request.fullName(), captured.getValue().getFullName());
        assertEquals(request.email(), captured.getValue().getEmail());
        assertEquals(request.groupName(), captured.getValue().getGroupName());
        assertEquals(request.studentNumber(), captured.getValue().getStudentNumber());
        assertEquals(response(7L), response);
    }

    @Test
    void findAllRequestsAscendingIdOrderAndMapsEveryStudent() {
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        when(studentRepository.findAll(order)).thenReturn(List.of(student(2L), student(7L)));

        assertEquals(List.of(response(2L), response(7L)), studentService.findAll());
        verify(studentRepository).findAll(order);
    }

    @Test
    void findAllReturnsAnEmptyListWhenThereAreNoStudents() {
        when(studentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of());

        assertEquals(List.of(), studentService.findAll());
    }

    @Test
    void findByIdReturnsMappedStudent() {
        when(studentRepository.findById(7L)).thenReturn(Optional.of(student(7L)));

        assertEquals(response(7L), studentService.findById(7L));
    }

    @Test
    void findByIdThrowsWhenStudentIsMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        StudentNotFoundException exception = assertThrows(StudentNotFoundException.class,
                () -> studentService.findById(99L));

        assertEquals("Student with id 99 was not found", exception.getMessage());
    }

    @Test
    void updateChangesExistingEntityAndPreservesItsId() {
        Student existing = student(7L);
        when(studentRepository.findById(7L)).thenReturn(Optional.of(existing));
        StudentRequest update = new StudentRequest("Temirlan Alzhan Updated", "updated@example.com",
                "IT2-2308", "0038372");

        StudentResponse response = studentService.update(7L, update);

        assertEquals(new StudentResponse(7L, update.fullName(), update.email(), update.groupName(),
                update.studentNumber()), response);
        assertEquals(7L, existing.getId());
        assertEquals(update.fullName(), existing.getFullName());
        assertEquals(update.email(), existing.getEmail());
        assertEquals(update.groupName(), existing.getGroupName());
        assertEquals("0038372", existing.getStudentNumber());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void updateMissingStudentThrowsWithoutSavingOrInserting() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.update(99L, request()));

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void deleteRemovesTheLoadedStudent() {
        Student existing = student(7L);
        when(studentRepository.findById(7L)).thenReturn(Optional.of(existing));

        studentService.delete(7L);

        verify(studentRepository).delete(existing);
    }

    @Test
    void deleteMissingStudentThrowsWithoutDeleting() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.delete(99L));

        verify(studentRepository, never()).delete(any(Student.class));
    }

    private StudentRequest request() {
        return new StudentRequest("Temirlan Alzhan", "temirlan@example.com", "IT2-2307", "38372");
    }

    private Student student(Long id) {
        StudentRequest request = request();
        Student student = new Student(request.fullName(), request.email(), request.groupName(), request.studentNumber());
        ReflectionTestUtils.setField(student, "id", id);
        return student;
    }

    private StudentResponse response(Long id) {
        StudentRequest request = request();
        return new StudentResponse(id, request.fullName(), request.email(), request.groupName(), request.studentNumber());
    }
}
