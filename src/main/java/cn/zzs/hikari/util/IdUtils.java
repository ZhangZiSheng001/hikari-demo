package cn.zzs.hikari.util;


import java.util.UUID;

/**
 * 生成id的工具类
 * @author zzs
 * @version 1.0.0
 * @date 2021/4/28
 */
public class IdUtils {


    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


}
