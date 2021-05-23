package io.renren.modules.hotel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author taoz
 * @email 18819175397@gmail.com
 * @date 2019-09-16 19:47:22
 */
@Data
@TableName("t_hotel_brand")
public class HotelBrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	private String name;
	
	@TableField(exist = false)
	private String typeName;
	/**
	 * 
	 */
	private Date creatTime;
	/**
	 * 
	 */
	private Long typeId;

}
