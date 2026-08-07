-- 新增模块种子数据（商家端登录账号由 mall-admin 的 CommandLineRunner 播种，
-- 此处仅放业务演示数据：一张优惠券、一个秒杀场次及商品）

-- 优惠券：满100减20，全品类，每人限领1张，发放100张
INSERT INTO sms_coupon (id, name, amount, min_point, per_limit, publish_count, usable_range, start_time, end_time)
SELECT 1, '新人满100减20', 20.00, 100.00, 1, 100, 'ALL', CURRENT_TIMESTAMP, DATEADD('DAY', 30, CURRENT_TIMESTAMP)
WHERE NOT EXISTS (SELECT 1 FROM sms_coupon WHERE id = 1);

-- 秒杀场次
INSERT INTO sms_seckill_session (id, name, start_time, end_time, status)
SELECT 1, '每日整点秒杀', CURRENT_TIMESTAMP, DATEADD('DAY', 1, CURRENT_TIMESTAMP), 1
WHERE NOT EXISTS (SELECT 1 FROM sms_seckill_session WHERE id = 1);

-- 秒杀商品：商品1，秒杀价1元，库存100
INSERT INTO sms_seckill_product (id, session_id, product_id, seckill_price, seckill_stock, seckill_sales)
SELECT 1, 1, 1, 1.00, 100, 0
WHERE NOT EXISTS (SELECT 1 FROM sms_seckill_product WHERE id = 1);

-- ============ 阶段二补全模块种子 ============

-- 角色：超级管理员
INSERT INTO ums_role (id, name, code, description, status, sort)
SELECT 1, '超级管理员', 'ROLE_ADMIN', '拥有后台全部权限', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM ums_role WHERE id = 1);

-- 管理员-角色：把 id=1 的商家账号绑定为超级管理员
INSERT INTO ums_admin_role (admin_id, role_id)
SELECT 1, 1
WHERE NOT EXISTS (SELECT 1 FROM ums_admin_role WHERE admin_id = 1 AND role_id = 1);

-- 菜单：后台功能菜单
INSERT INTO ums_menu (id, parent_id, title, name, url, icon, type, sort)
SELECT 1, 0, '商品', 'pms', '/pms', 'goods', 0, 1
WHERE NOT EXISTS (SELECT 1 FROM ums_menu WHERE id = 1);
INSERT INTO ums_menu (id, parent_id, title, name, url, icon, type, sort)
SELECT 2, 0, '订单', 'oms', '/oms', 'order', 0, 2
WHERE NOT EXISTS (SELECT 1 FROM ums_menu WHERE id = 2);
INSERT INTO ums_menu (id, parent_id, title, name, url, icon, type, sort)
SELECT 3, 0, '会员', 'ums', '/ums', 'user', 0, 3
WHERE NOT EXISTS (SELECT 1 FROM ums_menu WHERE id = 3);
INSERT INTO ums_menu (id, parent_id, title, name, url, icon, type, sort)
SELECT 4, 0, '营销', 'sms', '/sms', 'sale', 0, 4
WHERE NOT EXISTS (SELECT 1 FROM ums_menu WHERE id = 4);
INSERT INTO ums_menu (id, parent_id, title, name, url, icon, type, sort)
SELECT 5, 0, '系统', 'sys', '/sys', 'setting', 0, 5
WHERE NOT EXISTS (SELECT 1 FROM ums_menu WHERE id = 5);

-- 角色-菜单：超级管理员拥有全部菜单
INSERT INTO ums_role_menu (role_id, menu_id)
SELECT 1, 1 WHERE NOT EXISTS (SELECT 1 FROM ums_role_menu WHERE role_id = 1 AND menu_id = 1);
INSERT INTO ums_role_menu (role_id, menu_id)
SELECT 1, 2 WHERE NOT EXISTS (SELECT 1 FROM ums_role_menu WHERE role_id = 1 AND menu_id = 2);
INSERT INTO ums_role_menu (role_id, menu_id)
SELECT 1, 3 WHERE NOT EXISTS (SELECT 1 FROM ums_role_menu WHERE role_id = 1 AND menu_id = 3);
INSERT INTO ums_role_menu (role_id, menu_id)
SELECT 1, 4 WHERE NOT EXISTS (SELECT 1 FROM ums_role_menu WHERE role_id = 1 AND menu_id = 4);
INSERT INTO ums_role_menu (role_id, menu_id)
SELECT 1, 5 WHERE NOT EXISTS (SELECT 1 FROM ums_role_menu WHERE role_id = 1 AND menu_id = 5);

-- 首页广告示例
INSERT INTO sms_home_advertise (id, name, pic, status, type, url, note)
SELECT 1, '618 大促主视觉', '/images/banner1.png', 1, 0, '/promotion/flash', '首页顶部轮播'
WHERE NOT EXISTS (SELECT 1 FROM sms_home_advertise WHERE id = 1);

-- 专题示例
INSERT INTO cms_subject (id, category_id, title, pic, product_count, recommend_status, show_status, sort, description)
SELECT 1, 1, '夏季新品专题', '/images/subject1.png', 12, 1, 1, 1, '夏季清凉好物集合'
WHERE NOT EXISTS (SELECT 1 FROM cms_subject WHERE id = 1);

-- 订单设置（单例，id=1）
INSERT INTO oms_order_setting (id, flash_order_overtime, normal_order_overtime, confirm_overtime, finish_overtime, comment_overtime, member_level, auto_comment)
SELECT 1, 30, 60, 7, 15, 7, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM oms_order_setting WHERE id = 1);
