# Docker的安装

## 检查安装docker前提条件

$ uname -a 内核

$ ls -l /sys/class/misc/device-mapper 检查device mapper

$ grep device-mapper /proc/devices 检查device mapper

$ yum install -y device-mapper 安装device mapper

$ modprobe dm_mod 加载dm_mod内核模块

## 安装docker

$ yum install docker -y

$ systemctl start docker 启动docker服务

$ systemctl enable docker 设置docker服务自启，重启自动加载

$ docker info 查看安装docker信息

## 安装docker加速器

$ curl -sSL https://get.daocloud.io/daotools/set_mirror.sh | sh -s http://4e70ba5d.m.daocloud.io

$ cat /etc/docker/daemon.json

### docker可能起不来的解决办法，多个逗号

vim /etc/docker/daemon.json 编辑，去掉","或者直接如下覆盖

    {"registry-mirrors": [
        "http://4e70ba5d.m.daocloud.io",
        "https://registry.docker-cn.com",
        "https://kuamavit.mirror.aliyuncs.com",
        "https://docker.mirrors.ustc.edu.cn"
    ]}

$ systemctl restart docker
