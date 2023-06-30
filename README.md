# 学成在线
这是一个用于学习SpringCloud等技术综合使用的项目...

# (因情况而异的网络配置)
在虚拟机中配置了ip192.168.153.135

# 联网、连接SSH
关闭防火墙、selinux、iptable
systemctl stop firewalld
vi /etc/selinux/config  ==> SELINUX="disable"
service iptable stop
service network restart
systemctl restart network

# ifconfig失效
在/etc/sysconfig/network-scripts/ifcfg-ens33中进行编辑
ONBOOT=yes
安装yum install net-tools

# 检查ssh状态
service sshd status

# 通过docker预装一些程序
在Linux中，安装docker

使用docker下载
mysql 数据库
redis 非关系型数据库
nginx 反向代理服务器（预装程序：yum install -y gcc pcre pcre-devel zlib zlib-devel openssl openssl-devel；
gcc:C语言解析器
pcre:http兼容正则表达式库
zlib:对各个模块进行压缩
openssl:安全通信加密
）
rabbitmq 异步式消息队列
minio 大型分布式存储方案
kibana 分析可视化平台，于Elasticsearch一起使用
elasticsearch 分布式、restful风格的搜索和数据分析引擎
gogs 极易搭建的自助git服务
nacos 服务注册中心（使用1.4.1，新版本不明原因不可下载)

# 使用git控制版本，搭建gogs
* （也可以使用github，不过搭建私服仓库也属于一种技能）


修改容器自启动：docker update --restart=always nacosConteiner
docker无法找到并运行已下载容器——直接使用IMAGE ID：docker run --name nacosContainer -e MODE=standalone -p 8849:8848 -d  eec289123412

* （1）Linux系统中搭建Gogs、MySQl
docker run --name=mysql -p 3306:3306 -v mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0.32
bash界面控制mysql: docker exec -it mysql(容器名称) bash

docker run --name gogsContainer -p 10022:22 -p 10880:3000 -v gogs:/data/gogs/gogs -d gogs/gogs:0.13
进入浏览器搜索（192.168.153.135:10880)进行首次安装程序
数据库类型选择目前安装的MySQL
mysql在docker中进行配置
创建新的数据卷mysql-data
用户名、密码使用root,root，数据库名称定义后

* （2）回到本Windows实机，使用本地git对远程私人git仓库创建连接
（现在是个人使用git bash方法进行的测试，实际上是在idea中定义git的版本控制）
打开项目目录，并在目录中启动git bash
git init
git add README.md
git commit -m "first commit"
git remote add origin http://192.168.153.135:10880/root/xuecheng-plus.git
（切换url地址： git remote set-url origin http://group.51xuecheng.cn:3000/root/xuecheng-plus.git）
.解读 创建新的git远程推送，名称为origin，远程仓库地址为.../xuecheng-plus.git
git push -u origin #(master/dev)注意想推送哪个分支
.将用户master的当前版本推送到远程仓库，发送地址为origin定义

* （3）在idea中推送版本更新
和git bush大同小异，而且idea优化了可视化界面，不用输入部分代码
在idea创建新分支：dev（在界面右下角），创建成功后可以看到分支已经被切换到dev
打开git log（日志），小铅笔表示当前分支，日志界面右键dev，将其推送到远程仓库
接下来在dev分支开发，完成后才合并到master
文档中的要求：完成任务前都创建新的分支，在新的分支中进行

(使用github：第一次使用需要注意sshkey的验证，在本地初始化一个sshkey的命令是：$ ssh-keygen -t rsa -C 'youzou_rabbit@163.com'，然后把制定目录下的.pub内容复制到github中的ssh秘钥上，否则github的上传、拉取代码将无法运行)

# 开始创建父工程
在文件->项目结构中编辑，
项目栏，选择正确的jdk和语言级别
模块栏，创建新的模块xuecheng-plus-parent，使用spring initialaizer，创建空项目
创建成功，除了pom全部删除

* （面试）两个子分支在合并到主分支上遇到相同目录且同名不同代码
一般出现在1、多个分支向主分支合并2、同一分支下pull或push
需求分析

* 内容管理模块：需求分析--模块介绍--业务流程--界面原型--数据模型

