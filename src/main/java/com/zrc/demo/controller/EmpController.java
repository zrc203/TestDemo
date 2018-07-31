package com.zrc.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zrc.demo.domian.Emp;
import com.zrc.demo.service.EmpService;

@RestController
@RequestMapping("/emp")
public class EmpController {
	@Autowired
	private EmpService empService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Emp> getEmp(@PathVariable Integer eId) {
		ResponseEntity<Emp> entity =null;
		try {
			Emp emp = empService.getEmpById(eId);
			entity = new ResponseEntity<>(emp,HttpStatus.OK);
		} catch (Exception e) {
			entity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return entity;
	}

}
