package org.ice.jee.spring.mybatis.core.base;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ice.jee.spring.common.core.Constants;
import org.ice.jee.spring.common.core.util.DataUtil;
import org.ice.jee.spring.common.core.util.ExceptionUtil;
import org.ice.jee.spring.common.core.util.InstanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.BeanUtil.CopyOptions;
import com.xiaoleilu.hutool.util.ClassUtil;

/**
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:47:58
 */
public abstract class BaseService<M extends BaseMapper<T>, T extends BaseModel<T>> extends ServiceImpl<M, T> implements IBaseService<T>, ApplicationContextAware {
	 protected Logger logger = LogManager.getLogger(getClass());
	    @Autowired
	    protected M mapper;
	    protected ApplicationContext applicationContext;

	    public void setApplicationContext(ApplicationContext applicationContext) {
	        this.applicationContext = applicationContext;
	    }

	    /** 分页查询 */
	    public static Page<String> getPage(Map<String, Object> params) {
	        Integer current = 1;
	        Integer size = 10;
	        String orderBy = "id_";
	        if (DataUtil.isNotEmpty(params.get("pageNum"))) {
	            current = Integer.valueOf(params.get("pageNum").toString());
	        }
	        if (DataUtil.isNotEmpty(params.get("pageSize"))) {
	            size = Integer.valueOf(params.get("pageSize").toString());
	        }
	        if (DataUtil.isNotEmpty(params.get("orderBy"))) {
	            orderBy = (String)params.get("orderBy");
	            params.remove("orderBy");
	        }
	        if (size == -1) {
	            return new Page<String>();
	        }
	        Page<String> page = new Page<String>(current, size, orderBy);
	        page.setAsc(false);
	        return page;
	    }

	    /** 根据Id查询(默认类型T) */
	    public Page<T> getPage(Page<String> ids) {
	        if (ids != null) {
	            Page<T> page = new Page<T>(ids.getCurrent(), ids.getSize());
	            page.setTotal(ids.getTotal());
	            List<T> records = InstanceUtil.newArrayList();
	            for (String id : ids.getRecords()) {
	                records.add(this.queryById(id));
	            }
	            page.setRecords(records);
	            return page;
	        }
	        return new Page<T>();
	    }

	    /** 根据Id查询(默认类型T) */
	    public Page<Map<String, Object>> getPageMap(Page<String> ids) {
	        if (ids != null) {
	            Page<Map<String, Object>> page = new Page<Map<String, Object>>(ids.getCurrent(), ids.getSize());
	            page.setTotal(ids.getTotal());
	            List<Map<String, Object>> records = InstanceUtil.newArrayList();
	            for (String id : ids.getRecords()) {
	                records.add(InstanceUtil.transBean2Map(this.queryById(id)));
	            }
	            page.setRecords(records);
	            return page;
	        }
	        return new Page<Map<String, Object>>();
	    }

	    /** 根据Id查询(cls返回类型Class) */
	    public <K> Page<K> getPage(Page<String> ids, Class<K> cls) {
	        if (ids != null) {
	            Page<K> page = new Page<K>(ids.getCurrent(), ids.getSize());
	            page.setTotal(ids.getTotal());
	            List<K> records = InstanceUtil.newArrayList();
	            for (String id : ids.getRecords()) {
	                T t = this.queryById(id);
	                K k = InstanceUtil.to(t, cls);
	                records.add(k);
	            }
	            page.setRecords(records);
	            return page;
	        }
	        return new Page<K>();
	    }

	    /** 根据Id查询(默认类型T) */
	    public List<T> getList(List<String> ids) {
	        List<T> list = InstanceUtil.newArrayList();
	        if (ids != null) {
	            for (String id : ids) {
	                list.add(this.queryById(id));
	            }
	        }
	        return list;
	    }

	    /** 根据Id查询(cls返回类型Class) */
	    public <K> List<K> getList(List<String> ids, Class<K> cls) {
	        List<K> list = InstanceUtil.newArrayList();
	        if (ids != null) {
	            for (String id : ids) {
	                T t = this.queryById(id);
	                K k = InstanceUtil.to(t, cls);
	                list.add(k);
	            }
	        }
	        return list;
	    }

	    @Transactional
	    public void del(String id, String userId) {
	        try {
	            T record = this.queryById(id);
	            record.setEnable(0);
	            record.setUpdateTime(new Date());
	            record.setUpdateBy(userId);
	            mapper.updateById(record);
//	            CacheUtil.getCache().set(getCacheKey(id), record);
	        } catch (Exception e) {
	            logger.error(e.getMessage(), e);
	            throw new RuntimeException(e.getMessage(), e);
	        }
	    }

	    @Transactional
	    public void delete(String id) {
	        try {
	            mapper.deleteById(id);
//	            CacheUtil.getCache().del(getCacheKey(id));
	        } catch (Exception e) {
	            logger.error(e.getMessage(), e);
	            throw new RuntimeException(e.getMessage(), e);
	        }
	    }

	    @Transactional
	    public T update(T record) {
	        try {
	            record.setUpdateTime(new Date());
	            if (record.getId() == null) {
	                record.setCreateTime(new Date());
	                mapper.insert(record);
	            } else {
	                T org = queryById(record.getId());
	                String lockKey = getLockKey(record.getId());
	                if (StringUtils.isBlank(lockKey)) {
	                    T update = InstanceUtil.getDiff(org, record);
	                    update.setId(record.getId());
	                    mapper.updateById(update);
	                } else {
//	                    if (CacheUtil.getLock(lockKey)) {
//	                        try {
//	                            T update = InstanceUtil.getDiff(org, record);
//	                            update.setId(record.getId());
//	                            mapper.updateById(update);
//	                            record = mapper.selectById(record.getId());
//	                            CacheUtil.getCache().set(getCacheKey(record.getId()), record);
//	                        } finally {
//	                            CacheUtil.unlock(lockKey);
//	                        }
//	                    } else {
//	                        sleep(20);
//	                        return update(record);
//	                    }
	                }
	            }
	        } catch (DuplicateKeyException e) {
	            String msg = ExceptionUtil.getStackTraceAsString(e);
	            logger.error(Constants.Exception_Head + msg, e);
	            throw new RuntimeException("已经存在相同的配置.");
	        } catch (Exception e) {
	            String msg = ExceptionUtil.getStackTraceAsString(e);
	            logger.error(Constants.Exception_Head + msg, e);
	            throw new RuntimeException(msg);
	        }
	        return record;
	    }

