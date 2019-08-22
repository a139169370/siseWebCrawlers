import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * @author Lucien
 * @date 2019/08/15 18:18
 * @description 爬取studentdetail.jsp页面信息
 */

public class StudentDetailPageCrawlers {
	/**
	 * 方法实现说明
	 * @author	lucien
	 * 传入参数：会话cookie；StudentIndexPageCrawlers类下的studentCodeCrawlers方法返回的studentBaseInfo；
	 * 传入参数：专业代码如SO18；StudentIndexPageCrawlers类下的studentCodeCrawlers方法返回的studentCodeInfo；
	 * @return	爬取studentdetail.jsp页面信息并写入studentBaseInfo的HashMap
	 * @date	2019/8/15 18:22
	 */
	public static HashMap<String, String> studentInfoCrawlers(String cookie,String specialityGrade, HashMap<String, String> studentCodeInfo, HashMap<String, String> studentBaseInfo){
		//跳转URL
		String url = "http://class.sise.com.cn:7001/SISEWeb/module/sise/studentsearch/studentdetail.jsp?studid={0}&speciality_grade={1}";
		try {
			//new一个HTTPClient对象
			HttpClient httpClient = new HttpClient();

			// 进行登陆后的操作，跳转到信息页面
			//获取home页面
			GetMethod getMethodForHome = new GetMethod(MessageFormat.format(url, studentCodeInfo.get("sutdentidnum"), specialityGrade));
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

			//获取所有信息并写入数组
			Elements labelElements = homeDocument.getElementsByAttributeValue("align", "right");
			for (int i = 0; i < labelElements.size(); i++){
				//判断过滤条件
				if (i == 0){
					continue;
				} else if (labelElements.get(i).text().replace(" ", "").contains("学号") | labelElements.get(i).text().replace(" ", "").compareTo("姓名") == 0 | labelElements.get(i).text().replace(" ", "").contains("身份证") | labelElements.get(i).text().replace(" ", "").contains("电子邮件")) {
					continue;
				}
				//写入数组，除去：和空格
				studentBaseInfo.put(labelElements.get(i).text().replace(" ", "").replace("：", ""), labelElements.get(i).parent().nextElementSibling().text().trim());
			}

			/**
			 * 代码块实现说明
			 * @author	lucien
			 * 将图片写入本地
			 * @date	2019/8/15 19:30
			 */
			//获取图片URL
			String photoUrl = homeDocument.getElementsByTag("img").get(1).attr("src").trim().split("sise")[1];
			photoUrl = "http://class.sise.com.cn:7001/sise" + photoUrl;
			//获取图片
			HttpClient client = new HttpClient();
			GetMethod getPhoto = new GetMethod(photoUrl);
			client.executeMethod(getPhoto);

			//输出路径例如：h:/sise/studentPhoto/2017/123456.jpg
			File storeFile = new File(MessageFormat.format("h:/sise/studentPhoto/{0}", photoUrl.split("studentphoto/")[1]));

			FileOutputStream output = new FileOutputStream(storeFile);
			// 得到网络资源的字节数组，并写入文件
			output.write(getPhoto.getResponseBody());
			output.close();
			getPhoto.releaseConnection();

		}catch (Throwable t){
			t.printStackTrace();
		}
		return studentBaseInfo;
	}
}
