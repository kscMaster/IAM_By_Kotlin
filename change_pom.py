#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
import sys
import shutil
import  xml.dom.minidom
import  getopt


def err(message):
    print("%s 发生了错误！！！" %(message))
    sys.exit(1)

def replaceOsSep(src):
    return src.replace("/",os.sep).replace("\\",os.sep)

def setWorkPath():
    base_path =  os.path.abspath( os.path.join( __file__ ,"../" ) )
    os.chdir( base_path)

def printHelp():
    print('''
替换 xml 文件中的值。
python change_pom.py -f 根pom.xml所在路径（默认当前） -g 新的值
''')

def getArgs():
    groupId = ""
    file = ""

    try:
        opts, args = getopt.getopt(sys.argv[1:], "g:f:h", ["groupId=","file="])
    except getopt.GetoptError:
        printHelp()
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            printHelp()
            sys.exit()
        elif opt in ("-f", "--file"):
            file = arg
        elif opt in ("-g", "--groupId"):
            groupId = arg


    return file,groupId

def getModulesData():

    dom = xml.dom.minidom.parse('pom.xml')
    root = dom.documentElement
    groupId = root.getElementsByTagName('groupId')[0].childNodes[0].data;
    artifactId = root.getElementsByTagName('artifactId')[0].childNodes[0].data;


    mns = filter(lambda x: x.nodeType == 1, root.getElementsByTagName('modules')[0].childNodes);
    modules = list( map(lambda x: x.childNodes[0].data ,mns) )


    return groupId,artifactId,modules

def resetGroupId(newGroupId):

    dom = xml.dom.minidom.parse('pom.xml')
    root = dom.documentElement

    groupIdDom = root.getElementsByTagName('groupId')[0].childNodes[0];


    if  newGroupId:
        groupIdDom.data = newGroupId


    with open('pom.xml','w',encoding='UTF-8') as fh:
        fh.write(dom.toxml())
        fh.flush()


    print("重新设置了根 pom.xml")

def resetSubGroupId(module,fileName,groupId,artifactId,newGroupId,modules):
    if module:
        os.chdir(module);

    dom = xml.dom.minidom.parse(fileName)
    root = dom.documentElement

    groupIdDom = root.getElementsByTagName('groupId')[0].childNodes[0];


    mns = list(filter(lambda x: x.nodeType == 1, root.getElementsByTagName('parent')[0].childNodes));
    t_groupIdDom= list(filter( lambda x: x.tagName == "groupId",mns ))[0].childNodes[0]


    if t_groupIdDom.data == groupId and newGroupId:
        t_groupIdDom.data = newGroupId
        groupIdDom.data = newGroupId


    mns2 = list(filter(lambda x: x.nodeType == 1, root.getElementsByTagName('dependencies')[0].childNodes));

    for dependency in mns2:
        for module2 in modules:
            t2_groupIdDom =  list(filter( lambda x: x.tagName == "groupId", filter(lambda x:x.nodeType == 1, dependency.childNodes ) ))[0].childNodes[0]
            t2_artifactIdDom =  list(filter( lambda x: x.tagName == "artifactId", filter(lambda x:x.nodeType == 1, dependency.childNodes ) ))[0].childNodes[0]

            if  t2_groupIdDom.data == groupId and \
                    t2_artifactIdDom.data == module2 and\
                    newGroupId:
                t2_groupIdDom.data = newGroupId



    with open(fileName,'w',encoding='UTF-8') as fh:
        fh.write(dom.toxml())
        fh.flush()


    print("重新设置了: %s"%(module))

if __name__=='__main__':

    file,newGroupId = getArgs()

    setWorkPath();

    if file:
        os.chdir(file)

    groupId,artifactId,modules = getModulesData()

    print("-------------------------------------------------------------------------------")
    print(os.linesep)

    resetGroupId(newGroupId)
    resetSubGroupId("","pom.xml",groupId,artifactId, newGroupId,modules)
    resetSubGroupId("","pom_component.xml",groupId,artifactId, newGroupId,modules)
    resetSubGroupId("","pom_starter.xml",groupId,artifactId, newGroupId,modules)

    for module in modules:
        resetSubGroupId(module,"pom.xml",groupId,artifactId, newGroupId,modules)
        os.chdir("../")


    print("")
    print("设置 %s %s 完成！"%(newGroupId,artifactId))
    print("-------------------------------------------------------------------------------")

