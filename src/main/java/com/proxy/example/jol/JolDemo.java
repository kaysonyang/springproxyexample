package com.proxy.example.jol;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;

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



        int[] nums = new int[]{1,2,3,4,4,5};






        try {

            byte[] buffer = new BASE64Decoder().decodeBuffer("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");
            FileOutputStream out = new FileOutputStream("/Users/yangxinchu/IdeaProjects/springproxyexample/src/main/java/com/proxy/example/jol/a.zip");
            out.write(buffer);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        removeDuplicates(nums);
        removeDuplicates2(nums);
    }



    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;
        int i = 0;
        for (int j = 1; j < nums.length; j++) {
            if (nums[j] != nums[i]) {
                i++;
                nums[i] = nums[j];

            }
        }
        int k = i + 1;
        System.out.println(k);
        System.out.println(nums);
        return k;

    }


    public static  int removeDuplicates2(int[] nums) {
        //数组中没有元素
        if(nums == null || nums.length == 0) return 0;

        //双指针中的慢指针
        int i = 0;

        //快指针遍历该数组
        for(int j = i; j < nums.length; j++){
            if(nums[i] != nums[j]){
                //当前两个元素不相等
                //当数组中的元素都不重复时，防止每次都进行复制操作
                if(j - i < 1){
                    nums[i] = nums[j];
                }
                i++;
                nums[i] = nums[j];
            }
        }
        //因为初始指向下标0，因此长度最后需要加1
        int k = i + 1;
        System.out.println(k);
        System.out.println(nums);
        return k;
    }


}
