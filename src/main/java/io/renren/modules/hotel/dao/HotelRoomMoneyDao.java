package io.renren.modules.hotel.dao;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.modules.hotel.entity.HotelRoomMoneyEntity;

/**
 * 
 * 
 * @author taoz
 * @email 18819175397@163.com
 * @date 2019-03-21 22:44:37
 */
@Mapper
public interface HotelRoomMoneyDao extends BaseMapper<HotelRoomMoneyEntity> {

	/**
	 * 更新房量
	 * 
	 * @param hotelRoomMoneyEntity
	 * @param roomNum
	 */
	void updateRoomNum(HotelRoomMoneyEntity params, int roomNum);

}
