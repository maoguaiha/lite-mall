INSERT INTO ums_member_level (id, name, growth_point, default_status, free_freight_point) VALUES (1, '普通会员', 0, 1, 0);

INSERT INTO pms_product_category (id, name, parent_id, level, sort, show_status) VALUES
(1, '数码', 0, 1, 1, 1),
(2, '电脑', 0, 1, 2, 1),
(3, '服饰', 0, 1, 3, 1),
(4, '美妆', 0, 1, 4, 1),
(5, '运动', 0, 1, 5, 1),
(6, '图书', 0, 1, 6, 1),
(7, '手机', 1, 2, 1, 1),
(8, '耳机', 1, 2, 2, 1),
(9, '平板', 1, 2, 3, 1),
(10, '智能手表', 1, 2, 4, 1),
(11, 'MacBook', 2, 2, 1, 1),
(12, '显示器', 2, 2, 2, 1),
(13, '配件', 2, 2, 3, 1),
(14, '男装', 3, 2, 1, 1),
(15, '女装', 3, 2, 2, 1),
(16, '鞋靴', 3, 2, 3, 1),
(17, '护肤品', 4, 2, 1, 1),
(18, '彩妆', 4, 2, 2, 1),
(19, '香水', 4, 2, 3, 1),
(20, '运动装备', 5, 2, 1, 1),
(21, '健身器材', 5, 2, 2, 1),
(22, '户外用品', 5, 2, 3, 1),
(23, '小说', 6, 2, 1, 1),
(24, '经管', 6, 2, 2, 1),
(25, '科技', 6, 2, 3, 1);

INSERT INTO pms_product (id, category_id, name, subtitle, main_image, detail, price, stock, sales, publish_status, new_status, recommend_status) VALUES
(1, 7, 'iPhone 15 Pro', 'A17 Pro', 'img', 'iPhone 15 Pro', 7999.00, 100, 500, 1, 1, 1),
(2, 7, 'iPhone 15', 'A16', 'img', 'iPhone 15', 5999.00, 150, 800, 1, 1, 1),
(3, 7, 'Huawei Mate 60 Pro', 'Kirin 9000S', 'img', 'Huawei Mate 60 Pro', 6999.00, 80, 300, 1, 1, 1),
(4, 7, 'Xiaomi 14', 'Snapdragon 8 Gen3', 'img', 'Xiaomi 14', 3999.00, 120, 400, 1, 1, 1),
(5, 8, 'AirPods Pro 2', 'H2 Chip', 'img', 'AirPods Pro 2', 1899.00, 200, 800, 1, 0, 1),
(6, 8, 'Sony WH-1000XM5', 'Noise Cancelling', 'img', 'Sony WH-1000XM5', 2999.00, 60, 200, 1, 0, 1),
(7, 9, 'iPad Pro 12.9', 'M4 Chip', 'img', 'iPad Pro 12.9', 9299.00, 50, 150, 1, 1, 1),
(8, 10, 'Apple Watch S9', 'S9 Chip', 'img', 'Apple Watch S9', 3199.00, 100, 300, 1, 1, 1),
(9, 11, 'MacBook Pro 14', 'M3 Pro', 'img', 'MacBook Pro 14', 14999.00, 50, 150, 1, 1, 1),
(10, 11, 'MacBook Air M3', 'M3', 'img', 'MacBook Air M3', 9499.00, 70, 250, 1, 0, 1),
(11, 12, 'LG 4K Monitor', 'Nano IPS', 'img', 'LG 27 4K Monitor', 3499.00, 40, 100, 1, 0, 0),
(12, 13, 'Magic Keyboard', 'Apple', 'img', 'Magic Keyboard', 849.00, 100, 200, 1, 0, 1),
(13, 14, 'Nike Air Force 1', 'Sneakers', 'img', 'Nike Air Force 1', 799.00, 100, 300, 1, 0, 1),
(14, 18, 'MAC Lipstick', 'Chili', 'img', 'MAC Lipstick Chili', 250.00, 200, 500, 1, 0, 1),
(15, 23, 'Three Body Problem', 'Book', 'img', 'Three Body Problem', 93.00, 200, 500, 1, 0, 1);

INSERT INTO pms_product_sku (id, product_id, sku_code, sku_name, attributes, price, stock, sort) VALUES
(1, 1, 'IP15P-128G-BL', 'iPhone 15 Pro 128GB Blue', '{"color":"Blue","storage":"128GB"}', 7999.00, 30, 1),
(2, 1, 'IP15P-256G-BL', 'iPhone 15 Pro 256GB Blue', '{"color":"Blue","storage":"256GB"}', 8999.00, 25, 2),
(3, 9, 'MBP14-M3-18G-512', 'MacBook Pro 14 M3 Pro 18GB 512GB', '{"chip":"M3 Pro","memory":"18GB","storage":"512GB"}', 14999.00, 20, 1),
(4, 8, 'AW-S9-41-GPS', 'Apple Watch S9 41mm GPS', '{"size":"41mm","version":"GPS"}', 3199.00, 30, 1);
