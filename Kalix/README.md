# KALIX项目的部署

kalix校园信息化项目的部署，包括后台karaf、前台vue

## 后台的安装

```
$ yum update  更新系统
$ systemctl stop firewalld
```

### 1.安装java环境 JDK(1.8版本)
检查及删除
```
$ java -version  查看已经安装的JAVA版本信息
$ rpm -qa|grep java  查看JDK的信息
$ rpm -qa | grep openjdk  查看JDK信息

$ rpm -e --nodeps java-1.6.0-openjdk-xxx  卸载OpenJDK
$ yum remove -y java-1.8.0-openjdk*
```

安装方式一
```
$ yum install -y java-1.8.0-openjdk*
$ which java
```

安装方式二，需要提前下载.gz或rpm包

官网 http://www.oracle.com/technetwork/java/javase/downloads/index.html，需要注册
```
$ mkdir /usr/java  建立java程序安装目录
$ cd /usr/java  查看/usr/java目录(放入下载安装包)
$ tar -xzvf jdk-7u13-linux-x64.gz  .gz包解压后，在/usr/java目录下就会生成一个新的目录jdk1.7.0_13，该目录下存放的是解压后的文件
$ rpm -ivh jdk-7-linux-x64.rpm  rpm包，运行命令
$ mv jdk1.7.0_13 jdk  生成的目录jdk1.7.0_13改名为jdk
```

安装方式三(推荐)
```
$ mkdir /usr/java  建立java程序安装目录
$ cd /usr/java
$ wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u141-b15/336fa29ff2bb4ef291e347e091f7f4a7/jdk-8u141-linux-x64.tar.gz"
$ tar -xzvf jdk-8u141-linux-x64.tar.gz
```

```
$ mv jdk-8u141-linux-x64.tar.gz /usr/java  如果wget直接下载，下载文件位于当前目录，需要移动文件
```

设置环境变量，"/etc/profile"文件行"export PATH USER LOGNAME MAIL HOSTNAME HISTSIZE HISTCONTROL"上面增加
```
$ vi /etc/profile
  JAVA_HOME=/usr/java/jdk1.8.0_141
  JRE_HOME=/usr/java/jdk1.8.0_141/jre
  PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
  CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib
  export JAVA_HOME JRE_HOME PATH CLASSPATH

$ source /etc/profile  让环境变量立即生效
$ echo $PATH  查看系统环境状态
$ java -version
```

### 2.安装git
```
$ git --version  (git version 1.8.3.1)
$ yum install -y git
```

### 3.下载安装maven
参考：https://www.linuxidc.com/Linux/2015-02/112712.htm
```
$ wget -P /root/java-develop/tools/ http://mirror.bit.edu.cn/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
$ cd /root/java-develop/tools
$ tar -xzvf apache-maven-3.3.9-bin.tar.gz
```

设置环境变量，"/etc/profile"文件行"export PATH USER LOGNAME MAIL HOSTNAME HISTSIZE HISTCONTROL"上面增加
```
$ vi /etc/profile
  MAVEN_HOME=/root/java-develop/tools/apache-maven-3.3.9
  export MAVEN_HOME
  export PATH=${PATH}:${MAVEN_HOME}/bin

$ source /etc/profile  让环境变量立即生效
$ echo $PATH  查看系统环境状态
$ mvn -v
```

### 4.安装数据库
redis
参考：http://blog.csdn.net/gebitan505/article/details/54602662
https://blog.csdn.net/zhezhebie/article/details/73470134
```
$ yum install -y redis
$ /bin/systemctl enable redis.service
$ /bin/systemctl start redis.service
$ chkconfig redis on  开机自启动服务器

$ redis-server & 启动redis，加上`&`号使redis以后台程序方式运行

$ whereis redis-cli
$ whereis redis-server
$ ps -ef |grep redis  检测后台进程是否存在
$ netstat -lntp | grep 6379  检测6379端口是否在监听
$ redis-cli  使用redis-cli客户端检测连接是否正常
  > keys *
  > set key "hello world"
  > get key
$ redis-cli shutdown  停止redis，使用客户端
$ kill -9 PID  停止redis，Redis可以妥善处理SIGTERM信号，所以直接kill -9也是可以的
```

