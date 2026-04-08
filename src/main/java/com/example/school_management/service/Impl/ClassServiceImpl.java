package com.example.school_management.service.Impl;

import com.example.school_management.dto.ClassRequestDto;
import com.example.school_management.entity.Class;
import com.example.school_management.repo.ClassRepository;
import com.example.school_management.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;


    @Override
    public Class createClass(ClassRequestDto request) {

        String className = request.getStandard() + "-" + request.getDivision();
        request.setClassName(className);
        Class classEntity = new Class();
        classEntity.setClassName(className);
        classEntity.setGirls(request.getGirls());
        classEntity.setBoys(request.getBoys());
        classEntity.setTotalStudents(request.getTotalStudents());
        classEntity.setDivision(request.getDivision());
        classEntity.setStandard(request.getStandard());
        return classRepository.save(classEntity);
    }

    @Override
    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    @Override
    public Class getClassById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
    }
    @Override
    public Class updateClass(Long id, Class classEntity) {

        Class existing = getClassById(id);

        existing.setStandard(classEntity.getStandard());
        existing.setDivision(classEntity.getDivision());
        existing.setTotalStudents(classEntity.getTotalStudents());
        existing.setBoys(classEntity.getBoys());
        existing.setGirls(classEntity.getGirls());

        String className = existing.getStandard() + "-" + existing.getDivision();
        existing.setClassName(className);

        return classRepository.save(existing);
    }

    @Override
    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }
}