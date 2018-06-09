package org.ice.jee.spring.mybatis.core.base;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

public interface IBaseService<T> extends IService<T> {

//	 /** 分页查询 */
//    public Page<Long> getPage(Map<String, Object> params) ;

    /** 根据Id查询(默认类型T) */
    public Page<?> getPage(Page<String> ids) ;

    /** 根据Id查询(默认类型T) */
    public Page<Map<String, Object>> getPageMap(Page<String> ids) ;

    /** 根据Id查询(cls返回类型Class) */
    public <K> Page<K> getPage(Page<String> ids, Class<K> cls) ;

    /** 根据Id查询(默认类型T) */
    public List<?> getList(List<String> ids) ;

    /** 根据Id查询(cls返回类型Class) */
    public <K> List<K> getList(List<String> ids, Class<K> cls) ;

    public void del(String id, String userId) ;

    public void delete(String id) ;

    public T update(T record) ;

    public T queryById(String id) ;

    public Page<?> query(Map<String, Object> params) ;

    public Page<Map<String, Object>> queryMap(Map<String, Object> params) ;

    public List<?> queryList(Map<String, Object> params) ;

    public Page<T> selectPageByWrapper(Page<T> page ,  Map<String, Object> paramMap , Class<T> clazz) throws Exception;
    
    public int updateByWrapper(T t ,  Map<String, Object> paramMap , Class<T> clazz) throws Exception;
}
