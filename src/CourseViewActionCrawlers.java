import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * @author Lucien
 * @date 2019/08/15 16:52
 * @description courseViewAction.do页面信息（即个人信息查看页面）爬虫
 */

public class CourseViewActionCrawlers {
	/**
	 * 方法实现说明
	 * @author	lucien
	 * 个人信息页面爬虫，爬取个人信息以Map数组返回
	 * 传入参数：会话cookie；学生对应代码Map数组：studentCodeInfo；
	 * @return	HashMap<String, String>
	 * @date	2019/8/15 16:55
	 */
	public static HashMap<String, String> StudentInfoCrawlers(String cookie, HashMap<String, String> studentCodeInfo){
		//初始化数组，用于存放学生基本信息
		HashMap<String, String> studentBaseInfo = new HashMap<>(20);

		try {

			//个人信息查看页面跳转链接
			String url = "http://class.sise.com.cn:7001/SISEWeb/pub/course/courseViewAction.do?method=doMain&studentid={0}";

			//new一个HTTPClient对象
			HttpClient httpClient = new HttpClient();

			// 进行登陆后的操作，跳转到信息页面
			//获取home页面
			GetMethod getMethodForHome = new GetMethod(MessageFormat.format(url, studentCodeInfo.get("studentid")));
			//设置cookie请求头
			getMethodForHome.setRequestHeader("cookie", cookie);
			//发送执行
			Thread.sleep(300);
			httpClient.executeMethod(getMethodForHome);
			//将服务器返回的个人信息页面html文本保存在字符串中
			String homeHtml = getMethodForHome.getResponseBodyAsString();
			//使用完后关闭连接
			getMethodForHome.releaseConnection();

			//使用jsoup解析
			Document homeDocument = Jsoup.parse(homeHtml);

			//获取信息名标签，如：学号
			Elements tdRightClassElements = homeDocument.getElementsByClass("td_right");
			//获取信息内容Element，如：1840332132
			Elements tdLeftClassElements = homeDocument.getElementsByClass("td_left");
			for (int i = 0; i < tdRightClassElements.size(); i++){
				//获取text,除去：和空格
				String label = tdRightClassElements.get(i).text().replace("：", "").replace(" ", "");

				if (label.isEmpty() | label == ""){
					//如果label是空的，则跳过
					continue;
				}else {
					//否则存入数组
					studentBaseInfo.put(label, tdLeftClassElements.get(i).text().trim());
				}
			}

		}catch (Throwable t){
			t.printStackTrace();
		}
		return studentBaseInfo;
	}
}
