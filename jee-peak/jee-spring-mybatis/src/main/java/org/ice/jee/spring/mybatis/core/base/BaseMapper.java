package org.ice.jee.spring.mybatis.core.base;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**

 * @author Jaden

 * @version 2016年6月3日 下午2:30:14

 */
public interface BaseMapper<T extends BaseModel<T>> extends com.baomidou.mybatisplus.mapper.BaseMapper<T> {
	
	List<String> selectIdPage(@Param("cm") Map<String, Object> params);

	List<String> selectIdPage(RowBounds rowBounds, @Param("cm") Map<String, Object> params);

}
