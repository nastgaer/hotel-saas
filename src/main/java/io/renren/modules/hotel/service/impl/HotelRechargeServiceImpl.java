package io.renren.modules.hotel.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import io.renren.common.exception.RRException;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.constants.OrderTypeConstants;
import io.renren.modules.hotel.config.WxMaConfiguration;
import io.renren.modules.hotel.config.WxPayConfiguration;
import io.renren.modules.hotel.dao.HotelConsumptionRecordDao;
import io.renren.modules.hotel.dao.HotelMemberDao;
import io.renren.modules.hotel.dao.HotelMemberLevelDao;
import io.renren.modules.hotel.dao.HotelMemberLevelDetailDao;
import io.renren.modules.hotel.dao.HotelRechargeConfigDao;
import io.renren.modules.hotel.dao.HotelRechargeDao;
import io.renren.modules.hotel.dao.HotelSellerDao;
import io.renren.modules.hotel.dto.HotelRechargeDto;
import io.renren.modules.hotel.entity.HotelConsumptionRecordEntity;
import io.renren.modules.hotel.entity.HotelMemberEntity;
import io.renren.modules.hotel.entity.HotelMemberLevelDetailEntity;
import io.renren.modules.hotel.entity.HotelMemberLevelEntity;
import io.renren.modules.hotel.entity.HotelRechargeConfigEntity;
import io.renren.modules.hotel.entity.HotelRechargeEntity;
import io.renren.modules.hotel.entity.HotelSellerEntity;
import io.renren.modules.hotel.entity.HotelWxConfigEntity;
import io.renren.modules.hotel.form.CardRechargeForm;
import io.renren.modules.hotel.service.HotelRechargeService;
import io.renren.modules.hotel.service.HotelWxConfigService;
import io.renren.modules.hotel.vo.CardConsumptionVo;
import io.renren.modules.wx.OrderType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("hotelRechargeService")
public class HotelRechargeServiceImpl extends ServiceImpl<HotelRechargeDao, HotelRechargeEntity> implements HotelRechargeService {

	@Autowired
	private HotelRechargeConfigDao hotelRechargeConfigDao;

	@Autowired
	private HotelMemberLevelDao hotelMemberLevelDao;

	@Autowired
	private HotelConsumptionRecordDao hotelConsumptionRecordDao;

	@Autowired
	private HotelMemberLevelDetailDao hotelMemberLevelDetailDao;

	@Autowired
	private HotelSellerDao hotelSellerDao;

	@Autowired
	private HotelMemberDao hotelMemberDao;

