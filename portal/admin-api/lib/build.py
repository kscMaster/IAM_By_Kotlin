#!/usr/bin/env python3
#coding=utf-8

import os, sys, getopt ,pexpect,shutil
from pprint import pprint
import git_update

version=""
server_ip="39.107.85.115"
server_pwd=""
server_java_path=""
server_html_path=""
local_java_path="app.shop.java/admin"
local_html_path="app.shop.html/admin"
app_name="品致信后台"
app_jar_name="shop-admin-1.0.1.jar"
lang = ""
execute = True
process=[]

base_path =  os.path.abspath( os.path.join( __file__ ,"../../../../" ) )
os.chdir( base_path + "/" + local_java_path )

def help():
    #print(sys.version)
    print ('''  %s发版工具
        -v --version,[prod|test|dev]
        -c --only print command
        -h --help 帮助
    ''' %(app_name) );

    sys.exit();

def init():
    global version,app_name,server_java_path,server_html_path,execute
    opts, args = getopt.getopt(sys.argv[1:], "v:ch" ,["version=","command","help"] )

    for c, v in opts:
        if c in ("-v", "--version"):
            version = v
        if c in ("-c", "--command"):
            execute = False
        elif c in ( "-h", "--help" ):
            help()

    if version == "" :
        help();

    if version == "" :
        version="prod"

    if  version == "prod" :
        app_name += "正式版"
        server_java_path="/var/www/shop/admin/java"
        server_html_path="/var/www/shop/admin/html"
    elif  version == "test" :
        app_name += "测试版"
        server_java_path="/var/test/shop/admin/java"
        server_html_path="/var/test/shop/admin/html"
    elif version == "dev" :
        app_name += "开发版"
        server_java_path="/var/dev/shop/admin/java"
        server_html_path="/var/dev/shop/admin/html"
    else:
        help();


    app_name += "(" + version + ")"

    print()
    print( "=============================================")
    print()
    print( "             " + app_name   )
    print()
    print( "=============================================")
    print()

def git__update():
    if execute:
        git_update.git_update(version);

def rm(path):
    if  os.path.exists( base_path + path) :
        print("rm -R " + baes_path + path)

        if execute:
            shutil.rmtree(base_path + path )

def rm_vue2():
    cmds = "dist,dict_cn,dist_en,html_cn,html_en,src_cn,src_en".split(",")
    for cmd in cmds:
        rm(base_path + "/" + local_html_path +"/" + cmd)

def p(code):
    if( code >> 8 ):
        print();
        print("系统出现错误!")
        sys.exit();

def scp_web():
    path="%s/%s/dist%s"%(base_path,local_html_path, "_" + lang if lang else "" )
    for file in os.listdir( path)  :
        cmd = '''scp -r %s root@%s:%s/''' % (os.path.join(path,file),server_ip,server_html_path + ("_" + lang if lang else "") )
        print(cmd)
        if execute:
            child=pexpect.spawn(cmd)
            child.expect("password:", timeout=5);
            child.sendline(server_pwd);
            child.interact()
            child.close()

def scp():
    print('''%s 前端程序打包完成,开始上传...'''%(app_name));
    cmd = '''scp -r %s/%s/target/%s root@%s:%s/''' % (base_path, local_java_path, app_jar_name, server_ip,server_java_path)
    print(cmd)
    if execute :
        child=pexpect.spawn(cmd)
        child.expect("password:", timeout=5);
        child.sendline(server_pwd);
        child.interact()
        child.close()



def ssh():
    cmd = 'ssh  %s@%s' % ("root",server_ip)
    print(cmd)

    if execute:
        child=pexpect.spawn(cmd, timeout=3)
        child.expect("password:", timeout=5);
        child.sendline(server_pwd);

        child.expect("root@", timeout=5);
        child.sendline("cd %s"%(server_java_path));


    runjvm_option= "";

    if version != "prod" :
        runjvm_option += "-Xmx150m"
    else :
        runjvm_option += "-Xmx500m"

    cmd = "ps aux | grep  \"%s --spring.profiles.active=%s\" | awk '{print $2}'  |xargs kill -9"%(app_jar_name, version);
    print(cmd);

    if execute:
        child.sendline(cmd);
        child.expect("root@", timeout=3);

    cmd="nohup  java %s -jar %s/%s --spring.profiles.active=%s --jwdmq.consumer=true >/dev/null 2>&1 &"%(runjvm_option,server_java_path, app_jar_name, version)
    print(cmd)

    if execute:
        child.sendline(cmd);

        child.expect("root@", timeout=5);
        child.sendline("");

        child.expect("root@", timeout=5);
        child.sendline("exit");

        child.interact()
        child.close()

if __name__=='__main__':
    init();

    os.chdir( base_path + "/" + local_java_path )
    os.chdir("../");
    git__update();
    os.chdir( base_path + "/" + local_java_path )


    cmd = '''mvn clean package -Dmaven.test.skip=true'''
    print(cmd);
    if execute:
        p(os.system(cmd))

    print('''%s Java程序打包完成,打包 Html 部分...'''%(app_name) );

    os.chdir( base_path + "/" + local_html_path )

    rm_vue2();

    os.chdir("../");
    git__update();
    os.chdir( base_path + "/" + local_html_path )

    cmd = '''npm install
    npm run build %s %s'''%( lang, version )
    print(cmd)

    if execute :
        p(os.system(cmd))


    scp()
    ssh()
    scp_web();

    print('');
    print('''%s 发布完成'''%(app_name));

