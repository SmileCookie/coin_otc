package com.redis;

import com.world.config.GlobalConfig;
import com.world.timer.T;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/7/13上午10:52
 */
public class RedisUtil {
    private final static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private static StringRedisTemplate stringRedisTemplate;
    private static HashOperations<String, String, String> stringHashOperations;

    private static HashOperations<String, String, Integer> stringHashOperationInteger;

    private static ValueOperations<String, String> stringValueOperations;
    private static SetOperations<String, String> stringSetOperations;
    private static ListOperations<String, String> stringListOperations;

    private static RedisTemplate<String, Object> redisTemplate;
    private static HashOperations<String, String, Object> hashOperations;
    private static ValueOperations<String, Object> valueOperations;
    private static SetOperations<String, Object> setOperations;
    private static ListOperations<String, Object> listOperations;

    static {
        RedisConnectionFactory factory =  createRedisConnectionFactory();//createLettuceConnectionFactory();
        stringRedisTemplate = createStringRedisTemplate(factory);
        stringHashOperations = stringRedisTemplate.opsForHash();
        stringHashOperationInteger = stringRedisTemplate.opsForHash();
        stringValueOperations = stringRedisTemplate.opsForValue();
        stringSetOperations = stringRedisTemplate.opsForSet();
        stringListOperations = stringRedisTemplate.opsForList();

        redisTemplate = createRedisTemplate(factory);
        hashOperations = redisTemplate.opsForHash();
        valueOperations = redisTemplate.opsForValue();
        setOperations = redisTemplate.opsForSet();
        listOperations = redisTemplate.opsForList();
    }

