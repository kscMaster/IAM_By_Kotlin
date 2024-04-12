#!/usr/bin/env bash
source /etc/profile
cd $(dirname $0)
pwd
app_name=cms_shop_admin_api
ps -ef | grep $app_name | grep -v grep | awk '{print $2}' | xargs -r kill -9
nohup java -Xmx510m  -Dloader.path=libs-admin  -Dfile.encoding=utf-8 -jar admin-api-1.0.1.jar \
  --spring.profiles.active=server --name=$app_name \
  --server.port=8111 \
  --server.filter.allow-origins=dev8.cn \
  --app.scheduler=false \
  --server.upload.path=/opt/data/file \
  --server.upload.host=http://file-edu.huidd365.com \
  >/dev/null 2>&1


#服务上直接运行: ./radmin.sh