	    @Transactional
	    @SuppressWarnings("unchecked")
	    public T queryById(String id) {
	        try {
	            String key = getCacheKey(id);
	            if (StringUtils.isBlank(key)) {
	                return mapper.selectById(id);
	            } else {
//	                T record = (T)CacheUtil.getCache().get(key);
//	                if (record == null) {
//	                    String lockKey = getLockKey(id);
//	                    if (CacheUtil.getLock(lockKey)) {
//	                        record = mapper.selectById(id);
//	                        CacheUtil.getCache().set(key, record);
//	                        CacheUtil.getCache().del(lockKey);
//	                    } else {
//	                        sleep(20);
//	                        return queryById(id);
//	                    }
//	                }
	                return null;
	            }
	        } catch (Exception e) {
	            logger.error(e.getMessage(), e);
	            throw new RuntimeException(e.getMessage(), e);
	        }
	    }

	    public Page<T> query(Map<String, Object> params) {
	        Page<String> page = getPage(params);
	        page.setRecords(mapper.selectIdPage(page, params));
	        return getPage(page);
	    }

	    public Page<Map<String, Object>> queryMap(Map<String, Object> params) {
	        Page<String> page = getPage(params);
	        page.setRecords(mapper.selectIdPage(page, params));
	        return getPageMap(page);
	    }

	    public List<T> queryList(Map<String, Object> params) {
	        List<String> ids = mapper.selectIdPage(params);
	        List<T> list = getList(ids);
	        return list;
	    }

	    protected <P> Page<P> query(Map<String, Object> params, Class<P> cls) {
	        Page<String> page = getPage(params);
	        page.setRecords(mapper.selectIdPage(page, params));
	        return getPage(page, cls);
	    }

	    protected void sleep(int millis) {
	        try {
	            Thread.sleep(RandomUtils.nextLong(10, millis));
	        } catch (InterruptedException e) {
	            logger.error("", e);
	        }
	    }

	    /** 获取缓存键值 */
	    protected String getCacheKey(Object id) {
	        String cacheName = getCacheKey();
	        if (StringUtils.isBlank(cacheName)) {
	            return null;
	        }
	        return new StringBuilder(Constants.CACHE_NAMESPACE).append(cacheName).append(":").append(id).toString();
	    }

	    /** 获取缓存键值 */
	    protected String getLockKey(Object id) {
	        String cacheName = getCacheKey();
	        if (StringUtils.isBlank(cacheName)) {
	            return null;
	        }
	        return new StringBuilder(Constants.CACHE_NAMESPACE).append(cacheName).append(":LOCK:").append(id).toString();
	    }

	    /**
	     * @return
	     */
	    private String getCacheKey() {
	        Class<?> cls = getClass();
	        String cacheName = Constants.cacheKeyMap.get(cls);
	        if (StringUtils.isBlank(cacheName)) {
	            CacheConfig cacheConfig = cls.getAnnotation(CacheConfig.class);
	            if (cacheConfig == null) {
	                return null;
	            } else if (cacheConfig.cacheNames() == null || cacheConfig.cacheNames().length < 1) {
	                cacheName = getClass().getName();
	            } else {
	                cacheName = cacheConfig.cacheNames()[0];
	            }
	            Constants.cacheKeyMap.put(cls, cacheName);
	        }
	        return cacheName;
	    }
	    
	    public Page<T> selectPageByWrapper(Page<T> page ,  Map<String, Object> paramMap , String[] ignoreProperties, Class<T> clazz) throws Exception {
	    	CopyOptions copyOptions = new CopyOptions();
	    	copyOptions.setIgnoreProperties(ignoreProperties);
	        T t = BeanUtil.fillBeanWithMap(paramMap, ClassUtil.newInstance(clazz), copyOptions);
	        return this.selectPageByWrapper(page, t, clazz);
	    }
	    
	    public Page<T> selectPageByWrapper(Page<T> page ,  Map<String, Object> paramMap , Class<T> clazz) throws Exception {	
	        T t = BeanUtil.mapToBeanIgnoreCase(paramMap, clazz, true);
	        return this.selectPageByWrapper(page, t, clazz);
	    }
	    
	    public Page<T> selectPageByWrapper(Page<T> page ,  T t , Class<T> clazz) throws Exception {
	    	EntityWrapper<T> TEntityWrapper = new EntityWrapper<>();
	        TEntityWrapper.setEntity(t);
	        page.setRecords(mapper.selectPage(page,TEntityWrapper));
//	        page.setTotal(mapper.selectCount(TEntityWrapper));
	        return page;
	    }
	    
	    public int updateByWrapper(T t ,  Map<String, Object> paramMap , Class<T> clazz) throws Exception {
	    	EntityWrapper<T> TEntityWrapper = new EntityWrapper<>();
	        T cond = BeanUtil.mapToBeanIgnoreCase(paramMap, clazz, true);
	        TEntityWrapper.setEntity(cond);
	        return mapper.update(t, TEntityWrapper);
	    }
}
