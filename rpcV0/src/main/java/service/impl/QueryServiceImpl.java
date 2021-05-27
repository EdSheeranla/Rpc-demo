package service.impl;

import annotation.Service;
import service.QueryService;
import service.dto.Student;
@Service
public class QueryServiceImpl implements QueryService {
    @Override
    public Student Query(int id) {
        Student student = new Student();
        student.setId(id);
        student.setName("curry");
        return student;
    }
}
