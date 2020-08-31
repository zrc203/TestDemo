package com.zrc.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zrc.demo.service.EmpService;

@RestController
@RequestMapping("/test")
public class TestController {
	@Autowired
	private EmpService empService;

	@RequestMapping(value = "/{eId}", method = RequestMethod.GET)
	public ResponseEntity<Integer> getEmp(@PathVariable Integer eId) {
		return new ResponseEntity<Integer>(eId, HttpStatus.ACCEPTED);
	}

}