postgresql
参考：https://www.jianshu.com/p/7e95fd0bc91a
```

```

配置远程访问
```
$ vim /var/lib/pgsql/9.5/data/postgresql.conf
  修改#listen_addresses = 'localhost' 为 listen_addresses='*'
  *表示所有地址，也可以是指定的单个IP
$ vim /var/lib/pgsql/9.5/data/pg_hba.conf
  修改如下内容，加入一行：
  # IPv4 local connections:
  host    all             all         0.0.0.0/0               md5
$ systemctl restart postgresql-9.5.service  修改配置后需要重启
```

couchdb
参考：http://docs.couchdb.org/en/2.1.1/install/unix.html#installation-using-the-apache-couchdb-convenience-binary-packages
```
$ systemctl status couchdb  查看couchdb服务
$ vim /etc/yum.repos.d/bintray-apache-couchdb-rpm.repo  创建文件，为安装couchdb提供仓库地址
$ i

[bintray--apache-couchdb-rpm]
name=bintray--apache-couchdb-rpm
baseurl=http://apache.bintray.com/couchdb-rpm/el$releasever/$basearch/
gpgcheck=0
repo_gpgcheck=0
enabled=1

$ esc
$ :wq
$ cat /etc/yum.repos.d/bintray-apache-couchdb-rpm.repo
$ yum install -y couchdb  安装couchdb
$ systemctl start couchdb  启动couchdb服务
$ netstat -lntp | grep 5984
$ curl http://127.0.0.1:5984
$ curl http://127.0.0.1:5984/_utils/
$ curl -I http://0.0.0.0:5984/_utils/index.html  检查couchdb是否正常工作

配置couchdb
$ cat /opt/couchdb/etc/local.ini
$ vim /opt/couchdb/etc/local.ini
  [chttpd]
  bind_address = 192.168.0.227
  [admins]
  admin = 123456
$ systemctl restart couchdb
$ systemctl status couchdb
$ curl -I http://192.168.0.227:5984/_utils/index.html
```

### 5.下载安装karaf4.1.2
下载地址 http://archive.apache.org/dist/karaf/4.1.2/apache-karaf-4.1.2.tar.gz
```
$ mkdir -p /root/java-develop/tools
$ wget -P /root/java-develop/tools/ http://archive.apache.org/dist/karaf/4.1.2/apache-karaf-4.1.2.tar.gz
$ cd /root/java-develop/tools
$ tar -xzvf apache-karaf-4.1.2.tar.gz
```

运行
```
$ /root/java-develop/tools/apache-karaf-4.1.2/bin/karaf  前台启动
$ /root/java-develop/tools/apache-karaf-4.1.2/bin/start  后台启动
$ /root/java-develop/tools/apache-karaf-4.1.2/bin/client 后台启动后查看控制台
$ /root/java-develop/tools/apache-karaf-4.1.2/bin/stop   停止
```

前台启动karaf
```
karaf@root()> feature:repo-add mvn:com.kalix.tools/tools-karaf-features/1.0.1-SNAPSHOT/xml/features
karaf@root()> feature:install -v kalix-base
```

常用命令
```
$ nmap localhost
$ ps -aux|grep 8181
$ netstat -nat|grep 8181
$ netstat -lntp | grep 8181  检测8181端口是否在监听
karaf@root()> logout
```

