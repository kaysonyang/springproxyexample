package com.proxy.example.jol;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

/**
 * @Author: Kayson Yang
 * @Date: 2020/4/1 1:59 PM
 * @Desc:
 */
public class JolDemo {



    public static void main(String[] args) {

        Object obj = new Object();
        //查看对象内部信息
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());;

        //查看对象外部信息
        System.out.println(GraphLayout.parseInstance(obj).toPrintable());

        //获取对象总大小
        System.out.println("size : " + GraphLayout.parseInstance(obj).totalSize());
    }
}
