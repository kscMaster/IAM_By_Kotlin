#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
import sys
import shutil

cleanCMD = "mvn clean package -Dmaven.test.skip=true"
jarFile = "target/shop-entity-1.0.1.jar"
installCMD="mvn install:install-file -Dfile=%s -DgroupId=dev8 -DartifactId=shop-entity -Dversion=1.0.1 -Dpackaging=jar" %(jarFile.replace("/",os.sep).replace("\\",os.sep))


def err(message):
    print("%s 发生了错误！！！" %(message))
    sys.exit()

def replaceOsSep(src):
    return src.replace("/",os.sep).replace("\\",os.sep)

def setWorkPath():
    base_path =  os.path.abspath( os.path.join( __file__ ,"../" ) )
    os.chdir( base_path)

if __name__=='__main__':
    print("-------------------------------------------------------------------------------")
    print("")
    print("正在打包并安装实体jar ...")
    print("")
    print(os.linesep)

    setWorkPath();

    returnCode = os.system(cleanCMD)
    if(returnCode !=0 ):
        err("clean")

    print(os.linesep)

    returnCode = os.system(installCMD)
    if (returnCode != 0):
        err("install")

    # shutil.copy(jarFile,"../corp/lib/")
    # shutil.copy(jarFile,"../admin/lib/")
    # shutil.copy(jarFile,"../c/lib/")

    print("")
    print("安装完成！！！")
    print("-------------------------------------------------------------------------------")

