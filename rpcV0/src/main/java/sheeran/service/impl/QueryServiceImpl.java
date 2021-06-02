package sheeran.service.impl;

import sheeran.service.QueryService;
import sheeran.service.dto.Student;
import sheeran.spring.RpcService;

@RpcService
public class QueryServiceImpl implements QueryService {
    @Override
    public Student Query(int id) {
        Student student = new Student();
        student.setId(id);
        student.setName("curry");
        return student;
    }
}
