package com.zrc.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zrc.demo.dao.EmpDao;
import com.zrc.demo.domian.Emp;
import com.zrc.demo.service.EmpService;
@Service
public class EmpServiceImpl implements EmpService{
	@Autowired
	private EmpDao empDao;

	@Override
	public Emp getEmpById(Integer eId) throws Exception {
		return empDao.getOne(eId);
	}

	@Override
	public void save(List<Emp> emps) throws Exception {
		empDao.saveAll(emps);
	}

	@Override
	public void delete(List<Emp> emps) throws Exception {
		empDao.deleteAll(emps);
		
	}

	@Override
	public void update(Emp emps) throws Exception {
		empDao.saveAndFlush(emps);
	}
	
	
	
}
