package com.zrc.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zrc.demo.domian.Emp;
@Repository
public interface EmpDao extends JpaRepository<Emp, Integer> {

}
