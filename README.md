# 华软教务系统爬虫

拉取基本信息和图片，分别存于数据库和本地中；  
登录验证没写，用的方法是先网页登录拿到cookie再放进来；  
图片路径用的绝对路径，可能需要改一下；  
每次发起网络请求前会先休眠200-300ms，防止频率过快封IP  
可能会报数据库异常“主键已存在”，因为主键用的学号，而页面上会出现两个专业目录下都有该学生的情况，如软件工程和软件工程（软件开发）；  

###数据库
数据库配置在Config接口，表结构在Tool目录下的psc文件  
表名直接写在Database类里了，如果要改表名就改一下  