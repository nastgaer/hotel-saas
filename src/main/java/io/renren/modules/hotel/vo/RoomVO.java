package io.renren.modules.hotel.vo;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;

@Data
public class RoomVO {

	private Long id;

	private String name;

	private String amount;

	private String floor;
	/**
	 * 可住人数
	 */
	private Integer people;
	/**
	 * 加床
	 */
	private Integer bed;
	/**
	 * 早餐
	 */
	private Integer breakfast;
	/**
	 * 房间设施
	 */
	private String facilities;
	/**
	 * 窗户
	 */
	private Integer windows;
	/**
	 * 房型大图
	 */
	private String logo;
	/**
	 * 床型尺寸
	 */
	private String size;

	/**
	 * 最低价格
	 */
	private String price;
	
	private String oprice;

	/**
	 * 是否有房
	 */
	private boolean hasRoom;

	private int discounts;
	
	private List<String> tagList;

	/**
	 * 价格列表
	 */
	List<RoomMoneyVo> amountItems;

	private List<String> imgs = new ArrayList<String>();
}
