import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;

/**
 * @author Lucien
 * @date 2019/08/15 15:39
 * @description 学生index页面爬虫，用于爬取4个学生对应的code
 */

public class StudentIndexPageCrawlers {
	/**
	 * 方法实现说明
	 * @author	lucien
	 * 爬取4个学生对应的code存入Map数组并返回
	 * @return	Map<String, String>,{id=1;studentid=2,gzcode=3,sutdentidnum=4,serialabc=5}(第一个为学生学号，第二个为学生加密ID，第四个为明文数字ID)
	 * @date	2019/8/15 15:45
	 */
	public static HashMap<String, String> studentCodeCrawlers(String cookie, String studentID){

		/**
		 * @author	lucien
		 * 声明Map数组，用于存放学生Code信息
		 * {id=1;studentid=2,gzcode=3,sutdentidnum=4,serialabc=5}
		 * (第一个为学生学号，第二个为学生加密ID，第四个为明文数字ID)
		 * @date	2019/8/15 15:51
		 */
		HashMap<String, String> studentCodeInfo = new HashMap<>(5);

		try {
			//存入学生学号
			studentCodeInfo.put("id", studentID);

			// 需登陆后访问的 Url，学生index.jsp的URL，需要拼接学号，如：http://class.sise.com.cn:7001/SISEWeb/module/sise/viewstudent/logonChkPwdx.jsp?username=1840332132
			String indexURL = "http://class.sise.com.cn:7001/SISEWeb/module/sise/viewstudent/logonChkPwdx.jsp?username=" + studentID;

			//new一个HTTPClient对象
			HttpClient httpClient = new HttpClient();
			// 进行登陆后的操作，跳转到信息页面
			//获取home页面
			GetMethod getMethodForHome = new GetMethod(indexURL);
			//设置cookie请求头
			getMethodForHome.setRequestHeader("cookie", cookie);
			//发送执行
			Thread.sleep(200);
			httpClient.executeMethod(getMethodForHome);

			//TODO 访问http://class.sise.com.cn:7001/SISEWeb/module/sise/viewstudent/logonChkPwdx.jsp?username=页面后会重定向到main.jsp，此处直接跳过去了，有空的话可以优化下
			GetMethod getMethod = new GetMethod("http://class.sise.com.cn:7001/SISEWeb/module/sise/viewstudent/main.jsp");
			//设置cookie请求头
			getMethod.setRequestHeader("cookie", cookie);
			//发送执行
			Thread.sleep(200);
			httpClient.executeMethod(getMethod);

			//将服务器返回的个人信息页面html文本保存在字符串中
			String homeHtml = getMethod.getResponseBodyAsString();
			//使用完后关闭连接
			getMethod.releaseConnection();
			//使用jsoup解析
			Document homeDocument = Jsoup.parse(homeHtml);


			//查找"个人信息查询"的标签
			Elements personalInformationForHomeTagTr = homeDocument.getElementsByAttributeValue("title", "个人信息查询");
			//获取该标签onclick后执行跳转操作的链接
			String personalInformationForHomeUrl = personalInformationForHomeTagTr.get(0).child(0).attr("onclick");
			//通过字符串切割过滤出参数，存入studentid
			studentCodeInfo.put("studentid", personalInformationForHomeUrl.split("studentid=")[1].split("'")[0]);

			//获取“考勤查询”的tr
			Elements attendanceQueryForHomeTagTr = homeDocument.getElementsByAttributeValue("title", "考勤");
			//获取该标签onclick后执行跳转操作的链接
			String attendanceQueryForHomeUrl = attendanceQueryForHomeTagTr.get(0).child(0).attr("onclick");
			//获取gzcode并存入
			studentCodeInfo.put("gzcode", attendanceQueryForHomeUrl.split("gzcode=")[1].split("'")[0]);

			//获取“学生考试时间查看”的tr
			Elements studentExaminationTimeForHomeTagTr = homeDocument.getElementsByAttributeValue("title", "学生考试时间查看");
			//获取该标签onclick后执行跳转操作的链接
			String studentExaminationTimeForHomeUrl = studentExaminationTimeForHomeTagTr.get(0).child(0).attr("onclick");
			//获取sutdentidnum，并存入
			studentCodeInfo.put("sutdentidnum", studentExaminationTimeForHomeUrl.split("studentid=")[1].split("'")[0]);

			//TODO httpclient中获取的serialabc和谷歌浏览器中获取的serialabc数值不同，但均可以进入奖惩记录页面；尚未知道原因
			//获取“考勤查询”的tr
			Elements disciplinaryRecordsForHomeTagTr = homeDocument.getElementsByAttributeValue("title", "奖惩记录");
			//获取该标签onclick后执行跳转操作的链接
			String disciplinaryRecordsForHomeUrl = disciplinaryRecordsForHomeTagTr.get(0).child(0).attr("onclick");
			//获取sutdentidnum，并存入
			studentCodeInfo.put("serialabc", disciplinaryRecordsForHomeUrl.split("serialabc=")[1].split("'")[0]);

		}catch (Throwable t){
			t.printStackTrace();
		}
		return studentCodeInfo;
	}
}
