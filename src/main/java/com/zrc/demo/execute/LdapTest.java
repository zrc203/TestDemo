package com.zrc.demo.execute;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LdapTest {

	public static void main(String[] args) throws NamingException {
		LdapContext ctx = connetLDAP();
		String userName = "Tom.Hardy";
		String passWord = "111111";
		// 设置过滤条件
		String filter = "(&(objectclass=top)(cn=Tom.Hardy)(ou=aly))";
		// 限制要查询的字段内容
		String[] attrPersonArray = { "uid", "userPassword", "displayName", "cn", "sn", "mail", "description" };
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// 设置将被返回的Attribute
		searchControls.setReturningAttributes(attrPersonArray);
		// 三个参数分别为：
		// 上下文；
		// 要搜索的属性，如果为空或 null，则返回目标上下文中的所有对象；
		// 控制搜索的搜索控件，如果为 null，则使用默认的搜索控件
		NamingEnumeration<SearchResult> answer = ctx.search("cn=ibmpolicies", filter.toString(), searchControls);
		// 输出查到的数据
		Map<String,String> user = new HashMap<String, String>();
		while (answer.hasMore()) {
			SearchResult result = answer.next();
			System.out.println(result);
			NamingEnumeration<? extends Attribute> all = result.getAttributes().getAll();
			while(all.hasMore()){
				Attribute attribute = all.next();
				String id = attribute.getID();
				Object value = attribute.get();
				if("cn".equals(id)){
					user.put("userName", value.toString());
				}
				if("userPassword".equals(id)){
					byte[] pwd=(byte[]) value;
					String psd = new String(pwd);
					user.put("userPassword", psd);
				}
				
			
			}
			
		}
		if(userName.equals(user.get("userName"))&&passWord.equals(user.get("userPassword"))){
			System.out.println("用户名："+userName+"  密码："+passWord+"  匹配成功");
		}
		System.out.println(user);
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LdapContext connetLDAP() throws NamingException {
		// 连接Ldap需要的信息
		String ldapFactory = "com.sun.jndi.ldap.LdapCtxFactory";
		String ldapUrl = "ldap://10.129.0.227:389";// url
		String ldapAccount = "cn=root"; // 用户名
		String ldapPwd = "zaq1@Wsx";// 密码
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
		// LDAP server
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, ldapAccount);
		env.put(Context.SECURITY_CREDENTIALS, ldapPwd);
		env.put("java.naming.referral", "follow");
		LdapContext ctxTDS = new InitialLdapContext(env, null);
		return ctxTDS;
	}
}
