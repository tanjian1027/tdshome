package com.tangdi.risk.batch;
/**
 * 该类为操作数据库而编写的一个类
 * @author lyfxinsui
 * time:2012-4-10
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQL {
	private Connection connection;// 数据库连接对象
	private String driver;// 数据库驱动名称
	private String url;// 数据库url
	private String username;// 用户名
	private String password;// 密码

	public SQL(String driver, String url, String username, String password) {// 数据库对象初始化
		this.connection = null;
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	private boolean AddDataBaseDriver() {// 加载数据库驱动
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean DataBaseConnection() {// 连接数据库
		this.AddDataBaseDriver();// 首先加载数据库驱动
		try {
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("数据库连接成功!");
		} catch (SQLException e) {
			System.out.println("连接数据库失败!");
			e.printStackTrace();
		}
		return true;
	}

	public boolean DatabaseClose() {// 关闭数据库
		try {
			if (connection != null) {
				connection.close();
				System.out.println("关闭数据库完成!");
			}
		} catch (SQLException e) {
			System.out.println("关闭数据库失败!");
			e.printStackTrace();
		}
		return true;
	}
	
	public Connection getCon() {
		return connection;
	}
	
	public static void main(String[] args) {
//		try {
//			SQL s = new SQL("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@192.168.0.143:1521:orcl", "test", "123456");
//			s.DataBaseConnection();
//			Connection conn = s.getCon();
//			PreparedStatement stmt = conn.prepareStatement("INSERT INTO TEST.FCS_FEE_TABLE ( FEE_CODE, FEE_NAME, CCY, CAL_MODE, MULTI_SECTION_MODE, MAX_FEE, MIN_FEE, START_CAL_AMT, LAST_UPD_DATE, LAST_UPD_USER ) VALUES ('890','mixpay','CNY','1','0',10,0.01,0.01,to_date('2012-08-29 00:00:00','YYYY-MM-DD HH24:MI:SS'),'qqqqq')");
//			stmt.executeUpdate();
//			s.DatabaseClose();
//		} catch (SQLException e) {
//			System.out.println(e);
//			System.out.println(e.getErrorCode());
//		}
	}
}
