package com.github.peter.wang.test.service;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;

/**
 * Mocke测试使用的抽象类
 * 把创建mock的上下文封装实现
 * @author wangcanpei
 *
 */
public abstract class AbstractMock {
	
	/**
	 * 测试上下文
	 */
	private Mockery context;
	
	/**
	 * 在方法启动之前先模拟出上下文
	 */
	@Before
	public void setUp(){
		context=new Mockery();
	}
	
	/**
	 * 生成mock的对象
	 * @param t
	 * @return
	 */
	protected  <T> T mock(Class<T> t){
		if(t==null){
			throw new IllegalArgumentException();
		}
		return context.mock(t);
		
	}
	
	/**
	 * 设置期望
	 * @param expectaitions 期望设置
	 */
	protected  void check(Expectations expectaitions){
		 context.checking(expectaitions);
	}
}