### 6.下载或更新项目，安装项目
```
$ mkdir -p /root/java-develop/project
$ cd /root/java-develop/project

$ git clone https://github.com/chenyanxu/kalix-parent.git
$ git clone https://github.com/chenyanxu/framework-parent.git
$ git clone https://github.com/chenyanxu/admin-parent.git
$ git clone https://github.com/chenyanxu/middleware-parent.git
$ git clone https://github.com/chenyanxu/oa-parent.git
$ git clone https://github.com/chenyanxu/common-parent.git
$ git clone https://github.com/chenyanxu/schedule-parent.git
$ git clone https://github.com/chenyanxu/tools-parent.git
$ git clone https://github.com/chenyanxu/research-parent.git
$ git clone https://github.com/chenyanxu/art-parent.git
或
$ cd /root/java-develop/project
$ chmod a+x gitclone.sh
$ /root/java-develop/project/gitclone.sh  下载

$ chmod a+x gitpull.sh
$ /root/java-develop/project/gitpull.sh  更新

$ chmod a+x install.sh
$ /root/java-develop/project/install.sh  安装

$ chmod a+x build.sh
$ /root/java-develop/project/build.sh  更新并安装
```

### 7.add openjpa maven jar
```
$ mvn install:install-file -Dfile=./openjpa-2.4.0.Release.jar -DgroupId=org.apache.openjpa -DartifactId=openjpa -Dversion=2.4.0.Release -Dpackaging=jar
$ mvn install:install-file -Dfile=./openjpa-2.4.0.Release.pom -DgroupId=org.apache.openjpa -DartifactId=openjpa -Dversion=2.4.0.Release -Dpackaging=pom
```

### 8.添加防火墙规则
```
$ firewall-cmd --zone=public --add-port=80/tcp --permanent
  命令含义：
  --zone #作用域
  --add-port=80/tcp 添加端口，格式为：端口/通讯协议
  --permanent 永久生效，没有此参数重启后失效
$ systemctl restart firewalld
```

## 前台的安装

```
$ node -v
$ npm -v
$ cnpm -v
```

### 1.下载安装nginx
安装
```
$ nginx -v
$ yum install -y nginx
```

运行
```
$ /usr/sbin/nginx

$ curl http://125.222.244.22
```

### 2.下载或更新项目，安装项目
下载
```
$ mkdir -p /root/vue-project
$ cd /root/vue-project
$ git clone https://github.com/minikiller/kalix-vue-project.git
$ git clone https://github.com/minikiller/vue-mobile-art.git
```

更新
```
$ cd /root/vue-project/kalix-vue-project
$ git pull origin master
$ cd /root/vue-project/vue-mobile-art
$ git pull origin master
```

安装项目配置nginx，参考：https://blog.csdn.net/marksinoberg/article/details/77816991
```
修改文件如下：
kalix-vue-project/src/config/global.toml
  baseURL = "http://125.222.244.22:8181"
vue-mobile-art/config/index.js
  host: '125.222.244.22'
  port: 8383
vue-mobile-art/src/config/global.toml
  baseURL = "http://125.222.244.22:8181"
  webURL = "http://125.222.244.22:8383"

$ cd /root/vue-project/kalix-vue-project
$ npm install
$ npm run build
$ mv dist/ /usr/share/nginx/
$ cd /usr/share/nginx
$ mv dist/ kalix-vue-project/
$ cd /root/vue-project/vue-mobile-art
$ npm install
$ npm run build
$ mv dist/ /usr/share/nginx/
$ cd /usr/share/nginx
$ mv dist/ vue-mobile-art/

配置nginx，/etc/nginx/conf.d/
复制kalix-vue-project.conf和vue-mobile-art 到 /etc/nginx/conf.d/

$ nginx -t -c /etc/nginx/nginx.conf  判断Nginx配置是否正确
$ nginx -t  判断Nginx配置是否正确
$ ps -ef | grep nginx  查询nginx主进程号
$ pkill -9 nginx  强制停止Nginx
$ /usr/sbin/nginx  启动Nginx
$ nginx 启动Nginx
$ nginx -c /etc/nginx/nginx.conf 启动Nginx
$ nginx -s reload  平滑重启命令
$ nginx -s stop  停止Nginx
```
