public class Main {

    public static void main(String[] args) {
		//年级
		String[] years = {"2016", "2017", "2018"};
		//TODO cookie是已登录教师系统后的cookie，注意前缀有JSESSIONID=；因为登录的时候要验证码，没有自动识别，又因为账号密码不能上传干脆直接用cookie了，有时间再优化了，years是要爬取信息的年级
		//TODO 在StudentDetailPageCrawlers类绝对路径输出照片，使用的时候可能需要改一下路径
		//数据库信息配置在Config接口
    	ViewStudentCrawlers.ViewStudentCrawlers("JSESSIONID=Lq9Qdpnc0n6bXDNxntThQqsNPyBNgHFHTqkJBTvnRsgdTW3vyvw3!-2060291676", years);
    }
}
