package io.renren.modules.hotel.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.hotel.dao.HotelCouponsBreakfastDao;
import io.renren.modules.hotel.entity.HotelCouponsBreakfastEntity;
import io.renren.modules.hotel.service.HotelCouponsBreakfastService;

@Service("hotelCouponsBreakfastService")
public class HotelCouponsBreakfastServiceImpl extends ServiceImpl<HotelCouponsBreakfastDao, HotelCouponsBreakfastEntity> implements HotelCouponsBreakfastService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Object sellerId = params.get("seller_id");
		IPage<HotelCouponsBreakfastEntity> page = this.page(new Query<HotelCouponsBreakfastEntity>().getPage(params), new QueryWrapper<HotelCouponsBreakfastEntity>().eq(sellerId != null, "seller_id", sellerId));

		return new PageUtils(page);
	}

}