	@Autowired
	private HotelWxConfigService hotelWxConfigService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Object sellerId = params.get("seller_id");
		Object outTradeNo = params.get("Object");
		IPage<HotelRechargeEntity> page = this.page(new Query<HotelRechargeEntity>().getPage(params), new QueryWrapper<HotelRechargeEntity>().eq(sellerId != null, "seller_id", sellerId).eq(outTradeNo != null, "out_trade_no", outTradeNo));
		List<HotelRechargeEntity> rechargeEntities = page.getRecords();
		List<HotelRechargeDto> hotelRechargeDtos = rechargeEntities.stream().map((HotelRechargeEntity item) -> {
			HotelRechargeDto hotelRechargeDto = new HotelRechargeDto();
			BeanUtil.copyProperties(item, hotelRechargeDto);
			HotelMemberLevelEntity memberLevelEntity = hotelMemberLevelDao.selectById(item.getCardId());
			hotelRechargeDto.setSellerName(hotelSellerDao.selectById(memberLevelEntity.getSellerId()).getName());
			hotelRechargeDto.setMemberName(hotelMemberDao.selectById(item.getUserId()).getName());
			return hotelRechargeDto;
		}).collect(Collectors.toList());
		IPage<HotelRechargeDto> resultPage = new Page<HotelRechargeDto>();
		resultPage.setCurrent(page.getCurrent());
		resultPage.setPages(page.getPages());
		resultPage.setRecords(hotelRechargeDtos);
		resultPage.setSize(page.getSize());
		resultPage.setTotal(page.getTotal());
		return new PageUtils(resultPage);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@SneakyThrows
	public WxPayMpOrderResult cardRecharge(Long userId, CardRechargeForm cardRechargeForm) {
		BigDecimal amount = new BigDecimal(0);
		HotelRechargeEntity hotelRechargeEntity = new HotelRechargeEntity();
		HotelMemberLevelEntity hotelMemberLevelEntity = hotelMemberLevelDao.selectById(cardRechargeForm.getCardId());
		hotelRechargeEntity.setZsMoney(new BigDecimal(0));
		if (null == cardRechargeForm.getRechargeConfigId() || -1 == cardRechargeForm.getRechargeConfigId()) {
			if (null == cardRechargeForm.getAmount() || cardRechargeForm.getAmount() == BigDecimal.ZERO) {
				throw new RRException("充值金额不能为空");
			}
			amount = cardRechargeForm.getAmount();
		} else {
			HotelRechargeConfigEntity rechargeConfigEntity = hotelRechargeConfigDao.selectById(cardRechargeForm.getRechargeConfigId());
			amount = rechargeConfigEntity.getMoney();
			hotelRechargeEntity.setZsMoney(rechargeConfigEntity.getReturnMoney());
		}
		hotelRechargeEntity.setCzMoney(amount);
		hotelRechargeEntity.setUserId(userId);
		hotelRechargeEntity.setCardId(cardRechargeForm.getCardId());
		hotelRechargeEntity.setSellerId(hotelMemberLevelEntity.getSellerId());
		hotelRechargeEntity.setTime(DateTime.now().getTime());
		hotelRechargeEntity.setState(0);
		hotelRechargeEntity.setOutTradeNo(DateUtil.format(DateUtil.date(), "yyyyMMddHHmmssSSS"));
		baseMapper.insert(hotelRechargeEntity);
		// 返回微信支付参数
		HotelMemberEntity hotelMemberEntity = hotelMemberDao.selectById(userId);
		HotelSellerEntity hotelSellerEntity = hotelSellerDao.selectById(hotelMemberLevelEntity.getSellerId());
		HotelWxConfigEntity hotelWxConfigEntity = hotelWxConfigService.getOne(new QueryWrapper<HotelWxConfigEntity>());
		WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
		wxPayUnifiedOrderRequest.setOpenid(hotelMemberEntity.getOpenid());
		wxPayUnifiedOrderRequest.setBody(hotelSellerEntity.getName() + "(在线充值)");
		wxPayUnifiedOrderRequest.setOutTradeNo(hotelRechargeEntity.getOutTradeNo());
		wxPayUnifiedOrderRequest.setSceneInfo(hotelSellerEntity.getAddress());
		wxPayUnifiedOrderRequest.setNotifyUrl("https://hotelapi.xqtinfo.cn/pay/" + hotelWxConfigEntity.getAppId() + "/notify/order");
		wxPayUnifiedOrderRequest.setTradeType("JSAPI");
		wxPayUnifiedOrderRequest.setAttach(JSON.toJSONString(new OrderType(OrderTypeConstants.order_recharge, cardRechargeForm.getFormId())));
		wxPayUnifiedOrderRequest.setTotalFee(1);
		wxPayUnifiedOrderRequest.setSpbillCreateIp(cardRechargeForm.getIp());
		WxPayMpOrderResult mpOrderResult = WxPayConfiguration.getPayServices().get(hotelWxConfigEntity.getAppId()).createOrder(wxPayUnifiedOrderRequest);
		log.info("调用微信统一下单--start,result:{}", JSON.toJSONString(mpOrderResult));
		return mpOrderResult;

	}

