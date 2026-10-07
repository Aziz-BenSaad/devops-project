package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void getAllDepartments_returnsAll() {
        when(departmentRepository.findAll()).thenReturn(List.of(new Department()));
        assertEquals(1, departmentService.getAllDepartments().size());
    }

    @Test
    void getDepartmentById_found() {
        Department d = new Department();
        d.setName("Informatique");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(d));
        assertEquals("Informatique", departmentService.getDepartmentById(1L).getName());
    }

    @Test
    void getDepartmentById_notFound_throws404() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> departmentService.getDepartmentById(99L));
    }

    @Test
    void saveAndDelete_delegateToRepository() {
        Department d = new Department();
        when(departmentRepository.save(d)).thenReturn(d);
        assertSame(d, departmentService.saveDepartment(d));
        departmentService.deleteDepartment(1L);
        verify(departmentRepository).deleteById(1L);
    }
}
