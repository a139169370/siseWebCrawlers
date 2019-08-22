package databaseUtils;

import config.Config;

import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Lucien
 * @date 2019/08/13 12:57
 * @description 数据库工具类，直接用原生了，懒得上框架，注意插入数据库语句是顺序插入
 */

public class Database implements Config {
	public Database() {
		try {

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 方法实现说明
	 * @author	lucien
	 * studentCodeInfo插入数据库
	 * @date	2019/8/17 13:01
	 */
	public void insertStudentsCode(HashMap<String, String> studentCodeInfo){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(MessageFormat.format("jdbc:mysql://{0}/{1}?useSSL=false&useUnicode=true&characterEncoding=UTF-8", Config.databasesAddress, Config.databaseName), Config.databaseUser, Config.databasePassword);
			//没有记录则插入记录
			String insertSql = "insert studentcodeinfo values(?,?,?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
			preparedStatement.setString(1, studentCodeInfo.get("id"));
			preparedStatement.setString(2, studentCodeInfo.get("studentid"));
			preparedStatement.setString(3, studentCodeInfo.get("gzcode"));
			preparedStatement.setString(4, studentCodeInfo.get("sutdentidnum"));
			preparedStatement.setString(5, studentCodeInfo.get("serialabc"));

			preparedStatement.executeUpdate();
			preparedStatement.close();
			connection.close();
		}catch (Throwable t){
			t.printStackTrace();
		}
	}

	public void insertStudentsBaseInfo(HashMap<String, String> studentBaseInfo){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(MessageFormat.format("jdbc:mysql://{0}/{1}?useSSL=false&useUnicode=true&characterEncoding=UTF-8", Config.databasesAddress, Config.databaseName), Config.databaseUser, Config.databasePassword);
			//没有记录则插入记录
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

			String insertSql = "insert basicinformationofstudents values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
			preparedStatement.setString(1, studentBaseInfo.get("学号"));
			preparedStatement.setString(2, studentBaseInfo.get("姓名"));
			preparedStatement.setString(3, studentBaseInfo.get("性别"));
			preparedStatement.setDate(4, new java.sql.Date (simpleDateFormat.parse(studentBaseInfo.get("出生日期")).getTime()));
			preparedStatement.setString(5, studentBaseInfo.get("身份证"));
			preparedStatement.setString(6, studentBaseInfo.get("政治面貌"));
			preparedStatement.setString(7, studentBaseInfo.get("专业"));
			preparedStatement.setString(8, studentBaseInfo.get("专业年级"));
			preparedStatement.setString(9, studentBaseInfo.get("电子邮箱"));
			preparedStatement.setString(10, studentBaseInfo.get("行政班"));
			preparedStatement.setString(11, studentBaseInfo.get("班主任"));
			preparedStatement.setString(12, studentBaseInfo.get("辅导员"));
			preparedStatement.setString(13, studentBaseInfo.get("家长姓名"));
			preparedStatement.setString(14, studentBaseInfo.get("家长工作性质"));
			preparedStatement.setString(15, studentBaseInfo.get("联系电话"));
			preparedStatement.setString(16, studentBaseInfo.get("家庭地址"));
			preparedStatement.setString(17, studentBaseInfo.get("特长"));
			preparedStatement.setString(18, studentBaseInfo.get("学生手机"));
			preparedStatement.setString(19, studentBaseInfo.get("准考证号"));
			preparedStatement.setString(20, studentBaseInfo.get("住址"));


			preparedStatement.executeUpdate();
			preparedStatement.close();
			connection.close();
		}catch (Throwable t){
			t.printStackTrace();
		}
	}

	/**
	 * 方法实现说明
	 * @author	lucien
	 * 查询所有该专业代码（如TS18）的学号并返回数组
	 * @return	ArrayList<String>
	 * @date	2019/8/18 19:30
	 */
	public ArrayList<String> selectAllId(String majorCode){
		//返回的ArrayList
		ArrayList<String> studentSIdWithMajorCode = new ArrayList<>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(MessageFormat.format("jdbc:mysql://{0}/{1}?useSSL=false&useUnicode=true&characterEncoding=UTF-8", Config.databasesAddress, Config.databaseName), Config.databaseUser, Config.databasePassword);

			String sql = "select id from basicinformationofstudents where majorCode = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, majorCode);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()){
				studentSIdWithMajorCode.add(resultSet.getString(1));
			}
		}catch (Throwable t){
			t.printStackTrace();
		}
		return studentSIdWithMajorCode;
	}
}
