package io.renren.modules.hotel.controller.api;

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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.utils.R;
import io.renren.modules.app.annotation.Login;
import io.renren.modules.hotel.entity.AssessTagEntity;
import io.renren.modules.hotel.form.CommentForm;
import io.renren.modules.hotel.service.AssessTagService;
import io.renren.modules.hotel.service.HotelAssessService;
import io.renren.modules.hotel.vo.CommentItemVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 评论API
 * 
 * @author taoz
 *
 */

@Api(value = "评论", tags = { "评论接口" })
@RestController
@RequestMapping("/hotel/comment")
public class HotelCommentAPI {

	@Autowired
	private HotelAssessService hotelAssessService;

	@Autowired
	private AssessTagService assessTagService;

	@ApiOperation("酒店评论Tag列表")
	@GetMapping("/commentTags")
	public R commentTags() {
		List<AssessTagEntity> assessTagEntities = assessTagService.list();
		return R.ok(assessTagEntities);
	}

	/**
	 * 酒店评论列表
	 * 
	 * @return
	 */
	@ApiOperation("酒店评论列表")
	@GetMapping("/hotelList")
	public R hotelList(@RequestParam(name = "page", required = false, defaultValue = "1") int page, @RequestParam(name = "limit", required = false, defaultValue = "10") int limit, Long sellerId, String type) {
		Page<CommentItemVo> pageResult = hotelAssessService.hotelCommnetList(new Page<CommentItemVo>(page, limit), sellerId, type);
		return R.ok(pageResult);
	}

	/**
	 * 添加评论
	 * 
	 * @return
	 */
	@ApiOperation("添加评论")
	@Login
	@PostMapping
	public R addComment(@RequestAttribute("userId") Long userId, @RequestBody CommentForm commentForm) {
		hotelAssessService.addAssess(userId, commentForm);
		return R.ok();
	}

	/**
	 * 商品评论
	 * 
	 * @return
	 */
	@ApiOperation("商品评论列表")
	@GetMapping("/goodsList")
	public R goodsList(@RequestParam(name = "page", required = false, defaultValue = "1") int page, @RequestParam(name = "limie", required = false, defaultValue = "10") int limie, Long goodsId) {
		Page<CommentItemVo> pageResult = hotelAssessService.goodsCommnetList(new Page<CommentItemVo>(page, limie), goodsId);
		return R.ok(pageResult);
	}

	@ApiOperation("酒店评论总数")
	@GetMapping("/hotelCount")
	public R hotelCount(@RequestParam Long sellerId) {
		Map<String, Object> result = hotelAssessService.hotelCount(sellerId);
		return R.ok().put("data", result);
	}

	@ApiOperation("酒店评论tags")
	@GetMapping("/hotelTags")
	public R hotelTags(@RequestParam Long sellerId) {
		List<AssessTagEntity> assessTagEntities = assessTagService.hotelTags(sellerId);
		return R.ok(assessTagEntities);
	}
}
