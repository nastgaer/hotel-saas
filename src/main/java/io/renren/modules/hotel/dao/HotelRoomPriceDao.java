package io.renren.modules.hotel.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.modules.hotel.entity.HotelRoomPriceEntity;

/**
 * 
 * 
 * @author taoz
 * @email 18819175397@163.com
 * @date 2019-03-21 22:44:37
 */
@Mapper
public interface HotelRoomPriceDao extends BaseMapper<HotelRoomPriceEntity> {

	/**
	 * 分页
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<String> getDays(String startDate, String endDate);

}
