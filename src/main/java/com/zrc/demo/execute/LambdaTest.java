package com.zrc.demo.execute;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class LambdaTest {
	public static void main(String[] args) {
		BookServiceImpl bookService = new BookServiceImpl();
		Enhancer enhancer = new Enhancer();
		enhancer.setClassLoader(LambdaTest.class.getClassLoader());
		enhancer.setSuperclass(bookService.getClass());
		enhancer.setCallback(new MethodInterceptor() {
			
			@Override
			public Object intercept(Object proxy, Method method, Object[] args, MethodProxy arg3) throws Throwable {
				System.out.println("----11----");
				Object invoke = method.invoke(bookService, args);
				System.out.println("----22----");
				return invoke;
			}
		});
		BookServiceImpl bookServiceProxy = (BookServiceImpl) enhancer.create();
		
		bookServiceProxy.add("zrc");
	}

}

interface BookService {
	public void add(String name);
}

class BookServiceImpl implements BookService {

	@Override
	public void add(String name) {
		System.out.println("add-->"+name);

	}
}