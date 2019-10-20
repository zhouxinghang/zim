package team.zhou.zim.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhouxinghang
 * @date 2019-09-28
 */
public class DesensitizeUtil {
    public DesensitizeUtil() {
    }

    public static String desensitize(String context) {
        if (!StringUtils.isBlank(context) && context.length() >= 6) {
            String[] array = context.split(",");
            StringBuilder stringBuilder = new StringBuilder();

            for(int i = 0; i < array.length; ++i) {
                if (array[i].length() > 5) {
                    array[i] = array[i].substring(0, array[i].length() / 3) + getStars(array[i].length() * 2 / 3 - array[i].length() / 3) + array[i].substring(array[i].length() * 2 / 3);
                }

                stringBuilder.append(array[i]).append(",");
            }

            return stringBuilder.substring(0, stringBuilder.length() - 1);
        } else {
            return context;
        }
    }

    private static String getStars(int count) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < count; ++i) {
            stringBuilder.append("*");
        }

        return stringBuilder.toString();
    }

    public static String desensitizeJson(String context, Set<String> set) {
        JSONObject jsonObject = null;

        try {
            jsonObject = JSONObject.parseObject(context);
        } catch (Exception var4) {
        }

        if (jsonObject == null) {
            return context;
        } else {
            doDesensitizeJson(set, jsonObject);
            return jsonObject.toJSONString();
        }
    }

    private static void doDesensitizeJson(Set<String> set, JSONObject jsonObject) {
        Iterator var2 = jsonObject.entrySet().iterator();

        while(true) {
            while(var2.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)var2.next();
                if (entry.getValue() instanceof JSONObject) {
                    doDesensitizeJson(set, (JSONObject)entry.getValue());
                } else if (!set.contains(entry.getKey())) {
                    if (entry.getValue() instanceof Iterable) {
                        Iterable it = (Iterable)entry.getValue();
                        Iterator var9 = it.iterator();

                        while(var9.hasNext()) {
                            Object object = var9.next();
                            if (object instanceof JSONObject) {
                                doDesensitizeJson(set, (JSONObject)object);
                            }
                        }
                    }
                } else if (!(entry.getValue() instanceof Iterable)) {
                    entry.setValue(desensitize((String)entry.getValue()));
                } else {
                    List<String> list = new ArrayList();
                    Iterable it = (Iterable)entry.getValue();
                    Iterator var6 = it.iterator();

                    while(var6.hasNext()) {
                        Object object = var6.next();
                        list.add(desensitize(object.toString()));
                    }

                    entry.setValue(list);
                }
            }

            return;
        }
    }

    public static String desensitizeObject(Object object, final Set<String> set) {
        return JSON.toJSONString(object, new SerializeFilter[]{new ValueFilter() {
            @Override
            public Object process(Object object, String name, Object value) {
                if (value == null) {
                    return null;
                } else {
                    return set.contains(name) ? DesensitizeUtil.desensitize(value.toString()) : value;
                }
            }
        }, new PropertyFilter() {
            @Override
            public boolean apply(Object object, String name, Object value) {
                return !name.startsWith("set") && !name.endsWith("Iterator");
            }
        }}, new SerializerFeature[0]);
    }
}
