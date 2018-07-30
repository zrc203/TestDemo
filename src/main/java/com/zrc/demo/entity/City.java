package com.zrc.demo.entity;

public class City implements Comparable<City>{
	public City(String cityName ) {
		this.cityName = cityName;
	}
	private String cityName ;

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	@Override
	public String toString() {
		return this.cityName;
	}

	@Override
	public int compareTo(City o) {
		return 0;
	}
	
}
