package com.zrc.ldap.service;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class LDAPConfig {

	public static LDAPConfig getInstance() {
		return new LDAPConfig();
	}

	public ArrayList getPropertyList(String key, String sep) {
		String value = ResourceBundle.getBundle("LDAP").getString(key);
		String[] split = value.split(sep);
		ArrayList list = new ArrayList<>();
		for (String str : split) {
			list.add(str);
		}
		return list;
	}

	public static String getProperty(String key) {
		String value = ResourceBundle.getBundle("LDAP").getString(key);
		return value;
	}

}
