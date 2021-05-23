package io.renren.modules.hotel.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.modules.hotel.entity.HotelCouponsBreakfastEntity;
import io.renren.modules.hotel.vo.UserCoupons;

/**
 * 早餐券
 * 
 * @author taoz
 * @email 18819175397@gmail.com
 * @date 2019-08-21 22:22:25
 */
@Mapper
public interface HotelCouponsBreakfastDao extends BaseMapper<HotelCouponsBreakfastEntity> {

	/**
	 * 用户早餐券
	 * 
	 * @param page
	 * @param userId
	 * @return
	 */
	Page<UserCoupons> userBreakfastCoupons(Page<UserCoupons> page, int status, Long userId);

	/**
	 * 用户可用早餐券
	 * 
	 * @param userId
	 * @param sellerId
	 * @return
	 */
	List<UserCoupons> sellerCanUseBreakCoupons(Long userId, Long sellerId);

}
