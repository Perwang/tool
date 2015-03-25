package com.github.peter.wang.test.dao;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * 单元测试DAO层的基础类
 * 目的是
 * 1、在测试之前要连上数据库
 * 2、在每一个方法之前把数据清空
 * 3、执行需要测试的方法
 * 4、执行完成以后回滚所有的数据
 * 
 * 回滚的配置方式是通过defaultRollback=true配置的
 * 支持通过配置文件来往数据库里加载数据，依赖DBunit
 * 必须继承AbstractTransactionalJUnit4SpringContextTests才能支持事务
 * @author wangcanpei
 */
@TransactionConfiguration(defaultRollback = true,transactionManager = "transactionManager")
@RunWith(SpringJUnit4ClassRunner.class)
public class BaseDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private DataSource dataSource;

	private IDatabaseConnection conn;
	
	/**
	 * 启动时，把数据库连接
	 */
	@Before
	public  void setUpTest(){
		//用来给
		conn = new DatabaseConnection(DataSourceUtils.getConnection(dataSource));
	}

	

	/**
	 * 清空file中包含的表中的数据，并插入file中指定的数据
	 * 
	 * @param file
	 * @throws Exception
	 */
	protected void setUpDataSet(String file) throws Exception {
		IDataSet dataset = new FlatXmlDataSet(new ClassPathResource(file)
				.getFile());
		DatabaseOperation.CLEAN_INSERT.execute(conn, dataset);
	}

	/**
	 * 验证file中包含的表中的数据和数据库中的相应表的数据是否一致
	 * 
	 * @param file
	 * @throws Exception
	 */
	protected void verifyDataSet(String file) throws Exception {
		IDataSet expected = new FlatXmlDataSet(new ClassPathResource(file)
				.getFile());
		IDataSet dataset = conn.createDataSet();
		for (String tableName : expected.getTableNames()) {
			Assertion.assertEquals(expected.getTable(tableName), dataset
					.getTable(tableName));
		}

	}

	/**
	 * 清空指定的表中的数据
	 * 
	 * @param tableName
	 * @throws Exception
	 */
	protected void clearTable(String tableName) throws Exception {
		DefaultDataSet dataset = new DefaultDataSet();
		dataset.addTable(new DefaultTable(tableName));
		DatabaseOperation.DELETE_ALL.execute(conn, dataset);
	}

	/**
	 * 验证指定的表为空
	 * 
	 * @param tableName
	 * @throws DataSetException
	 * @throws SQLException
	 */
	protected void verifyEmpty(String tableName) throws DataSetException,
			SQLException {
		Assert.assertEquals(0, conn.createDataSet().getTable(tableName)
				.getRowCount());
	}
}
