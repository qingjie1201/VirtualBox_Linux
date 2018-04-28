# VirtualBox_Linux

虚拟机安装LINUX系统

## 环境

VirtualBox-5.2.8

本机: linux\VirtualBox-5.2.8-121009-Win

官网下载地址：https://www.virtualbox.org/wiki/Downloads

CentOS-7.0-1406-x86_64

本机: linux\CentOS-7.0-1406-x86_64-DVD.iso

官网下载地址：https://wiki.centos.org/Download

SshClient

本机：linux\BvSshClient-Inst.exe

官网下载地址：

## 系统设置

### 设置网络：VirtualBox网络设置桥接

$ vi /etc/sysconfig/network-scripts/ifcfg-enp0s3

$ i 修改文件,新增或修改如下

    BOOTPROTO=static/dhcp/none
   	IPADDR=192.168.0.21
   	NETMASK=255.255.255.0
   	GETEWAY=192.168.0.1
   	DNS1=8.8.8.8
   	ONBOOT=yes

$ esc 退出

$ :wq 保存

$ :q! 不保存退出

$ systemctl restart network 重启网络配置

$ ping ip/baidu.com

$ yum update 更新系统

### 设置系统时区、时间

$ date 查看系统当前时间

$ yum install ntp -y

$ ntpdate 在系统联网的情况，修改时间最快的方法就是使用ntpdate命令自动同步网络服务器上的时间

$ ntpdate time.nist.gov (注：可能失败)

$ tzselect 查看，提示 修改/etc/sysconfig/clock    ZONE=Asia/Shanghai

$ rm /etc/localtime

$ ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime 链接到上海时区文件

### 常用软件安装

$ yum install vim -y	安装vim

$ yum install wget -y

$ yum install nmap -y

$ yum install git -y

$ yum install net-tools -y

### 设置hostname

$ uname -a  查看hostname

$ hostname  查看hostname

$ hostname examples.com  修改，让hostname立刻生效，但重启后无效，需要以下修改命令。

$ vi /etc/hostname 修改newname(如examples.com)

$ vi /etc/hosts  修改原hostname为newname

$ vi /etc/sysconfig/network  修改原hostname为newname(可能无此文件)

### 关闭防火墙

$ systemctl stop firewalld

$ systemctl disable firewalld

$ reboot 重启
