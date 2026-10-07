package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void getAllStudents_returnsAll() {
        when(studentRepository.findAll()).thenReturn(List.of(new Student(), new Student()));
        assertEquals(2, studentService.getAllStudents().size());
    }

    @Test
    void getStudentById_found() {
        Student s = new Student();
        s.setIdStudent(1L);
        s.setFirstName("Arij");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s));
        assertEquals("Arij", studentService.getStudentById(1L).getFirstName());
    }

    @Test
    void getStudentById_notFound_throws404() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> studentService.getStudentById(99L));
    }

    @Test
    void saveStudent_delegatesToRepository() {
        Student s = new Student();
        when(studentRepository.save(s)).thenReturn(s);
        assertSame(s, studentService.saveStudent(s));
        verify(studentRepository).save(s);
    }

    @Test
    void deleteStudent_delegatesToRepository() {
        studentService.deleteStudent(1L);
        verify(studentRepository).deleteById(1L);
    }
}
