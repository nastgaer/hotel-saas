package io.renren.modules.hotel.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 会员卡详情
 * 
 * @author taoz
 * @email 18819175397@gmail.com
 * @date 2019-08-17 19:46:29
 */
@Data
@TableName("t_hotel_member_level_detail")
public class HotelMemberLevelDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 等级类型
	 */
	private Long levelId;
	/**
	 * 会员ID
	 */
	private Long memberId;
	/**
	 * 证件类型
	 */
	private Integer certificate;
	/**
	 * 证件号
	 */
	private String certificateNo;
	/**
	 * 名字
	 */
	private String name;
	/**
	 * 手机
	 */
	private String mobile;
	/**
	 * 性别
	 */
	private Integer gender;
	/**
	 * 卡号
	 */
	private String cardNo;
	/**
	 * 联系地址
	 */
	private String address;
	/**
	 * 创建者
	 */
	private Long creator;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 修改时间
	 */
	private Date updateDate;
	/**
	 * 商家ID
	 */
	private Long sellerId;

	/**
	 * 状态，当为1的时候才有效
	 */
	private int status;

	/**
	 * 推销员
	 */
	private String salesman;

	/**
	 * 积分
	 */
	private BigDecimal score;

	/**
	 * 余额
	 */
	private BigDecimal balance;

	/**
	 * 卡片二维码
	 */
	private String qrCode;

}
