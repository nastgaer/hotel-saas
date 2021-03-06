package io.renren.modules.hotel.wx;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponInfoQueryRequest;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponInfoQueryResult;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponSendRequest;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponSendResult;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponStockQueryRequest;
import com.github.binarywang.wxpay.bean.coupon.WxPayCouponStockQueryResult;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxScanPayNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayDownloadBillRequest;
import com.github.binarywang.wxpay.bean.request.WxPayMicropayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayOrderCloseRequest;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryRequest;
import com.github.binarywang.wxpay.bean.request.WxPayOrderReverseRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundQueryRequest;
import com.github.binarywang.wxpay.bean.request.WxPayReportRequest;
import com.github.binarywang.wxpay.bean.request.WxPaySendRedpackRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayBillResult;
import com.github.binarywang.wxpay.bean.result.WxPayMicropayResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderCloseResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderReverseResult;
import com.github.binarywang.wxpay.bean.result.WxPayRedpackQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPaySendRedpackResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;

import cn.hutool.core.thread.ThreadUtil;
import io.renren.modules.constants.OrderTypeConstants;
import io.renren.modules.hotel.config.WxPayConfiguration;
import io.renren.modules.hotel.service.HotelMemberLevelService;
import io.renren.modules.hotel.service.HotelOrderService;
import io.renren.modules.hotel.service.HotelRechargeService;
import io.renren.modules.wx.OrderType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Binary Wang
 */
@Slf4j
@Api("????????????")
@RestController
@RequestMapping("/pay/{appid}")
public class WxPayController {
	private WxPayService wxService;

	@Autowired
	private HotelOrderService hotelOrderService;

	@Autowired
	private HotelRechargeService hotelRechargeService;

	@Autowired
	private HotelMemberLevelService hotelMemberLevelService;

//	@Autowired
//	public WxPayController(WxPayService wxService) {
//		this.wxService = wxService;
//	}

	/**
	 * <pre>
	 * ????????????(??????https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2)
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ????????????????????????????????????
	 * ??? ???????????????????????????????????????????????????????????????????????????????????????????????????
	 * ??? ????????????????????????????????????????????????????????????????????????
	 * ??? ??????????????????API?????????USERPAYING????????????
	 * ??? ???????????????????????????API?????????????????????????????????
	 * ???????????????https://api.mch.weixin.qq.com/pay/orderquery
	 * </pre>
	 *
	 * @param transactionId ???????????????
	 * @param outTradeNo    ?????????????????????????????????????????????transactionId?????????????????????
	 */
	@ApiOperation(value = "????????????")
	@GetMapping("/queryOrder")
	public WxPayOrderQueryResult queryOrder(@RequestParam(required = false) String transactionId, @RequestParam(required = false) String outTradeNo) throws WxPayException {
		return this.wxService.queryOrder(transactionId, outTradeNo);
	}

	@ApiOperation(value = "????????????")
	@PostMapping("/queryOrder")
	public WxPayOrderQueryResult queryOrder(WxPayOrderQueryRequest wxPayOrderQueryRequest) throws WxPayException {
		return this.wxService.queryOrder(wxPayOrderQueryRequest);
	}

	/**
	 * <pre>
	 * ????????????
	 * ????????????
	 * ???????????????????????????????????????
	 * 1. ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 2. ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ????????????????????????????????????????????????????????????????????????????????????5?????????
	 * ???????????????https://api.mch.weixin.qq.com/pay/closeorder
	 * ?????????????????????   ????????????
	 * </pre>
	 *
	 * @param outTradeNo ??????????????????????????????
	 */
	@ApiOperation(value = "????????????")
	@GetMapping("/closeOrder/{outTradeNo}")
	public WxPayOrderCloseResult closeOrder(@PathVariable String outTradeNo) throws WxPayException {
		return this.wxService.closeOrder(outTradeNo);
	}

	@ApiOperation(value = "????????????")
	@PostMapping("/closeOrder")
	public WxPayOrderCloseResult closeOrder(WxPayOrderCloseRequest wxPayOrderCloseRequest) throws WxPayException {
		return this.wxService.closeOrder(wxPayOrderCloseRequest);
	}

	/**
	 * ??????????????????????????????????????????????????????????????????.
	 *
	 * @param request ????????????????????????
	 * @param <T>     ?????????{@link com.github.binarywang.wxpay.bean.order}????????????
	 * @return ?????? {@link com.github.binarywang.wxpay.bean.order}??????????????????
	 */
	@ApiOperation(value = "??????????????????????????????????????????")
	@PostMapping("/createOrder")
	public <T> T createOrder(@RequestBody WxPayUnifiedOrderRequest request) throws WxPayException {
		return this.wxService.createOrder(request);
	}

