# Word2pdf2jpg

实现word(doc\docx)转换成pdf，pdf转换成jpg

方法1 poi读取doc + itext生成pdf （实现最方便，效果最差，跨平台）

使用jdoctopdf来实现，这是一个封装好的包，可以把doc转换成pdf，html，xml等格式，调用很方便

方法2 jodconverter + openOffice （一般格式实现效果还行，复杂格式容易有错位，跨平台）

使用jodconverter来调用openOffice的服务来转换，openOffice有个各个平台的版本，所以这种方法跟方法1一样都是跨平台的

方法3 jacob + msOfficeWord + SaveAsPDFandXPS （完美保持原doc格式，效率最慢，只能在windows环境下进行）

效果最好的一种方法，但是需要window环境，而且速度是最慢的需要安装msofficeWord以及SaveAsPDFandXPS.exe(word的一个插件，用来把word转化为pdf)

本文使用方法2和方法3实现

## jacob + msOfficeWord + SaveAsPDFandXPS

### 准备环境：

需要window环境，需要安装msofficeWord以及SaveAsPDFandXPS.exe(word的一个插件，用来把word转化为pdf)

Office版本是2007+，因为SaveAsPDFandXPS是微软为office2007及以上版本开发的插件

SaveAsPDFandXPS下载地址：http://www.microsoft.com/zh-cn/download/details.aspx?id=7 本机：jacob\SaveAsPDFandXPS.exe

jacob包下载地址：http://sourceforge.net/projects/jacob-project/ 本机：jacob\jacob-1.18.zip

jacob-1.18-x64.dll,jacob-1.18-x86.dll 拷贝到 C:\Program Files\Java\jdk1.8.0_66\jre\bin

### 操作

word转pdf：项目中引用jacob.jar，如下

maven pom.xml
```
<dependency>
    <groupId>cn.boltit.s421.jacob</groupId>
    <artifactId>jacob</artifactId>
    <version>1.8</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/src/main/webapp/WEB-INF/lib/jacob.jar</systemPath>
</dependency>
```

实现代码：jacob\JacobUtil.java

执行main函数，如果报错：
```
如果代码提示：
Error:文档转换失败：Invoke of: SaveAs
Source: Microsoft Word
Description:
```
安装SaveAsPDFandXPS.exe，否则不用安装SaveAsPDFandXPS.exe

pdf转jpg：项目中引用pdfbox和fontbox，如下

maven pom.xml
```
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.9</version>
</dependency>
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>fontbox</artifactId>
    <version>2.0.9</version>
</dependency>
```

实现代码：jacob\JacobUtil.java

## jodconverter + openOffice

openOffice在线下载地址：https://www.openoffice.org/download/

### windows环境

安装openOffice 本机：jodconverter\Apache_OpenOffice_4.1.5_Win_x86_install_zh-CN.exe

启动服务 cmd命令
```
> cd C:\Program Files\OpenOffice 4\program
> soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard
```

查看端口是否启动
```
> netstat -ano|findstr 8100
```
或者查看windows进程
soffice.bin
soffice.exe

### linux环境

CentOS-7

安装openOffice 本机：jodconverter\Apache_OpenOffice_4.1.5_Linux_x86-64_install-rpm_zh-CN.tar.gz

```
$ tar -xzvf Apache_OpenOffice_4.1.5_Linux_x86-64_install-rpm_zh-CN.tar.gz
$ cd /home/zh-CN/RPMS
$ yum localinstall -y *.rpm 或 rpm -ivh *.rpm
$ cd desktop-integration
$ yum localinstall -y openoffice4.1.5-redhat-menus-4.1.5-9789.noarch.rpm 或 rpm -ivh openoffice4.1.5-redhat-menus-4.1.5-9789.noarch.rpm
$ cd /opt/openoffice4/program
$ netstat -tln
$ soffice -headless -accept="socket,host=127.0.0.1,port=8200;urp;"
```

临时启动服务
```
$ /opt/openoffice4/program/soffice -headless -accept="socket,host=127.0.0.1,port=8200;urp;" -nofirststartwizard
```

永久启动服务
```
$ nohup /opt/openoffice4/program/soffice -headless -accept="socket,host=127.0.0.1,port=8200;urp;" -nofirststartwizard &
```

查看端口是否启动
```
$ netstat -tln
$ netstat -lnp |grep 8200
```

安装后启动服务报错解决方法：

/opt/openoffice4/program/下缺少libXext.so.6文件，可以去/usr/lib64　或者　/usr/lib　查看有没有这个文件，
如果有就copy到/opt/openoffice4/program/目录里面，如果没有安装
```
$ yum install -y libXext.x86_64
$ cp -a /usr/lib64/libXext.so.6 /opt/openoffice4/program/
```

找不到libfreetype.so.6文件
```
$ yum install -y freetype
$ cp -a /usr/lib64/libfreetype.so.6 /opt/openoffice4/program/
```

no suitable windowing system found, exiting.
```
$ yum groupinstall -y "X Window System"
```

### 操作

word转pdf：项目中引用jodconverter-2.2.2.jar及其依赖jar，如下

2.2.2以上版本支持docx，2.2.1仅支持doc

maven pom.xml
```
<dependency>
			<groupId>com.artofsolving</groupId>
			<artifactId>jodconverter</artifactId>
			<version>2.2.2</version>
			<scope>system</scope>
			<systemPath>${project.basedir}/src/main/webapp/WEB-INF/lib/jodconverter-2.2.2.jar</systemPath>
		</dependency>
		<dependency>
			<groupId>org.openoffice</groupId>
			<artifactId>ridl</artifactId>
			<version>3.0.1</version>
		</dependency>
		<dependency>
			<groupId>org.openoffice</groupId>
			<artifactId>juh</artifactId>
			<version>3.0.1</version>
		</dependency>
		<dependency>
			<groupId>org.openoffice</groupId>
			<artifactId>jurt</artifactId>
			<version>3.0.1</version>
		</dependency>
		<dependency>
			<groupId>org.openoffice</groupId>
			<artifactId>unoil</artifactId>
			<version>3.0.1</version>
		</dependency>
```

实现代码：jodconverter\JodconverterUtil.java

执行main函数

linux下测试，执行main函数：
```
$ cd /home
$ chmod -R 777 /home/jeesite
$ cd /home/jeesite/WEB-INF/classes

$ java -classpath . cn/boltit/s421/common/utils/JodconverterUtil
$ java -classpath ".:/home/jeesite/WEB-INF/lib/*" cn/boltit/s421/common/utils/JodconverterUtil
```

解决linux下，转换pdf字体不全问题，缺少字体库，拷贝windows下字体库到openOffice里

copy C:\Windows\Fonts to /opt/openoffice4/share/fonts/truetype
