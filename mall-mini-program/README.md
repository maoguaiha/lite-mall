# 商城系统 · 微信小程序端

本目录是基于现有 `mall-lite` Web 商城（后端 `mall-portal`，接口前缀 `/api`，JWT 鉴权）开发的**微信小程序端**。完整对接后端 `member / product / cart / order / pay` 接口，包含账号密码登录与**微信一键登录**。

## 技术栈

- 微信原生小程序（WXML / WXSS / JS / JSON），无第三方框架
- 统一请求封装：`utils/request.js`（自动携带 `Authorization: Bearer <token>`，处理 `CommonResult{code,message,data}`，`code=200` 视为成功，`401` 自动登出）
- 参数规则：`params` 走 query（对应后端 `@RequestParam`），`data` 走 JSON body（对应 `@RequestBody`）

## 目录结构

```
greenfield/
├── app.js / app.json / app.wxss      # 全局配置、tabBar、全局占位样式
├── project.config.json               # 小程序项目配置（appid 现为 touristappid）
├── sitemap.json
├── utils/
│   ├── config.js                     # 后端地址 / 图片前缀（运行前必改）
│   ├── request.js                    # 网络请求封装
│   ├── api.js                        # 所有后端接口
│   ├── auth.js                       # 登录态读写
│   └── util.js                       # formatPrice / formatImage 等
└── pages/
    ├── index/       首页（搜索、轮播、分类入口、推荐、新品）
    ├── category/    分类（左侧一级 + 右侧二级）
    ├── product/     商品列表 list + 商品详情 detail（SKU 规格选择）
    ├── cart/        购物车（多选、改数量、删除、结算）
    ├── order/       确认订单 create / 订单列表 list / 订单详情 detail
    ├── profile/     我的
    ├── login/       登录（账号密码 + 微信一键登录）
    └── register/    注册
```

## 快速开始

1. 用**微信开发者工具**打开本目录（`greenfield/`）。
2. 修改 `utils/config.js`，把后端地址改成你的实际地址（默认 `http://localhost:8080`）：
   ```js
   const BASE_URL = 'http://localhost:8080/api'
   const IMG_BASE = 'http://localhost:8080'
   ```
   > 手机预览时 `localhost` 需替换为电脑局域网 IP（如 `http://192.168.x.x:8080/api`）。
3. 在开发者工具「详情 → 本地设置」中勾选 **不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书**。
4. 将 `project.config.json` 里的 `appid` 替换为你自己的小程序 AppID（当前为 `touristappid`，仅可体验）。
5. 确保 `mall-lite` 后端 `mall-portal` 已启动。

## 微信一键登录（已打通后端）

小程序端 `pages/login` 提供「微信一键登录」：

- 小程序调用 `wx.login()` 获取 `code`，请求后端 `POST /member/loginByWeixin?code=xxx`；
- 后端新增接口（`UmsMemberController.loginByWeixin`）会拿 `code` 向微信 `jscode2session` 换取 `openid`，以 `wx_<openid>` 作为用户名**自动注册 / 登录**，返回 JWT `token`；
- 小程序保存 `token` 并拉取会员信息，进入「我的」。

### 后端需要配置（mall-portal）

在 `mall-portal/src/main/resources/application.yml` 填入你的小程序凭证：

```yaml
wechat:
  appid: 你的小程序AppID
  secret: 你的小程序AppSecret
```

并在 `mall-security` 的 `SecurityConfig` 中已放行 `/member/loginByWeixin`（无需登录即可访问）。

> 说明：微信登录依赖真实 AppID / AppSecret 与微信服务器联调，未配置时该接口会返回「微信登录失败」，可改用账号密码 / 注册登录。

## 账号密码登录 / 注册

- 登录：`POST /member/login` → `token`
- 注册：`POST /member/register`
- 会员信息：`GET /member/info`（需登录）

## 图片占位说明

商品 / 购物车 / 订单在无图时显示**本地灰底占位**（纯 CSS 渲染，🛍 图标），不依赖网络或二进制资源，开箱即用。后端返回的图片地址由 `utils/util.js` 的 `formatImage()` 自动拼接 `IMG_BASE` 前缀。

## 接口与 Web 端一致性

- 商品详情真实路径为 `GET /product/detail/{id}`（已修正 Web 端一处错误路径）。
- 订单状态：`0待付款 / 1待发货 / 2待收货 / 3已完成 / 4已取消`。
- 购物车增改用 `@RequestParam`，下单与登录注册用 `@RequestBody`，请求封装已分别对应。

## 上线注意事项

- 正式发布需在 **微信公众平台 → 开发 → 开发管理 → 服务器域名** 配置合法的 HTTPS `request` 域名（小程序要求生产环境必须为 HTTPS）。
- 将 `utils/config.js` 的 `BASE_URL / IMG_BASE` 改为 HTTPS 生产地址。
- 微信登录的 `appid / secret` 务必使用生产小程序凭证。