	/**
	 * ????????????(??????https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1)
	 * ??????????????????????????????????????????????????????????????????"???????????????????????????"
	 * ???????????????https://api.mch.weixin.qq.com/pay/unifiedorder
	 *
	 * @param request ????????????????????????????????????appid???mchid?????????????????????????????????????????????????????????????????????????????????????????????????????????
	 */
	@ApiOperation(value = "???????????????????????????")
	@PostMapping("/unifiedOrder")
	public WxPayUnifiedOrderResult unifiedOrder(@RequestBody WxPayUnifiedOrderRequest request) throws WxPayException {
		return this.wxService.unifiedOrder(request);
	}

	/**
	 * <pre>
	 * ????????????-????????????
	 * ???????????????
	 *  ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????20??????????????????
	 *  ????????????????????????3??????????????????????????????????????????
	 * ?????? https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_5
	 * ???????????????https://api.mch.weixin.qq.com/pay/refundquery
	 * </pre>
	 * 
	 * ???????????????????????????
	 *
	 * @param transactionId ???????????????
	 * @param outTradeNo    ???????????????
	 * @param outRefundNo   ??????????????????
	 * @param refundId      ??????????????????
	 * @return ????????????
	 */
	@ApiOperation(value = "????????????")
	@GetMapping("/refundQuery")
	public WxPayRefundQueryResult refundQuery(@RequestParam(required = false) String transactionId, @RequestParam(required = false) String outTradeNo, @RequestParam(required = false) String outRefundNo, @RequestParam(required = false) String refundId) throws WxPayException {
		return this.wxService.refundQuery(transactionId, outTradeNo, outRefundNo, refundId);
	}

	@ApiOperation(value = "????????????")
	@PostMapping("/refundQuery")
	public WxPayRefundQueryResult refundQuery(WxPayRefundQueryRequest wxPayRefundQueryRequest) throws WxPayException {
		return this.wxService.refundQuery(wxPayRefundQueryRequest);
	}

	@ApiOperation(value = "????????????????????????")
	@PostMapping("/notify/order")
	public String parseOrderNotifyResult(@PathVariable String appid, @RequestBody String xmlData) throws WxPayException {
		log.info("????????????????????????--start,params:{}", xmlData);
		final WxPayOrderNotifyResult notifyResult = WxPayConfiguration.getPayServices().get(appid).parseOrderNotifyResult(xmlData);
		log.debug("????????????????????????--start,parse date :{}", JSON.toJSONString(notifyResult));
		// ?????????????????????????????????????????????????????? ??????????????????
		ThreadUtil.execute(new Runnable() {
			@Override
			public void run() {
				OrderType orderType = JSON.parseObject(notifyResult.getAttach(), OrderType.class);
				if (OrderTypeConstants.order_room == orderType.getType()) {
					log.info("?????????????????????????????????");
					hotelOrderService.updateOrderStatus2Payed(notifyResult.getOutTradeNo());
				}
				if (OrderTypeConstants.order_recharge == orderType.getType()) {
					log.info("??????????????????");
					hotelRechargeService.cardRechargeHandler(notifyResult.getOutTradeNo(), orderType.getFormId());
				}
				if (OrderTypeConstants.order_becomvip == orderType.getType()) {
					log.info("????????????????????????");
					hotelMemberLevelService.becomeVipCallBack(notifyResult.getOutTradeNo(), orderType.getFormId());
				}
			}
		});
		log.info("????????????????????????--success,");
		return WxPayNotifyResponse.success("??????");
	}

	@ApiOperation(value = "????????????????????????")
	@PostMapping("/notify/refund")
	public String parseRefundNotifyResult(@PathVariable String appid, @RequestBody String xmlData) throws WxPayException {
		log.info("??????????????????--start,params:{}", xmlData);
		final WxPayRefundNotifyResult result = WxPayConfiguration.getPayServices().get(appid).parseRefundNotifyResult(xmlData);
		// hotelOrderService.updateOrderStatus2Refunded(result.get)
		log.info("??????????????????--start,params:{}", JSON.toJSONString(result));
		hotelOrderService.updateOrderStatus2Refunded(result.getReqInfo().getOutTradeNo());
		log.info("??????????????????--success,????????????");
		return WxPayNotifyResponse.success("??????");
	}

