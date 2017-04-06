package com.byl.forcast;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * map按照value值排序方法
* @Description: TODO(这里用一句话描述这个方法的作用) 
* @author banna
* @date 2017年4月6日 上午11:08:32
 */
public class Maputil  
{  
    public static <K, V extends Comparable<? super V>> Map<K, V>   
        sortByValue( Map<K, V> map )  
    {  
        List<Map.Entry<K, V>> list =  
            new LinkedList<Map.Entry<K, V>>( map.entrySet() );  
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()  
        {  
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )  
            {  
                return (o2.getValue()).compareTo( o1.getValue() );  
            }  
        } );  
  
        Map<K, V> result = new LinkedHashMap<K, V>();  
        for (Map.Entry<K, V> entry : list)  
        {  
            result.put( entry.getKey(), entry.getValue() );  
        }  
        return result;  
    }  
} 
