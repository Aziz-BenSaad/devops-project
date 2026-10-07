package tn.esprit.studentmanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.util.List;

@Service
public class StudentService implements IStudentService {
    @Autowired
    private StudentRepository studentRepository;
    public List<Student> getAllStudents() { return studentRepository.findAll(); }
    public Student getStudentById(Long id) { return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id)); }
    public Student saveStudent(Student student) { return studentRepository.save(student); }
    public void deleteStudent(Long id) { studentRepository.deleteById(id); }

}