	@ApiOperation(value = "??????????????????????????????")
	@PostMapping("/notify/scanpay")
	public String parseScanPayNotifyResult(String xmlData) throws WxPayException {
		final WxScanPayNotifyResult result = this.wxService.parseScanPayNotifyResult(xmlData);
		// TODO ????????????????????????????????????????????????
		return WxPayNotifyResponse.success("??????");
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * <pre>
	 * ????????????:
	 * ?????????????????? https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_4&index=3
	 *  ???????????????https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack
	 * ?????????????????? https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_5&index=4
	 *  ???????????????https://api.mch.weixin.qq.com/mmpaymkttransfers/sendgroupredpack
	 * </pre>
	 *
	 * @param request ????????????
	 */
	@ApiOperation(value = "????????????")
	@PostMapping("/sendRedpack")
	public WxPaySendRedpackResult sendRedpack(@RequestBody WxPaySendRedpackRequest request) throws WxPayException {
		return this.wxService.sendRedpack(request);
	}

	/**
	 * <pre>
	 *   ??????????????????
	 *   ?????????????????????????????????????????????????????????????????????????????????????????????????????????
	 *   ??????Url	https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo
	 *   ??????????????????	????????????????????????????????????????????????
	 *   ????????????	POST
	 * </pre>
	 *
	 * @param mchBillNo ?????????????????????????????????????????????10000098201411111234567890
	 */
	@ApiOperation(value = "????????????")
	@GetMapping("/queryRedpack/{mchBillNo}")
	public WxPayRedpackQueryResult queryRedpack(@PathVariable String mchBillNo) throws WxPayException {
		return this.wxService.queryRedpack(mchBillNo);
	}

	/**
	 * <pre>
	 * ?????????????????????????????????????????????
	 * ?????????????????????????????????????????????
	 * weixin://wxpay/bizpayurl?sign=XXXXX&appid=XXXXX&mch_id=XXXXX&product_id=XXXXXX&time_stamp=XXXXXX&nonce_str=XXXXX
	 * ??????XXXXX?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ????????????: https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=6_4
	 * </pre>
	 *
	 * @param productId  ??????Id
	 * @param logoFile   ??????logo????????????????????????????????????
	 * @param sideLength ???????????????????????????????????????????????????????????????400
	 * @return ?????????????????????????????????
	 */
	public byte[] createScanPayQrcodeMode1(String productId, File logoFile, Integer sideLength) {
		return this.wxService.createScanPayQrcodeMode1(productId, logoFile, sideLength);
	}

	/**
	 * <pre>
	 * ?????????????????????????????????????????????
	 * ?????????????????????????????????????????????
	 * weixin://wxpay/bizpayurl?sign=XXXXX&appid=XXXXX&mch_id=XXXXX&product_id=XXXXXX&time_stamp=XXXXXX&nonce_str=XXXXX
	 * ??????XXXXX?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ????????????: https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=6_4
	 * </pre>
	 *
	 * @param productId ??????Id
	 * @return ??????????????????URL??????
	 */
	public String createScanPayQrcodeMode1(String productId) {
		return this.wxService.createScanPayQrcodeMode1(productId);
	}

	/**
	 * <pre>
	 * ?????????????????????????????????????????????
	 * ?????????????????????weixin???//wxpay/bizpayurl?sr=XXXXX?????????????????????????????????code_url????????????????????????
	 * ???????????????????????????????????????????????????????????????????????????????????????
	 * ????????????: https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=6_5
	 * </pre>
	 *
	 * @param codeUrl    ?????????????????????????????????????????????
	 * @param logoFile   ??????logo????????????????????????????????????
	 * @param sideLength ???????????????????????????????????????????????????????????????400
	 * @return ?????????????????????????????????
	 */
	public byte[] createScanPayQrcodeMode2(String codeUrl, File logoFile, Integer sideLength) {
		return this.wxService.createScanPayQrcodeMode2(codeUrl, logoFile, sideLength);
	}

	/**
	 * <pre>
	 * ????????????
	 * ???????????????
	 *  ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 *  ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 *  ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ??????????????? https://api.mch.weixin.qq.com/payitil/report
	 * ??????????????????????????????
	 * </pre>
	 */
	@ApiOperation(value = "????????????????????????")
	@PostMapping("/report")
	public void report(@RequestBody WxPayReportRequest request) throws WxPayException {
		this.wxService.report(request);
	}

	/**
	 * <pre>
	 * ???????????????
	 * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ?????????
	 * 1????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????bill_type???REVOKED???
	 * 2??????????????????9???????????????????????????????????????????????????10??????????????????
	 * 3?????????????????????????????????????????????????????????
	 * 4?????????????????????????????????????????????????????????
	 * ???????????????https://api.mch.weixin.qq.com/pay/downloadbill
	 * ????????????: <a href=
	"https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_6">???????????????</a>
	 * </pre>
	 *
	 * @param billDate   ??????????????? bill_date ????????????????????????????????????20140603
	 * @param billType   ???????????? bill_type
	 *                   ALL????????????????????????????????????????????????SUCCESS???????????????????????????????????????REFUND???????????????????????????
	 * @param tarType    ???????????? tar_type ??????????????????????????????GZIP??????????????????.gzip?????????????????????????????????????????????????????????
	 * @param deviceInfo ????????? device_info ?????????????????????????????????
	 * @return ??????????????????????????????
	 */
	@ApiOperation(value = "???????????????")
	@GetMapping("/downloadBill/{billDate}/{billType}/{tarType}/{deviceInfo}")
	public WxPayBillResult downloadBill(@PathVariable String billDate, @PathVariable String billType, @PathVariable String tarType, @PathVariable String deviceInfo) throws WxPayException {
		return this.wxService.downloadBill(billDate, billType, tarType, deviceInfo);
	}

	@ApiOperation(value = "???????????????")
	@PostMapping("/downloadBill")
	public WxPayBillResult downloadBill(WxPayDownloadBillRequest wxPayDownloadBillRequest) throws WxPayException {
		return this.wxService.downloadBill(wxPayDownloadBillRequest);
	}

	/**
	 * <pre>
	 * ??????????????????
	 * ???????????????https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_10&index=1
	 * ???????????????
	 * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * ??????1????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????5???????????????????????????API????????????????????????????????????????????????????????????USERPAYING??????????????????????????????????????????(??????10???)??????????????????????????????????????????????????????(??????30???)???
	 * ??????2??????????????????????????????????????????????????????????????????????????????????????????API?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????API???,????????????15??????????????????????????????API?????????????????????
	 * ???????????????   https://api.mch.weixin.qq.com/pay/micropay
	 * ?????????????????????????????????
	 * </pre>
	 */
	@ApiOperation(value = "??????????????????")
	@PostMapping("/micropay")
	public WxPayMicropayResult micropay(@RequestBody WxPayMicropayRequest request) throws WxPayException {
		return this.wxService.micropay(request);
	}

	/**
	 * <pre>
	 * ????????????API
	 * ???????????????https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_11&index=3
	 * ???????????????
	 *  ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 *  ?????????7????????????????????????????????????????????????????????????????????????????????????????????????????????????API?????????????????????????????????????????????API?????????????????????????????????????????????????????????API??????
	 *  ???????????????????????????????????????????????????API????????????????????????15s?????????????????????????????????
	 *  ???????????? ???https://api.mch.weixin.qq.com/secapi/pay/reverse
	 *  ????????????????????????????????????????????????
	 * </pre>
	 */
	@ApiOperation(value = "????????????")
	@PostMapping("/reverseOrder")
	public WxPayOrderReverseResult reverseOrder(@RequestBody WxPayOrderReverseRequest request) throws WxPayException {
		return this.wxService.reverseOrder(request);
	}

	@ApiOperation(value = "????????????????????????key")
	@GetMapping("/getSandboxSignKey")
	public String getSandboxSignKey() throws WxPayException {
		return this.wxService.getSandboxSignKey();
	}

	@ApiOperation(value = "???????????????")
	@PostMapping("/sendCoupon")
	public WxPayCouponSendResult sendCoupon(@RequestBody WxPayCouponSendRequest request) throws WxPayException {
		return this.wxService.sendCoupon(request);
	}

	@ApiOperation(value = "?????????????????????")
	@PostMapping("/queryCouponStock")
	public WxPayCouponStockQueryResult queryCouponStock(@RequestBody WxPayCouponStockQueryRequest request) throws WxPayException {
		return this.wxService.queryCouponStock(request);
	}

	@ApiOperation(value = "?????????????????????")
	@PostMapping("/queryCouponInfo")
	public WxPayCouponInfoQueryResult queryCouponInfo(@RequestBody WxPayCouponInfoQueryRequest request) throws WxPayException {
		return this.wxService.queryCouponInfo(request);
	}

	@ApiOperation(value = "????????????????????????")
	@PostMapping("/queryComment")
	public String queryComment(Date beginDate, Date endDate, Integer offset, Integer limit) throws WxPayException {
		return this.wxService.queryComment(beginDate, endDate, offset, limit);
	}

}
