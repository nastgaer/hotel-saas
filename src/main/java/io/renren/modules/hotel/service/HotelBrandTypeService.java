package io.renren.modules.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.hotel.entity.HotelBrandTypeEntity;

import java.util.Map;

/**
 * 
 *
 * @author taoz
 * @email 18819175397@gmail.com
 * @date 2019-09-16 19:47:22
 */
public interface HotelBrandTypeService extends IService<HotelBrandTypeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

