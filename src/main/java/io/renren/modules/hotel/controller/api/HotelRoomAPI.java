package io.renren.modules.hotel.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.renren.common.utils.R;
import io.renren.modules.app.annotation.Login;
import io.renren.modules.hotel.service.HotelRoomService;
import io.renren.modules.hotel.vo.RoomVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author taoz
 *
 */
@Api(value = "酒店房间接口", tags = { "酒店房间接口" })
@RestController
@RequestMapping("/hotel/room")
public class HotelRoomAPI extends BaseController {

	@Autowired
	private HotelRoomService hotelRoomService;

	/**
	 * 酒店房型列表
	 * 
	 * @param appId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Login
	@ApiOperation("查询房型列表")
	@GetMapping("/roomList")
	public R roomList(@RequestAttribute("userId") Long userId, Long sellerId, String checkInTime, String checkOutTime, @RequestParam(required = false, name = "roomType", defaultValue = "1") int roomType) {
		if (StrUtil.isEmpty(checkInTime)) {
			checkInTime = DateUtil.format(DateUtil.date(), "yyyy-MM-dd");
		}
		if (StrUtil.isEmpty(checkOutTime)) {
			checkOutTime = DateUtil.format(DateUtil.offsetDay(DateUtil.date(), 1), "yyyy-MM-dd");
		}
		List<RoomVO> roomVOs = hotelRoomService.roomList(userId, sellerId, checkInTime, checkOutTime, roomType);
		return R.ok().put("data", roomVOs);
	}

}
