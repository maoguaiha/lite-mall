# mall-lite 商城后端

本仓库包含商城系统的**后端 API**，以及微信小程序端 `mall-mini-program/`（与 `mall-lite-frontend` 的 Web 端并列）。后端为小程序与 Web 端提供 REST API。
基于 Spring Boot 3.2 + MyBatis-Plus + Spring Security(JWT)，数据库使用 H2（文件库）。

## 模块说明

| 模块 | 说明 |
| --- | --- |
| `mall-portal` | 前台 API 服务，端口 **8080**，接口前缀 `/api` |
| `mall-admin`  | 管理后台 |
| `mall-mbg`    | MyBatis 生成层（实体 `model` / `mapper`） |
| `mall-security` | Spring Security + JWT 鉴权 |
| `mall-common` | 公共组件 / 工具 |

## 前端：微信小程序（`mall-mini-program/`）

微信小程序源码位于本仓库的 **`mall-mini-program/`** 目录（从原 `greenfield` 项目整体复制而来，原 `greenfield` 目录保持不动）。结构：

```
mall-mini-program/
├── app.js / app.json / app.wxss     小程序入口与全局配置/样式
├── project.config.json             微信开发者工具项目配置
├── sitemap.json
├── pages/                          页面（index/category/product/cart/order/profile/login/register）
└── utils/                          请求封装、BASE_URL 配置、token 管理
```

### 启动方式

1. 打开**微信开发者工具** → 导入项目 → 目录选择 `mall-lite/mall-mini-program/`。
2. 勾选「不校验合法域名…」（本地联调时）。
3. 后端地址在 `mall-mini-program/utils/config.js` 的 `BASE_URL`：本地默认 `http://localhost:8080/api`；手机预览时改为本机局域网 IP（如 `http://192.168.x.x:8080/api`）。
4. 后端需先启动（见下文「运行」），小程序登录后带 `token` 即可调用收货地址、商品收藏等接口。

## 运行

### 一键启动（推荐，Windows）

mall-lite 提供了一套一键启动脚本：双击即可拉起全家桶（后端 API + H2 数据库 + Redis + Web 前台 + 微信小程序），并自动打开微信开发者工具。绝大多数情况下**不需要手动敲命令**。

**前置依赖**

- Windows 10 / 11
- 已安装 **Docker Desktop** 并处于运行中（脚本会自检）
- **Java 17+** / **Maven 3.8+**（host 模式或本地构建时需要）
- **Node.js 18+**（Web 前台 / 微信开发者工具需要）
- **PowerShell 5.1+**（Windows 自带）

**最常用的两种启动方式**

| 方式 | 操作 | 说明 |
| --- | --- | --- |
| 双击启动 | 双击仓库根目录 `run.bat` | 默认以 `docker` 模式启动全套服务，并自动打开微信开发者工具 |
| 一键停止 | 双击 `stop.bat` | 停止并清理所有容器 |

**统一启动器 `mall-lite.ps1`**

`run.bat` / `stop.bat` 本质是 `mall-lite.ps1` 的薄封装。在 PowerShell 中直接调用它可获得更多控制：

```powershell
# 以 docker 模式启动（默认，依赖最少）
.\mall-lite.ps1 start

# 以 host 模式启动（后端 / Web 直接用本机进程，便于断点调试与热加载）
.\mall-lite.ps1 start -Mode host

# 启动但不拉起微信小程序（无开发者工具环境时使用）
.\mall-lite.ps1 start -NoMini

# 其他子命令
.\mall-lite.ps1 stop        # 停止
.\mall-lite.ps1 restart     # 重启
.\mall-lite.ps1 status      # 查看各服务状态
.\mall-lite.ps1 logs        # 跟踪容器日志
.\mall-lite.ps1 build       # 重新构建后端 / 前端镜像
.\mall-lite.ps1 mini        # 仅打开微信小程序
.\mall-lite.ps1 mini-close  # 关闭微信小程序
```

> `start.ps1` / `stop.ps1` 是 `mall-lite.ps1 start -Mode host` / `stop` 的快捷封装，方便习惯脚本方式的人使用。

**环境自检与构建检查**

- `check-env.bat`：检查 Docker / Java / Maven / Node / PowerShell 是否就绪，排错第一步先跑它。
- `build-check.bat`：正式构建前做连通性检查（含 Redis 镜像预拉），避免构建中途失败。

**访问地址**

| 服务 | docker 模式 | host 模式 |
| --- | --- | --- |
| Web 前台（Vite） | http://localhost:18088 | http://localhost:8088 |
| Portal API | http://localhost:18080（`/api`） | http://localhost:8080（`/api`） |
| Admin API | http://localhost:18081（`/admin-api`） | http://localhost:18081（`/admin-api`） |
| H2 控制台 | http://localhost:9092 | — |
| Redis | localhost:6379 | localhost:6379 |
| 微信小程序后台 | 同 Portal API：http://localhost:8080/api | 同左 |

> 微信小程序端（`mall-mini-program/`）通过 `utils/config.js` 的 `BASE_URL` 连接后端，本地默认 `http://localhost:8080/api`。脚本会自动尝试打开微信开发者工具并导入项目（appid `wxb23c20ad538a6cea`）；若未安装开发者工具或路径未匹配，会跳过此步，**不影响**后端 / Web 启动。

**常见排错**

