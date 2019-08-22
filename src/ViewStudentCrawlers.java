import databaseUtils.Database;
import org.jsoup.nodes.Element;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Lucien
 * @date 2019/08/13 12:57
 * @description viewstudent页面爬虫
 */

public class ViewStudentCrawlers {
	public static void ViewStudentCrawlers(String cookie, String[] years){
		try {
			for (String year : years){

				// 需登陆后访问的 Url，爬取学生ID的URL
				String defaultURL = "http://class.sise.com.cn:7001/SISEWeb/module/sise/viewstudent/default.jsp?grade={0}&speciality={1}";

				//new一个HTTPClient对象
				HttpClient httpClient = new HttpClient();
				// 进行登陆后的操作，跳转到信息页面
				//获取home页面,241是默认值，可以不修改
				GetMethod getMethodForHome = new GetMethod(MessageFormat.format(defaultURL, year, "241"));
				//设置cookie请求头
				getMethodForHome.setRequestHeader("cookie", cookie);
				//发送执行，休眠200毫秒，防止请求过快封IP
				Thread.sleep(200);
				httpClient.executeMethod(getMethodForHome);
				//将服务器返回的个人信息页面html文本保存在字符串中
				String homeHtml = getMethodForHome.getResponseBodyAsString();
				//使用完后关闭连接
				getMethodForHome.releaseConnection();
				//使用jsoup解析
				Document homeDocument = Jsoup.parse(homeHtml);

				/**
				 * 代码块说明
				 * @author lucien
				 * 分离出年级select里的数据
				 * @date 2019/8/13 13:29
				 */
				//查找年级的select标签
				Elements gradeSelects = homeDocument.getElementsByAttributeValue("name", "grade");
				//获取select标签的子元素option
				Elements gradesOptions = gradeSelects.get(0).children();

				//声明grades数组用于存放grade
				String[] grades = new String[gradesOptions.size()];
				//遍历gradesOptions，将grade数据读取存入数组
				for (int i = 0; i < grades.length; i++) {
					grades[i] = gradesOptions.get(i).text();
				}

				//获取当前viewstudent页面中speciality的value
				List<String> specialityList = crawlSpeciality(homeDocument);

				//声明数据库类
				Database database = new Database();

				//循环遍历specialityList，爬取该学年所有专业信息
				for (String speciality : specialityList) {
					//获取home页面
					GetMethod getMethod = new GetMethod(MessageFormat.format(defaultURL, year, speciality));
					//设置cookie请求头
					getMethod.setRequestHeader("cookie", cookie);
					//发送执行
					Thread.sleep(300);
					httpClient.executeMethod(getMethod);
					//将服务器返回的个人信息页面html文本保存在字符串中
					homeHtml = getMethod.getResponseBodyAsString();
					//使用完后关闭连接
					getMethodForHome.releaseConnection();
					//使用jsoup解析
					homeDocument = Jsoup.parse(homeHtml);

					//获取当前viewstudent页面的所有学号
					List<String> studentIdList = crawlStudentsID(homeDocument);

					//获取专业代码，例如：TS18
					String specialityGrade = homeDocument.getElementsByAttributeValue("value", speciality).text().split("-")[0] + year.substring(2, 4);

					//根据专业代码查询数据库中所有该代码学生的学号
					ArrayList<String> studentSIdWithMajorCode = database.selectAllId(specialityGrade);

					//遍历爬取该学年该专业下的所有学生
					for (int i = 0; i < studentIdList.size(); i++) {

						//判断该学号学生是否在数据库中，若是则跳过
						if (studentSIdWithMajorCode.contains(studentIdList.get(i))) {
							System.out.println(MessageFormat.format("学生学号为 {0} ，已存在；", studentIdList.get(i)));
							continue;
						}

						//studentCodeInfo为学生对应的代码信息
						HashMap<String, String> studentCodeInfo = StudentIndexPageCrawlers.studentCodeCrawlers(cookie, studentIdList.get(i));

						//个人信息查看页面爬取信息
						HashMap<String, String> studentBaseInfo = CourseViewActionCrawlers.StudentInfoCrawlers(cookie, studentCodeInfo);

						studentBaseInfo = StudentDetailPageCrawlers.studentInfoCrawlers(cookie, specialityGrade,
								studentCodeInfo, studentBaseInfo);
						//插入数据库
						database.insertStudentsCode(studentCodeInfo);
						database.insertStudentsBaseInfo(studentBaseInfo);
						//输出信息
						System.out.println(MessageFormat.format("学生学号为 {0} ，姓名为： {1} ；信息已爬取", studentBaseInfo.get("学号"), studentBaseInfo.get("姓名")));

					}
				}
			}
		}catch (Throwable t){
			t.printStackTrace();
		}
	}

	/**
	 * 方法实现说明
	 * @author	lucien
	 * 将当前viewstudent页面的所有学号选取并存入名为studentInfo的List数组并返回
	 * @return List<String>
	 * @date	2019/8/13 14:12
	 */
	public static List<String> crawlStudentsID(Document homeDocument){
		//选取所有表格数组Elements，即tablebody类
		Elements tablebodyClasses = homeDocument.getElementsByClass("tablebody");
		//List数组存放获取的全部学生学号
		List studentInfo = new ArrayList<String>();
		//遍历tablebodyClasses，将学号存入List数组
		for (int i = 0; i < tablebodyClasses.size(); i++){
			//如果是偶数，则是学号对应的td
			if (i % 2 == 0){
				studentInfo.add(tablebodyClasses.get(i).text());
			}
		}
		return studentInfo;
	}

	/**
	 * 方法实现说明
	 * @author	lucien
	 * 分离出专业select标签里的value，并返回
	 * @return List<String>
	 * @date	2019/8/13 13:44
	 */
	public static List<String> crawlSpeciality(Document homeDocument){

		//查找专业的select标签
		Elements specialitySelects = homeDocument.getElementsByAttributeValue("name", "speciality");
		//获取select标签的子元素option
		Elements specialityOptions = specialitySelects.get(0).children();
		//声明speciality数组用于存放speciality的value
		List<String> speciality = new ArrayList<>();
		//遍历specialityOptions，将speciality数据读取存入数组
		for (Element specialityOption : specialityOptions){
			speciality.add(specialityOption.attr("value"));
		}
		return speciality;
	}
}
