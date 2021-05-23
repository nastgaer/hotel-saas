package io.renren.modules.hotel.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;

import io.renren.common.utils.R;
import io.renren.modules.app.annotation.Login;
import io.renren.modules.hotel.form.BecomeVipForm;
import io.renren.modules.hotel.form.BindVipCardForm;
import io.renren.modules.hotel.service.HotelMemberLevelService;
import io.renren.modules.hotel.vo.VipCardItemVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 酒店会员
 * 
 * @author taoz
 *
 */

@Api(value = "酒店会员卡接口", tags = { "酒店会员卡接口" })
@RestController
@RequestMapping("/hotel/vip")
public class HotelMemberLevelAPI extends BaseController {

	@Autowired
	private HotelMemberLevelService hotelMemberLevelService;

	/**
	 * 绑定会员卡
	 * 
	 * @param appId
	 * @param userId
	 * @param vipCardForm
	 * @return
	 */
	@Login
	@ApiOperation("绑定会员卡")
	@PostMapping("/bindCard")
	public R bindCard(@RequestAttribute("userId") Long userId, @RequestBody BindVipCardForm vipCardForm) {
		hotelMemberLevelService.bindCard(userId, vipCardForm);
		return R.ok();
	}

	/**
	 * 注册会员
	 * 
	 * @param userId
	 * @param becomeVipForm
	 * @return
	 */
	@Login
	@ApiOperation("注册会员卡")
	@PostMapping("/becomeVip")
	public R becomeVip(@RequestAttribute("userId") Long userId, @RequestBody BecomeVipForm becomeVipForm) {
		WxPayMpOrderResult mpOrderResult = hotelMemberLevelService.becomeVip(userId, becomeVipForm);
		return R.ok(mpOrderResult);
	}

	/**
	 * 检查会员状态
	 * 
	 * @param userId
	 * @param sellerId
	 * @return
	 */
	@Login
	@ApiOperation("检查会员状态")
	@GetMapping("/check")
	public R check(@RequestAttribute("userId") Long userId, @RequestParam(required = true) Long sellerId) {
		return R.ok().put("data", hotelMemberLevelService.checkVipStatus(userId, sellerId));
	}

	/**
	 * 会员卡信息
	 * 
	 * @param userId
	 * @param sellerId
	 * @return
	 */
	@Login
	@ApiOperation("用户会员卡信息")
	@GetMapping("/info")
	public R info(@RequestAttribute("userId") Long userId, @RequestParam(required = true) Long sellerId) {
		VipCardItemVo cardItemVo = hotelMemberLevelService.vipCardInfo(userId, sellerId);
		return R.ok(cardItemVo);
	}

	/**
	 * 商家会员卡列表
	 * 
	 * @param userId
	 * @param sellerId
	 * @return
	 */
	@Login
	@ApiOperation("商家会员卡列表")
	@GetMapping("/list")
	public R list(@RequestAttribute("userId") Long userId, @RequestParam(required = true) Long sellerId) {
		List<VipCardItemVo> cardItemVos = hotelMemberLevelService.vipCardList(userId, sellerId);
		BecomeVipForm becomeVipForm = hotelMemberLevelService.getSellerCardInfo(userId, sellerId);
		Map<String, Object> date = new HashMap<String, Object>();
		date.put("cards", cardItemVos);
		date.put("userCardInfo", becomeVipForm);
		return R.ok().put("data", date);
	}

	/**
	 * 用户会员列表
	 * 
	 * @param userId
	 * @return
	 */
	@Login
	@ApiOperation("用户会员列表")
	@GetMapping("/userCardlist")
	public R userCardlist(@RequestAttribute("userId") Long userId) {
		List<VipCardItemVo> cardItemVos = hotelMemberLevelService.userCardlist(userId);
		return R.ok(cardItemVos);
	}

	@Login
	@ApiOperation("卡片积分规则")
	@GetMapping("/cardRule")
	public R cardRule(Long cardId) {
		String rule = hotelMemberLevelService.cardRule(cardId);
		return R.ok().put("data", rule);
	}
}
