package com.zrc.demo.service;

import java.util.List;
import java.util.Map;

public interface AreaService {
	public void insertArea(Map<String,String> area);

	List<List<String>> getListCard();

	public void insertMusic(List<Map<String, Object>> list);
}