# 创建xuecheng-plus-content
负责聚合其余的模块，除了parent和base，其余模块都直接或间接继承此模块（xuecheng-plus-base被数据模型依赖）
总结出的内容管理模块总共有四个工程：content、content-api、content-service、content-model

# 创建generator模块
（或者说是直接复制），把pom文件添加到Maven工程，然后选择ContentCodeGenerator类，把数据库各种信息修改为自己需要的信息，修改完成执行main方法，就会自动生成一个content文件夹（软件包），它是根据数据库自动生成的controller、mapper、model.po、service，简化代码的编写

把自动生成的model.po部分复制到content子模块的content-model中，注意创建相同的包路径...po再复制进去，此时还有部分依赖没有导入所以会报错，之后导入即可（mybatis-plus-annotation、mybatis-plus-core）

# 接口设计
1、协议指定（http）：最基本要求，给出接口协议，把接口实现，这是第一级要求；不给接口协议，给需求文档，实现接口，是二级要求；没有协议，没有需求文档，给出大的模块，实现所有模块的结构，这是第三级要求（或者给一个模块、给一个系统，就把接口搞定）（注：企业更需要第三级别的人才）。确定content-type（get/post/put/delete......）
2、分析请求参数（按需求对请求、响应的参数模型类）
3、分析响应结果

# DTO:数据传输对象
PO:持久化对象
VO:有时会使用，用作前段与接口之间传输数据（如果前段访问对象比较固定，就可以不适用VO）

# 通过Swagge生成接口文档
(https://swagger.io)
在pom文件中添加swagger依赖
在yml文件中添加基本路径、名称、描述等信息，在启动类中添加@EnableSwagger2Doc注解
把启动类运行，访问api所处的路径:端口/content/swagger-ui.html，可以查看api详细信息
这个文档存在两个问题：
1、接口名称显示course-base-info-controller名称不直观
2、课程查询是post方式只显示post /course/list即可。

可以在启动类中添加@Api注解，优化文字替换，实现更加友好的汉化
1、指定响应对象的参数进行注释
@ApiModelProperty("页码")
private Long pageNo = 1L;
2、在content-api的类中注释
【类外】@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")//修改Swagger主页的英文，用汉字显示
【类中特定Mapping】@ApiOperation("课程查询接口") //告诉创建的文档，这个接口是“课程查询接口”

# 接口设计（最基本能力）
定义模型类
生成接口文档
写出持久层（Mapper）

接口测试工具http client
相比之下，postman需要额外启动一个应用，比较笨重
swagger无法保存测试填入的数据
http client是IDEA的插件，是一个优秀、轻量级的、乃至新版IDEA自带的插件，它会随着git上传一起被保存到仓库，它是mapping旁边的“小地球”图标，点击，选择生成http请求，将会在idea中生成一个http请求测试窗口，输入以下数据测试service：
###
POST http://localhost:63040/content/course/list?pageSize=2&pageNo=1
Content-Type: application/json

{
"audioStatus": "202004",
"course": "java"
}
此时的测试记录还是比较难管理，在项目根目录创建一个api-test目录，在目录中，以模块为单位创建.http文件
同目录下创建http-client.env.json，可定义常量参数

为提高数据日后修改显示内容的方便，创建一个数据库存放数据字典

# 准备前后端联调：
前段人员会使用mock数据（假数据）进行开发，当开始联调，前段将会把mock数据换成后端接口
本项目推荐使用node.v16，在idea设置中配置yarn，发现没有，可以使用npm，但请使用npm安装yarn，命令——npm install -g yarn
如果前端程序运行出现问题，修改云镜像仓库：npm install -g cnpm --registry=https://registry.npm.taobao.org
运行cnpm i        运行npm run server
...
前端的页面报错部分先忽略，把xuecheng-plus-system文件夹导入到java项目根目录下
再次运行，浏览器报错：跨域问题

# 解决跨域问题的几种方案：

1、JSONP
请求端发起jsonp请求，名称不是固定的（本次示范为fun）
<script>
	function fun(result){}
		//获取响应结果
		alert(result);
}
</script>
<script src="http://跨域地址?callback=fun">
</script>
服务端收到请求
读取callback参数值fun
发送响应结果

2、添加响应头
3、通过nginx代理跨域

