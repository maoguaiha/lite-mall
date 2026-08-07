-- 新增模块表结构（用户端：地址/评价/优惠券/秒杀；商家端：管理员）
-- 由 mall-portal 与 mall-admin 两个应用共同引用（classpath*: 加载依赖 jar 内资源）

CREATE TABLE IF NOT EXISTS ums_member_address (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_id BIGINT,
  name VARCHAR(64),
  phone VARCHAR(32),
  province VARCHAR(64),
  city VARCHAR(64),
  district VARCHAR(64),
  detail_address VARCHAR(255),
  is_default INT DEFAULT 0,
  delete_flag INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS oms_order_comment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT,
  order_item_id BIGINT,
  member_id BIGINT,
  product_id BIGINT,
  star INT,
  content VARCHAR(1000),
  pictures VARCHAR(1000),
  status INT DEFAULT 1,
  reply VARCHAR(1000),
  delete_flag INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sms_coupon (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128),
  amount DECIMAL(10,2),
  min_point DECIMAL(10,2),
  per_limit INT,
  publish_count INT,
  received_count INT DEFAULT 0,
  usable_range VARCHAR(64),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  delete_flag INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sms_coupon_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  coupon_id BIGINT,
  member_id BIGINT,
  order_id BIGINT,
  use_status INT DEFAULT 0,
  receive_time TIMESTAMP,
  use_time TIMESTAMP,
  delete_flag INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sms_seckill_session (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  status INT DEFAULT 0,
  delete_flag INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sms_seckill_product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT,
  product_id BIGINT,
  seckill_price DECIMAL(10,2),
  seckill_stock INT,
  seckill_sales INT DEFAULT 0,
  delete_flag INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ums_admin (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) UNIQUE,
  password VARCHAR(128),
  nickname VARCHAR(64),
  role VARCHAR(32) DEFAULT 'ADMIN',
  delete_flag INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============ 新增模块（阶段二补全） ============

-- 首页轮播广告
CREATE TABLE IF NOT EXISTS sms_home_advertise (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128),
  pic VARCHAR(512),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  status INT DEFAULT 0,
  click_count INT DEFAULT 0,
  order_num INT DEFAULT 0,
  type INT DEFAULT 0,
  url VARCHAR(512),
  note VARCHAR(255),
  delete_flag INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 专题
CREATE TABLE IF NOT EXISTS cms_subject (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_id BIGINT,
  title VARCHAR(128),
  pic VARCHAR(512),
  product_count INT DEFAULT 0,
  recommend_status INT DEFAULT 0,
  show_status INT DEFAULT 0,
  sort INT DEFAULT 0,
  description TEXT,
  delete_flag INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单设置（单例配置，id=1）
CREATE TABLE IF NOT EXISTS oms_order_setting (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  flash_order_overtime INT DEFAULT 0,
  normal_order_overtime INT DEFAULT 0,
  confirm_overtime INT DEFAULT 0,
  finish_overtime INT DEFAULT 0,
  comment_overtime INT DEFAULT 0,
  member_level INT DEFAULT 0,
  auto_comment INT DEFAULT 0,
  delete_flag INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 后台角色
CREATE TABLE IF NOT EXISTS ums_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  code VARCHAR(100),
  description VARCHAR(500),
  status INT DEFAULT 1,
  sort INT DEFAULT 0,
  delete_flag INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 后台菜单
CREATE TABLE IF NOT EXISTS ums_menu (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  parent_id BIGINT DEFAULT 0,
  title VARCHAR(100),
  name VARCHAR(100),
  url VARCHAR(200),
  icon VARCHAR(100),
  type INT DEFAULT 0,
  sort INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 管理员-角色 关联
CREATE TABLE IF NOT EXISTS ums_admin_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  admin_id BIGINT,
  role_id BIGINT,
  UNIQUE (admin_id, role_id)
);

-- 角色-菜单 关联
CREATE TABLE IF NOT EXISTS ums_role_menu (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_id BIGINT,
  menu_id BIGINT,
  UNIQUE (role_id, menu_id)
);
