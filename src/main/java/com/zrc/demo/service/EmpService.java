package com.zrc.demo.service;

import java.util.List;

import com.zrc.demo.domian.Emp;

public interface EmpService {

	public Emp getEmpById(Integer eId) throws Exception;
	
	public void save(List<Emp> emps) throws Exception;
	
	public void delete(List<Emp> emps) throws Exception;
	
	public void update(Emp emps) throws Exception;

}