1. Docker 未启动 / 未安装 → 先启动 Docker Desktop，或改用 host 模式：`.\mall-lite.ps1 start -Mode host`。
2. 端口被占用（18080 / 18088 / 18081 / 9092 等）→ 关闭占用进程，或修改 `docker-compose.yml` 端口映射后重启。
3. 镜像拉不动（国内网络）→ 编辑 `.env` 将 `REGISTRY` 改为可用的镜像代理前缀（默认 `docker.1ms.run/library/`），再运行 `.\mall-lite.ps1 build`。
4. 微信小程序未弹出 → 确认已安装微信开发者工具；脚本会扫描常见安装路径，未命中则只跳过小程序，后端 / Web 仍正常。
5. Redis 未启动 → 仅打印 WARN，不影响地址 / 收藏 / 商品 / 订单等核心功能（见下「存储与配置」）。

**两种模式如何选择**

- `docker`（默认）：一键拉起完整环境，依赖最少，适合首次体验和演示。
- `host`：后端与 Web 直接以本机进程运行，便于断点调试和改代码热加载，但需要本机 Java / Maven / Node 就绪。

### 本地启动（已验证）

聚合 pom 不能直接 `spring-boot:run`（无 main class），需先安装依赖再运行 jar：

```bash
# 1. 安装依赖模块与打包 mall-portal（跳过测试，离线优先）
mvn -o -pl mall-portal -am install -DskipTests

# 2. 启动（工作目录保持 mall-portal，使 H2 的 ../mall-shared 路径正确）
cd mall-portal
java -jar target/mall-portal-1.0-SNAPSHOT.jar
```

启动成功后日志出现：`Started MallPortalApplication`。服务监听 `http://localhost:8080`。

### 容器 / 脚本启动

容器编排与脚本化启动的完整用法见上文「一键启动（推荐，Windows）」。`docker-compose.yml` 与各 `Dockerfile.*` 由 `mall-lite.ps1` 自动调用，一般无需手动操作。

### 存储与配置

- **数据库**：H2 文件库，物理路径 `mall-lite/mall-shared`（`jdbc:h2:file:../mall-shared;AUTO_SERVER=TRUE`）。
  建表脚本 `schema-h2.sql` + `schema-modules.sql` 在应用启动时自动执行；收藏表 `ums_member_favorite_product` 即在此初始化。
- **Redis**：`localhost:6379`，仅用于秒杀库存预热（`SeckillStockInitializer`）。未启动时会打印 WARN 且不影响地址 / 收藏 / 商品 / 订单等核心功能。
- **微信登录**：`mall-portal/src/main/resources/application.yml` 的 `wechat.appid` / `wechat.secret`。
- **JWT**：`jwt.secret` / `jwt.expire`（默认 24h）。

## 接口规范

- 统一前缀 `/api`（下文路径均省略前缀）。
- 鉴权：JWT，请求头 `Authorization: Bearer <token>`。
- 除登录相关接口外，其余接口**均需登录**（未携带有效 token 返回 `403`）。
- 统一返回结构 `CommonResult{code, message, data}`，`code=200` 视为成功。

## 会员与登录

| 接口 | 说明 |
| --- | --- |
| `POST /member/login` | 账号密码登录 → 返回 `token` |
| `POST /member/register` | 注册 |
| `GET /member/info` | 获取当前会员信息（需登录） |
| `POST /member/loginByWeixin?code=` | 微信一键登录（用 `code` 向微信换 `openid`，以 `wx_<openid>` 自动注册/登录） |

## 收货地址管理（新增）

后端 `AddressController` 提供会员收货地址的完整 CRUD，按 JWT 自动归属当前会员，地址相互隔离。

| 接口 | 方法 / 说明 |
| --- | --- |
| `/member/address/list` | `GET` 当前会员地址列表 |
| `/member/address/{id}` | `GET` 地址详情 |
| `/member/address/create` | `POST` 新增，body：`name, phone, province, city, district, detailAddress, isDefault` |
| `/member/address/update` | `POST` 修改，额外带 `id` |
| `/member/address/delete?id=` | `POST` 删除 |
| `/member/address/default?id=` | `POST` 设为默认 |

### 下单时选择地址

确认订单接口 `POST /order/create` 新增可选字段 `addressId`：

- 前端在确认订单页可从地址列表（`?mode=select`）选择，选中后回填收货人 / 手机 / 省市区 / 详细地址，并提交 `addressId`；
- 后端收到 `addressId` 后按当前会员校验归属，并用该地址**补全**表单中缺失的收货字段（手动填写优先），无需小程序端重复录入。

## 商品收藏（新增）

后端新建表 `ums_member_favorite_product`（H2 自动初始化），提供按会员隔离的商品收藏接口；收藏时自动快照商品名称 / 主图 / 价格。

| 接口 | 方法 / 说明 |
| --- | --- |
| `/member/favorite/add?productId=` | `POST` 收藏（快照商品名称 / 主图 / 价格） |
| `/member/favorite/delete?productId=` | `POST` 取消收藏 |
| `/member/favorite/list` | `GET` 收藏列表 |
| `/member/favorite/check?productId=` | `GET` 是否已收藏（用于详情页红心状态） |

## 商品 / 购物车 / 订单（既有核心接口）

- 商品：`GET /product/list`、`GET /product/detail/{id}`
- 购物车：增 / 改 / 查 / 删（均使用 `@RequestParam`）
- 订单：`POST /order/create`（使用 `@RequestBody`，支持 `addressId`）、订单列表 / 详情
- 订单状态：`0待付款 / 1待发货 / 2待收货 / 3已完成 / 4已取消`

更完整的接口与字段定义见仓库内 `spec-mall-lite.md` 及源码。
