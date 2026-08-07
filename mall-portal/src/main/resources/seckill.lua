-- 秒杀原子扣减脚本（Redis 内单脚本串行执行，等价于原子操作）
-- KEYS[1] = 库存 key
-- KEYS[2] = 已购用户集合 key
-- ARGV[1] = 用户ID
-- 返回: 1 成功 / 0 售罄 / 2 已参与 / -1 未初始化
local stock = tonumber(redis.call('GET', KEYS[1]))
if stock == nil then
    return -1
end
if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
    return 2
end
if stock <= 0 then
    return 0
end
redis.call('DECR', KEYS[1])
redis.call('SADD', KEYS[2], ARGV[1])
return 1
