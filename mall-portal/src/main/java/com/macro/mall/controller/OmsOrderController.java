package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.mapper.UmsMemberAddressMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.UmsMemberAddress;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "OmsOrderController", description = "订单管理")
@RestController
@RequestMapping("/order")
public class OmsOrderController {

    private final OmsOrderService orderService;
    private final UmsMemberService memberService;
    private final UmsMemberAddressMapper addressMapper;

    @Autowired
    public OmsOrderController(OmsOrderService orderService, UmsMemberService memberService, UmsMemberAddressMapper addressMapper) {
        this.orderService = orderService;
        this.memberService = memberService;
        this.addressMapper = addressMapper;
    }

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public CommonResult<OmsOrder> createOrder(@RequestBody CreateOrderRequest request) {
        Long memberId = memberService.getCurrentMember().getId();
        Map<String, String> receiverInfo = new HashMap<>();
        receiverInfo.put("name", request.getReceiverName());
        receiverInfo.put("phone", request.getReceiverPhone());
        receiverInfo.put("province", request.getReceiverProvince());
        receiverInfo.put("city", request.getReceiverCity());
        receiverInfo.put("district", request.getReceiverDistrict());
        receiverInfo.put("detailAddress", request.getReceiverDetailAddress());

        // 若传入已保存的收货地址，用地址补全缺失的收货信息（保证数据完整）
        if (request.getAddressId() != null) {
            UmsMemberAddress addr = addressMapper.selectById(request.getAddressId());
            if (addr != null && memberId.equals(addr.getMemberId())) {
                if (isEmpty(receiverInfo.get("name"))) receiverInfo.put("name", addr.getName());
                if (isEmpty(receiverInfo.get("phone"))) receiverInfo.put("phone", addr.getPhone());
                if (isEmpty(receiverInfo.get("province"))) receiverInfo.put("province", addr.getProvince());
                if (isEmpty(receiverInfo.get("city"))) receiverInfo.put("city", addr.getCity());
                if (isEmpty(receiverInfo.get("district"))) receiverInfo.put("district", addr.getDistrict());
                if (isEmpty(receiverInfo.get("detailAddress"))) receiverInfo.put("detailAddress", addr.getDetailAddress());
            }
        }

        OmsOrder order = orderService.createOrder(memberId, request.getCartItemIds(), receiverInfo);
        return CommonResult.success(order, "下单成功");
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<OmsOrder>> getOrderList(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Long memberId = memberService.getCurrentMember().getId();
        CommonPage<OmsOrder> orderList = orderService.getOrderListPage(memberId, status, pageNum, pageSize);
        return CommonResult.success(orderList);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/detail/{id}")
    public CommonResult<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        OmsOrder order = orderService.getOrderById(id);
        List<OmsOrderItem> orderItems = orderService.getOrderItems(id);
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItems", orderItems);
        return CommonResult.success(result);
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay/{id}")
    public CommonResult payOrder(@PathVariable Long id) {
        orderService.payOrder(id);
        return CommonResult.success(null, "支付成功");
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{id}")
    public CommonResult cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return CommonResult.success(null, "取消成功");
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirm/{id}")
    public CommonResult confirmOrder(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return CommonResult.success(null, "确认收货成功");
    }

    public static class CreateOrderRequest {
        private List<Long> cartItemIds;
        private Long addressId;
        private String receiverName;
        private String receiverPhone;
        private String receiverProvince;
        private String receiverCity;
        private String receiverDistrict;
        private String receiverDetailAddress;

        public List<Long> getCartItemIds() { return cartItemIds; }
        public void setCartItemIds(List<Long> cartItemIds) { this.cartItemIds = cartItemIds; }
        public Long getAddressId() { return addressId; }
        public void setAddressId(Long addressId) { this.addressId = addressId; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
        public String getReceiverProvince() { return receiverProvince; }
        public void setReceiverProvince(String receiverProvince) { this.receiverProvince = receiverProvince; }
        public String getReceiverCity() { return receiverCity; }
        public void setReceiverCity(String receiverCity) { this.receiverCity = receiverCity; }
        public String getReceiverDistrict() { return receiverDistrict; }
        public void setReceiverDistrict(String receiverDistrict) { this.receiverDistrict = receiverDistrict; }
        public String getReceiverDetailAddress() { return receiverDetailAddress; }
        public void setReceiverDetailAddress(String receiverDetailAddress) { this.receiverDetailAddress = receiverDetailAddress; }
    }
}
