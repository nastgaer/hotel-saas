package io.renren.modules.hotel.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.hotel.entity.HotelCouponsBreakfastEntity;
import io.renren.modules.hotel.entity.HotelCouponsEntity;
import io.renren.modules.hotel.entity.HotelOrderEntity;
import io.renren.modules.hotel.entity.HotelOrderRecordEntity;
import io.renren.modules.hotel.entity.HotelOrderSettingEntity;
import io.renren.modules.hotel.service.HotelCouponsBreakfastService;
import io.renren.modules.hotel.service.HotelCouponsService;
import io.renren.modules.hotel.service.HotelOrderRecordService;
import io.renren.modules.hotel.service.HotelOrderService;
import io.renren.modules.sys.controller.AbstractController;

/**
 * 
 *
 * @author taoz
 * @email 18819175397@163.com
 * @date 2019-03-20 12:49:34
 */
@RestController
@RequestMapping("hotel/hotelorder")
public class HotelOrderController extends AbstractController {
	@Autowired
	private HotelOrderService hotelOrderService;

	@Autowired
	private HotelOrderRecordService hotelOrderRecordService;
	
	@Autowired
	private HotelCouponsBreakfastService hotelCouponsBreakfastService;
	
	@Autowired
	private HotelCouponsService hotelCouponsService;

	/**
	 * 订单确认
	 */
	@RequiresPermissions("hotel:hotelorder:orderaffirm")
	@PostMapping("/orderAffirm/{id}")
	public R orderAffirm(@PathVariable Long id) {
		hotelOrderService.orderAffirm(id, getSellerId());
		return R.ok();
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("hotel:hotelorder:list")
	public R list(@RequestParam Map<String, Object> params) {
		if (!isAdmin()) {
			params.put("seller_id", getSellerId());
		}
		PageUtils page = hotelOrderService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("hotel:hotelorder:info")
	public R info(@PathVariable("id") Integer id) {
		HotelOrderEntity hotelOrder = hotelOrderService.getById(id);
		List<HotelOrderRecordEntity> records = hotelOrderRecordService.list(Wrappers.<HotelOrderRecordEntity>lambdaQuery().eq(HotelOrderRecordEntity::getOrderId, id));
		hotelOrder.setRecords(records);
		if(null != hotelOrder.getBreakCouponId()) {
			HotelCouponsBreakfastEntity couponsBreakfastEntity =  hotelCouponsBreakfastService.getById(hotelOrder.getBreakCouponId());
			hotelOrder.setBreakCouponName(couponsBreakfastEntity.getName());
		}
		if(null !=hotelOrder.getFreeRoomCouponId()) {
			HotelCouponsEntity couponsEntity = hotelCouponsService.getById(hotelOrder.getFreeRoomCouponId());
			hotelOrder.setFreeRoomCouponName(couponsEntity.getName());
		}
		return R.ok().put("hotelOrder", hotelOrder);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("hotel:hotelorder:save")
	public R save(@RequestBody HotelOrderEntity hotelOrder) {
		hotelOrderService.save(hotelOrder);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("hotel:hotelorder:update")
	public R update(@RequestBody HotelOrderEntity hotelOrder) {
		hotelOrderService.updateById(hotelOrder);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("hotel:hotelorder:delete")
	public R delete(@RequestBody Integer[] ids) {
		hotelOrderService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

	/**
	 * 订单入住
	 * 
	 * @param orderId
	 * @return
	 */
	@PostMapping("/orderCheckIn/{orderId}")
	@RequiresPermissions("hotel:hotelorder:ordercheckin")
	public R orderCheckIn(@PathVariable(required = true, name = "orderId") Long orderId) {
		hotelOrderService.orderCheckIn(orderId);
		return R.ok();
	}

}
