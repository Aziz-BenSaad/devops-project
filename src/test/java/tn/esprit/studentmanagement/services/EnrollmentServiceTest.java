package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.entities.Status;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    void getAllEnrollments_returnsAll() {
        when(enrollmentRepository.findAll()).thenReturn(List.of(new Enrollment(), new Enrollment()));
        assertEquals(2, enrollmentService.getAllEnrollments().size());
    }

    @Test
    void getEnrollmentById_found() {
        Enrollment e = new Enrollment();
        e.setStatus(Status.ACTIVE);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(e));
        assertEquals(Status.ACTIVE, enrollmentService.getEnrollmentById(1L).getStatus());
    }

    @Test
    void getEnrollmentById_notFound_throws404() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> enrollmentService.getEnrollmentById(99L));
    }

    @Test
    void saveAndDelete_delegateToRepository() {
        Enrollment e = new Enrollment();
        when(enrollmentRepository.save(e)).thenReturn(e);
        assertSame(e, enrollmentService.saveEnrollment(e));
        enrollmentService.deleteEnrollment(1L);
        verify(enrollmentRepository).deleteById(1L);
    }
}
