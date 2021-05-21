package cn.zzs.hikari.util;

import java.util.Collection;


/**
 * 集合操作 工具类
 * @author zzs
 * @date 2020年6月9日 下午4:05:40
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

}
