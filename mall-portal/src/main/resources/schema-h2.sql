DROP TABLE IF EXISTS ums_member_level;
DROP TABLE IF EXISTS ums_member;
DROP TABLE IF EXISTS pms_product_category;
DROP TABLE IF EXISTS pms_product_sku;
DROP TABLE IF EXISTS pms_product;
DROP TABLE IF EXISTS cms_cart_item;
DROP TABLE IF EXISTS oms_order_item;
DROP TABLE IF EXISTS oms_order;

CREATE TABLE ums_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_level_id BIGINT DEFAULT 1,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    nickname VARCHAR(100) DEFAULT '',
    phone VARCHAR(20) DEFAULT '',
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    icon VARCHAR(500) DEFAULT '',
    gender TINYINT DEFAULT 0,
    birthday DATE DEFAULT NULL,
    city VARCHAR(100) DEFAULT '',
    job VARCHAR(100) DEFAULT '',
    personalized_signature VARCHAR(500) DEFAULT '',
    source_type TINYINT DEFAULT 0,
    integration INT DEFAULT 0,
    growth INT DEFAULT 0,
    luckey_count INT DEFAULT 0,
    history_integration INT DEFAULT 0
);

CREATE TABLE ums_member_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    growth_point INT DEFAULT 0,
    default_status TINYINT DEFAULT 0,
    free_freight_point DECIMAL(10,2) DEFAULT 0,
    comment_integral INT DEFAULT 0,
    privilege_free_freight TINYINT DEFAULT 0,
    privilege_sign_in TINYINT DEFAULT 0,
    privilege_comment TINYINT DEFAULT 0,
    privilege_promotion TINYINT DEFAULT 0,
    privilege_member_price TINYINT DEFAULT 0,
    privilege_birthday TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pms_product_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    level TINYINT DEFAULT 1,
    sort INT DEFAULT 0,
    icon VARCHAR(500) DEFAULT '',
    show_status TINYINT DEFAULT 1,
    product_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pms_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT DEFAULT 0,
    brand_id BIGINT DEFAULT 0,
    name VARCHAR(500) NOT NULL,
    subtitle VARCHAR(500) DEFAULT '',
    main_image VARCHAR(500) DEFAULT '',
    sub_images TEXT DEFAULT '',
    detail CLOB DEFAULT '',
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock INT DEFAULT 0,
    low_stock INT DEFAULT 10,
    sales INT DEFAULT 0,
    sort INT DEFAULT 0,
    publish_status TINYINT DEFAULT 1,
    new_status TINYINT DEFAULT 0,
    recommend_status TINYINT DEFAULT 0,
    keywords VARCHAR(500) DEFAULT '',
    note VARCHAR(500) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pms_product_sku (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(100) NOT NULL,
    sku_name VARCHAR(200) NOT NULL,
    attributes VARCHAR(500) DEFAULT '',
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock INT DEFAULT 0,
    image VARCHAR(500) DEFAULT '',
    sort INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_sku_product_id ON pms_product_sku(product_id);

CREATE TABLE cms_cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_sku_id BIGINT DEFAULT NULL,
    product_name VARCHAR(500) NOT NULL,
    product_image VARCHAR(500) DEFAULT '',
    product_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    quantity INT NOT NULL DEFAULT 1,
    sku_attributes VARCHAR(500) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delete_status TINYINT DEFAULT 0
);

CREATE INDEX idx_cart_item_member_id ON cms_cart_item(member_id);

CREATE TABLE oms_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_sn VARCHAR(100) NOT NULL UNIQUE,
    member_id BIGINT NOT NULL,
    member_username VARCHAR(100) DEFAULT '',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    pay_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    pay_status TINYINT DEFAULT 0,
    pay_type VARCHAR(20) DEFAULT '',
    status TINYINT DEFAULT 0,
    receiver_name VARCHAR(100) DEFAULT '',
    receiver_phone VARCHAR(20) DEFAULT '',
    receiver_province VARCHAR(100) DEFAULT '',
    receiver_city VARCHAR(100) DEFAULT '',
    receiver_district VARCHAR(100) DEFAULT '',
    receiver_detail_address VARCHAR(500) DEFAULT '',
    delivery_company VARCHAR(100) DEFAULT '',
    delivery_sn VARCHAR(100) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pay_time TIMESTAMP DEFAULT NULL,
    delivery_time TIMESTAMP DEFAULT NULL,
    receive_time TIMESTAMP DEFAULT NULL,
    cancel_time TIMESTAMP DEFAULT NULL
);

CREATE INDEX idx_order_member_id ON oms_order(member_id);

CREATE TABLE oms_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_sn VARCHAR(100) NOT NULL,
    product_id BIGINT NOT NULL,
    product_sku_id BIGINT DEFAULT NULL,
    product_name VARCHAR(500) NOT NULL,
    product_image VARCHAR(500) DEFAULT '',
    product_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    quantity INT NOT NULL DEFAULT 1,
    total_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_item_order_id ON oms_order_item(order_id);

DROP TABLE IF EXISTS ums_member_favorite_product;

CREATE TABLE ums_member_favorite_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(500) DEFAULT '',
    product_pic VARCHAR(500) DEFAULT '',
    product_price DECIMAL(10,2) DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_favorite_member_id ON ums_member_favorite_product(member_id);