	@Override
	public Page<CardConsumptionVo> consumptionRecord(Page<CardConsumptionVo> page, Long userId, Long cardId) {
		return baseMapper.consumptionRecord(page, userId, cardId);
	}

	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public void cardRechargeHandler(String outTradeNo, String formId) {
		HotelRechargeEntity hotelRechargeEntity = baseMapper.selectOne(Wrappers.<HotelRechargeEntity>lambdaQuery().eq(HotelRechargeEntity::getOutTradeNo, outTradeNo));
		if (null != hotelRechargeEntity) {
			hotelRechargeEntity.setState(1);
			baseMapper.updateById(hotelRechargeEntity);
			// 给用户卡片加钱
			HotelMemberLevelDetailEntity memberLevelDetailEntity = hotelMemberLevelDetailDao.selectOne(Wrappers.<HotelMemberLevelDetailEntity>lambdaQuery().eq(HotelMemberLevelDetailEntity::getMemberId, hotelRechargeEntity.getUserId()).eq(HotelMemberLevelDetailEntity::getLevelId, hotelRechargeEntity.getCardId()));
			memberLevelDetailEntity.setBalance(NumberUtil.add(memberLevelDetailEntity.getBalance(), hotelRechargeEntity.getCzMoney()));
			this.addConsumptionRecord(hotelRechargeEntity.getCzMoney().intValue(), hotelRechargeEntity.getCardId(), hotelRechargeEntity.getUserId(), "在线充值");
			if (null != hotelRechargeEntity.getZsMoney() && hotelRechargeEntity.getZsMoney().intValue() > 0) {
				memberLevelDetailEntity.setBalance(NumberUtil.add(memberLevelDetailEntity.getBalance(), hotelRechargeEntity.getZsMoney()));
				this.addConsumptionRecord(hotelRechargeEntity.getZsMoney().intValue(), hotelRechargeEntity.getCardId(), hotelRechargeEntity.getUserId(), "充值赠送");
			}
			hotelMemberLevelDetailDao.updateById(memberLevelDetailEntity);
			HotelMemberEntity hotelMemberEntity = hotelMemberDao.selectById(memberLevelDetailEntity.getMemberId());
			// 获取酒店取消订单微信消息模板
			HotelWxConfigEntity hotelWxConfigEntity = null;
			HotelSellerEntity hotelSellerEntity = hotelSellerDao.selectById(memberLevelDetailEntity.getSellerId());
			hotelWxConfigEntity = hotelWxConfigService.getOne(new QueryWrapper<HotelWxConfigEntity>());
			if (null != hotelWxConfigEntity) {
				// 发送取消订房通知
				List<WxMaTemplateData> maTemplateDatas = new ArrayList<WxMaTemplateData>();
				maTemplateDatas.add(new WxMaTemplateData("first", "充值成功通知"));
				maTemplateDatas.add(new WxMaTemplateData("keyword1", hotelSellerEntity.getName()));
				maTemplateDatas.add(new WxMaTemplateData("keyword2", "会员卡充值"));
				maTemplateDatas.add(new WxMaTemplateData("keyword3", memberLevelDetailEntity.getCardNo()));
				maTemplateDatas.add(new WxMaTemplateData("keyword4", NumberUtil.decimalFormat(",###", hotelRechargeEntity.getCzMoney().doubleValue())));
				maTemplateDatas.add(new WxMaTemplateData("keyword5", NumberUtil.decimalFormat(",###", hotelRechargeEntity.getZsMoney().doubleValue())));
				maTemplateDatas.add(new WxMaTemplateData("keyword6", DateUtil.format(DateUtil.date(hotelRechargeEntity.getTime()), "yyyy-MM-dd")));
				WxMaTemplateMessage maTemplateMessage = new WxMaTemplateMessage(hotelMemberEntity.getOpenid(), "qFLAITJmXZ37LFyaQMmk3XF88nQATfUW-RUNdUD8RTU", null, formId, maTemplateDatas, null);
				WxMaConfiguration.getMaService(hotelWxConfigEntity.getAppId()).getMsgService().sendTemplateMsg(maTemplateMessage);
			}
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addConsumptionRecord(int amount, Long cardId, Long userId, String note) {
		HotelConsumptionRecordEntity consumptionRecordEntity = new HotelConsumptionRecordEntity();
		consumptionRecordEntity.setAmount(amount > 0 ? "+" + amount : "" + amount);
		consumptionRecordEntity.setCardId(cardId);
		consumptionRecordEntity.setUserId(userId);
		consumptionRecordEntity.setNote(note);
		HotelMemberLevelEntity memberLevelEntity = hotelMemberLevelDao.selectById(cardId);
		consumptionRecordEntity.setSellerId(memberLevelEntity.getSellerId());
		consumptionRecordEntity.setCreateDate(DateUtil.date());
		hotelConsumptionRecordDao.insert(consumptionRecordEntity);
	}

}