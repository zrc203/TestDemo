package com.zrc.demo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zrc.demo.service.AreaService;
@Service("areaService")
public class AreaServiceImpl implements AreaService{
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	@Override
	public void insertArea(Map<String, String> area) {
		sqlSessionTemplate.insert("AreaMapper.insertArea", area);
	}
	@Override
	public List<List<String>> getListCard(){
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> l1=sqlSessionTemplate.selectList("AreaMapper.getOne");
		List<String> l2=sqlSessionTemplate.selectList("AreaMapper.getTwo");
		list.add(l1);
		list.add(l2);
		return list;
	}
	@Override
	public void insertMusic(List<Map<String, Object>> list) {
		sqlSessionTemplate.insert("AreaMapper.insertMusic", list);
		
	}
}
