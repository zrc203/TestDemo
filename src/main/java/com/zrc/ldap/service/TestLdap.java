package com.zrc.ldap.service;

public class TestLdap {
	public static void main(String[] args) throws Exception {
		LDAPService ldapService = new LDAPService();
		ldapService.verifyUserCredentials("zhao", "zaq1@Wsx");
	}
}