dokcer数据卷：创建一个真机上的文件夹，它是虚拟的文件夹，而它指向一个docker容器中的真实文件夹......
VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
（案例）创建一个Docker容器
步骤一：在dockerhub上查看nginx容器的运行命令
docker run --name containerName -p 80:80 -d nginx
命令解读：
1.docker run 创建并运行一个容器
2.-name 给容器起名
3.-p 将宿主机端口与容器端口映射，冒号左侧是宿主机端口，右侧是容器端口
（容器本身是隔离的，所以要做端口映射，外部通过访问宿主机80端口，宿主机转到容器80端口）
（一般容器的端口是不能随意更改的，比如nginx使用80端口，而宿主机只需该端口不被占用即可）
4. -d 后台运行
5. nginx 指定镜像名称

（案例）进入nginx容器，修改html内容（在Docker的Nginx官方文档内部有详细入门教程文档，介绍了静态文件夹的路径）
1.进入容器（刚刚创建的nginx）：docker exec -it containerName bash
命令解读：docker exec：进入容器内部，执行一个命令
-it：给当前进入的容器创建一个标准输入、输出终端，允许与容器进行交互
mn：要进入容器的名称
bash：进入容器后执行的命令，bash是与linux终端交互命令
2.进入内部容器以后，按官方文档给出的路径对静态页面的内容进行修改
（不过docker不会自带vim编辑器，所以使用一个新的指令对静态页面内容进行修改：
sed -i -e 's#Welcome to nginx#传智教育欢迎您#g' -e 's#<head>#<head><meta charset="utf-8">#g' index.html）

（案例）进入redis
方式1：
docker exec -it redisContainer bash （启动bash终端，容器名称为自定义的redisContainer）
redis-cli （开启客户端）
方式2：
docker exec -it redisContainer redis-cli （一步启动到客户端）

截止上面的docker存在的问题：容器与数据耦合问题
1、数据不便修改（进入容器内部修改并不简单，而且内部不包括vim编辑器，修改困难）
2、数据不可复用（所有容器修改都是对外不可见的。所有修改对新创建的容器不可复用）
3、升级维护困难（数据在容器内，要升级必须要删除旧容器，所有数据也跟着删除了）
====>解决方案：数据卷

数据卷是在宿主机虚拟文件夹映射容器中的真实文件
两个容器可以挂载同一个目录
容器删除以后，数据卷依旧完好

# 数据卷入门：
常用命令
docker volume [COMMAND]
create（创建一个volume） + 创建的文件夹名
inspect（显示一个或多个volume信息）
ls（列出所有volume信息）
prune（删除未使用的volume）
rm（删除指定volume）

数据卷的挂载：
docker run \ # 创建并运行容器
--name mn \ # 给容器起名
-v html:/root/html \ # ！把html这个容器挂载到宿主机的/root/html/目录下
-p 8080:80 \  # 把宿主机的8080端口映射到容器内的80端口
nginx # 命令执行的镜像名称

（案例）创建一个nginx容器，修改容器内的html目录内的index.html内容
1、启动一个新的容器（容器内的静态页面或者其它信息可以从docker官网获取）
docker run --name mn -p 80:80 -v html:/usr/share/nginx/html -d nginx
2、如何查看挂载效果
docker inspect xxx 查看指定容器的信息
cd xxx 打开Mountpoint中的路径，内部文件就是映射到容器的文件

.......跳过了一些，用到了再学，现在可以看得懂docker基本操作就ok
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

（2023.05.16——忙活了好几天，终于把nacos配置在云服务器上了，内存吃紧的问题也解决了）
nacos复习：nacos是一个服务发现中心。
配置三要素：namespace group dataid(新知识)
dataid可以找到具体配置文件，有三部分组成（比如content-service-dev.yaml是由content-service+dev+yaml三部分组成）
yml文件必须添加
profile:
active: dev
* 导入nacos-config依赖，将部分配置文件上传至nacos进行统一管理，取而代之的是nacos配置的三要素
      config:
        namespace: dev
        group: xuecheng-plus-project
        file-extension: yml
        refresh-enabled: true

nacos配置本地最优先：(对复制的配置进行本地配置——顶部运行按钮旁边，编辑配置，VM选项中填入-Dserver.port=63041 spring.profile.active=test,本地配置优先级默认最低)
spring:
cloud:
config:
override-none: true