    private static RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    private static StringRedisTemplate createStringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(factory);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    private static LettuceConnectionFactory createLettuceConnectionFactory() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(GlobalConfig.redisPoolMaxActive);
        poolConfig.setMaxIdle(GlobalConfig.redisPoolMaxIdle);
        poolConfig.setMinIdle(GlobalConfig.redisPoolMinIdle);
        poolConfig.setMaxWaitMillis(GlobalConfig.redisPoolMaxWait);

        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMinutes(60))
                .poolConfig(poolConfig).build();

        LettuceConnectionFactory connectionFactory;
        if (StringUtils.isNotEmpty(GlobalConfig.redisClusterNodes)) {
            Set<String> clusterNodes = new HashSet<>();
            StringTokenizer tokenizer = new StringTokenizer(GlobalConfig.redisClusterNodes, ",;");
            while (tokenizer.hasMoreElements()) {
                clusterNodes.add(tokenizer.nextToken());
            }
            RedisClusterConfiguration config = new RedisClusterConfiguration(clusterNodes);
            config.setPassword(RedisPassword.of(GlobalConfig.redisPwd));
            connectionFactory = new LettuceConnectionFactory(config, clientConfig);
        } else {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(GlobalConfig.redisHost);
            config.setPort(GlobalConfig.redisPort);
            config.setPassword(RedisPassword.of(GlobalConfig.redisPwd));
            connectionFactory = new LettuceConnectionFactory(config, clientConfig);
        }
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    private static RedisConnectionFactory createRedisConnectionFactory() {
        // 创建jedis池配置实例
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(GlobalConfig.redisPoolMaxActive);
        poolConfig.setMaxIdle(GlobalConfig.redisPoolMaxIdle);
        poolConfig.setMinIdle(GlobalConfig.redisPoolMinIdle);
        poolConfig.setMaxWaitMillis(GlobalConfig.redisPoolMaxWait);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder().usePooling().poolConfig(poolConfig).build();

        JedisConnectionFactory factory;
        if (StringUtils.isNotEmpty(GlobalConfig.redisClusterNodes)) {

            Set<String> clusterNodes = new HashSet<>();
            StringTokenizer tokenizer = new StringTokenizer(GlobalConfig.redisClusterNodes, ",;");
            while (tokenizer.hasMoreElements()) {
                clusterNodes.add(tokenizer.nextToken());
            }
            RedisClusterConfiguration config = new RedisClusterConfiguration(clusterNodes);
            config.setPassword(RedisPassword.of(GlobalConfig.redisPwd));

            factory = new JedisConnectionFactory(config, poolConfig);
        } else {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(GlobalConfig.redisHost);
            config.setPort(GlobalConfig.redisPort);
            config.setPassword(RedisPassword.of(GlobalConfig.redisPwd));
            factory = new JedisConnectionFactory(config, clientConfig);
        }

        factory.afterPropertiesSet();
        return factory;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        return stringValueOperations.get(key);
    }


    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Object getObject(String key) {
        return valueOperations.get(key);
    }

    /**
     * 设置缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间，0为不超时
     */
    public static void set(String key, String value, int expireSeconds) {
        if (expireSeconds != 0) {
            stringValueOperations.set(key, value, expireSeconds, TimeUnit.SECONDS);
        } else {
            stringValueOperations.set(key, value);
        }
    }

    /**
     * 设置缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间，0为不超时
     * @return
     */
    public static void setObject(String key, Object value, int expireSeconds) {
        if (expireSeconds != 0) {
            valueOperations.set(key, value, expireSeconds, TimeUnit.SECONDS);
        } else {
            valueOperations.set(key, value);
        }
    }

    /**
     * 设置缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间，0为不超时
     */
    public static void hset(String key, String field, String value, int expireSeconds) {
        stringHashOperations.put(key, field, value);
        if (expireSeconds != 0) {
            stringRedisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }



    /**
     * 获取缓存
     *
     * @param key           键
     */
    public static Map<String,String> hget(String key) {
        return (Map<String,String>)stringHashOperations.entries(key);
    }





    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public static List<String> getList(String key) {
        return stringListOperations.range(key, 0, -1);
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public static List<Object> getObjectList(String key) {
        return listOperations.range(key, 0, -1);
    }

    /**
     * 设置List缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间，0为不超时
     */
    public static void setList(String key, List<String> value, int expireSeconds) {
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.delete(key);
        }

        stringListOperations.rightPushAll(key, value);
        if (expireSeconds != 0) {
            stringRedisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置List缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间，0为不超时
     * @return
     */
    public static void setObjectList(String key, List<Object> value, int expireSeconds) {
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }

        listOperations.rightPushAll(key, value);
        if (expireSeconds != 0) {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static void listAdd(String key, String... value) {
        stringListOperations.rightPushAll(key, value);
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static void listObjectAdd(String key, Object... value) {
        listOperations.rightPushAll(key, value);
    }


    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @return
     */
    public static void listObjectAddList(String key, List list) {
        listOperations.rightPushAll(key, list);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Set<String> getSet(String key) {
        return stringSetOperations.members(key);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public static Set<Object> getObjectSet(String key) {
        return setOperations.members(key);
    }

    /**
     * 设置Set缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间，0为不超时
     * @return
     */
    public static void setSet(String key, Set<String> value, int expireSeconds) {
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.delete(key);
        }
        stringSetOperations.add(key, (String[]) value.toArray(new String[value.size()]));
        if (expireSeconds != 0) {
            stringRedisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置Set缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间，0为不超时
     * @return
     */
    public static void setObjectSet(String key, Set<Object> value, int expireSeconds) {
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        setOperations.add(key, value.toArray());
        if (expireSeconds != 0) {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static void setSetAdd(String key, String... value) {
        stringSetOperations.add(key, value);
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static void setSetObjectAdd(String key, Object... value) {
        setOperations.add(key, value);
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public static Map<String, String> getMap(String key) {
        return stringHashOperations.entries(key);
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public static Map<String, Object> getObjectMap(String key) {
        return hashOperations.entries(key);
    }

    /**
     * 设置Map缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间，0为不超时
     */
    public static void setMap(String key, Map<String, String> value, int expireSeconds) {
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.delete(key);
        }
        stringHashOperations.putAll(key, value);
        if (expireSeconds != 0) {
            stringRedisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置Map缓存
     *
     * @param key           键
     * @param value         值
     * @param expireSeconds 超时时间，0为不超时
     * @return
     */
    public static void setObjectMap(String key, Map<String, Object> value, int expireSeconds) {
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
        hashOperations.putAll(key, value);
        if (expireSeconds != 0) {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static void mapPut(String key, Map<String, String> value) {
        stringHashOperations.putAll(key, value);
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static void mapObjectPut(String key, Map<String, Object> value) {
        hashOperations.putAll(key, value);
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key 键
     * @return
     */
    public static void mapRemove(String key, String mapKey) {
        stringHashOperations.delete(key, mapKey);
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key 键
     * @return
     */
    public static void mapObjectRemove(String key, Object mapKey) {
        hashOperations.delete(key, mapKey);
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key 键
     * @return
     */
    public static boolean mapExists(String key, String mapKey) {
        return stringHashOperations.hasKey(key, mapKey);
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key 键
     * @return
     */
    public static boolean mapObjectExists(String key, String mapKey) {
        return hashOperations.hasKey(key, mapKey);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public static void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 缓存是否存在
     *
     * @param key 键
     * @return
     */
    public static boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 对某个键的值自增
     *
     * @param key          键
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static void setIncr(String key, int cacheSeconds) {
        stringValueOperations.increment(key, 1);
        if (cacheSeconds != 0) {
            stringRedisTemplate.expire(key, cacheSeconds, TimeUnit.SECONDS);
        }
    }
}
