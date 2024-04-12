#!/usr/bin/env python3
#coding=utf-8

import os, sys, getopt ,pexpect,shutil

version=""
name=""

def help():
    print ('''  金维度 git update 工具
        [prod|test|dev]
    ''');

    sys.exit();

def init():
    global version,name;
    if version == "" :
        version="prod"

    if  version == "prod" :
        version = "master"
        name= "正式版"
    elif  version == "test" :
        name= "测试版"
    elif version == "dev" :
        name= "开发版"
    else:
        help();

def p(code):
    if( code >> 8 ):
        print();
        print("系统出现错误!")
        sys.exit();

def git_update(ver):
    global version
    version = ver

    init();

    print();
    print(  '''正在获取代码 %s %s(%s) ...'''%(os.getcwd(),name,version))
    print()

    if  os.path.exists('/.git/index.lock') :
        os.remove("/.git/index.lock" )


    child=pexpect.spawn('git fetch' )
    child.expect ('Username for', timeout=5)
    child.sendline( "yuxinhai@topjwd.com")

    child.expect ('Password for', timeout=5)
    child.sendline( "dev8--udi")

    child.interact()
    child.close()

    p(os.system( 'git clean -df') )
    p(os.system( 'git checkout --force ' + version ) )
    p(os.system( 'git reset --hard origin/' + version ))

if __name__=='__main__':
    git_update(sys.argv[1]);