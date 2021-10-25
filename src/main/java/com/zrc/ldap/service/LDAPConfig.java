package com.zrc.ldap.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class LDAPConfig {

	public static LDAPConfig getInstance() {
		return new LDAPConfig();
	}

	public ArrayList<String> getPropertyList(String key, String sep) {
		String value = ResourceBundle.getBundle("LDAP").getString(key);
		String[] split = value.split(sep);
		ArrayList<String> list = new ArrayList<>();
		Collections.addAll(list, split);
		return list;
	}

	public static String getProperty(String key) {
		return ResourceBundle.getBundle("LDAP").getString(key);
	}

}
