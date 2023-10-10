package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.QueryResult;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuPosterMapper spuPosterMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;


    /**
     * 获取一级分类列表
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        QueryWrapper<BaseCategory1> wrapper = new QueryWrapper<>();
        //添加排序条件
        wrapper.orderByAsc("id");
        List<BaseCategory1> baseCategory1List = baseCategory1Mapper.selectList(wrapper);
        return baseCategory1List;
    }

    /**
     * 获取二级分类列表
     *
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory2::getCategory1Id, category1Id);
        wrapper.orderByAsc(BaseCategory2::getId);
        List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(wrapper);
        return baseCategory2List;
    }

    /**
     * 获取三级分类列表
     *
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory3::getCategory2Id, category2Id);
        wrapper.orderByAsc(BaseCategory3::getId);
        List<BaseCategory3> baseCategory2List = baseCategory3Mapper.selectList(wrapper);
        return baseCategory2List;
    }

    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoList(category1Id, category2Id, category3Id);
    }

    /**
     * 1、涉及两张表
     * attr_info
     * attr_value
     * <p>
     * 2、两张表的关系一对多
     * <p>
     * 3、声明式事务
     * 异常的处理RuntimeException
     * IOException
     * SQLException
     *
     * @param baseAttrInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //判断id是否存在
        Long id = baseAttrInfo.getId();
        if (id != null) {
            //修改平台属性值
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {

            //新增baseAttrInfo
            baseAttrInfoMapper.insert(baseAttrInfo);

        }
        // 修改：通过先删除{baseAttrValue}，再新增的方式！
        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
        QueryWrapper queryWrapper = new QueryWrapper<BaseAttrValue>();
        queryWrapper.eq("attr_id", baseAttrInfo.getId());
        baseAttrValueMapper.delete(queryWrapper);
        //保存BaseAttrValue
        if (!CollectionUtils.isEmpty(valueList)) {
            for (BaseAttrValue baseAttrValue : valueList) {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);

            }
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        LambdaQueryWrapper<BaseAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseAttrValue::getAttrId, attrId);
        List<BaseAttrValue> valueList = baseAttrValueMapper.selectList(queryWrapper);
        return valueList;
    }

    /**
     * //admin/product/{page}/{limit}
     * //spu分页列表
     *
     * @param
     * @param
     * @return
     */
    @Override
    public IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> spuInfoPage, Long category3Id) {
        LambdaQueryWrapper<SpuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpuInfo::getCategory3Id, category3Id);
        queryWrapper.orderByDesc(SpuInfo::getId);
        Page<SpuInfo> spuInfoPageList = spuInfoMapper.selectPage(spuInfoPage, queryWrapper);
        return spuInfoPageList;
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);

    }

    /**
     * 保存spu
     * spu_info *
     * spu_poster *
     * spu_image *
     * spu_sale_attr_value
     * spu_sale_attr
     *
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
        Long spuId = spuInfo.getId();
        //spuImage
        List<SpuImage> imageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(imageList)) {
            for (SpuImage spuImage : imageList) {
                spuImage.setSpuId(spuId);
                spuImageMapper.insert(spuImage);
            }
        }
        //SpuPoster
        List<SpuPoster> posterList = spuInfo.getSpuPosterList();
        if (!CollectionUtils.isEmpty(posterList)) {
            for (SpuPoster spuPoster : posterList) {
                spuPoster.setSpuId(spuId);
                spuPosterMapper.insert(spuPoster);
            }
        }
        //SpuSaleAttr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuId);
                String saleAttrName = spuSaleAttr.getSaleAttrName();
                spuSaleAttrMapper.insert(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                    spuSaleAttrValue.setSaleAttrName(saleAttrName);
                    spuSaleAttrValue.setSpuId(spuId);
                    spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                }
            }
        }

    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.getSpuSaleAttrList(spuId);
        return spuSaleAttrList;
    }

    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(queryWrapper);
        return spuImageList;
    }

    /**
     * sku_ingo
     * sku_image
     * sku_sale_attr_value
     * sku_attr_value
     *
     * @param skuInfo
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();
        //sku图片
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (!CollectionUtils.isEmpty(skuImageList)) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuId);
                skuImageMapper.insert(skuImage);
            }
        }
        //销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
        //平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
    }

    @Override
    public IPage<SkuInfo> getSkuList(Page<SkuInfo> skuInfoPage) {
        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return skuInfoMapper.selectPage(skuInfoPage, queryWrapper);
    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }

    @Autowired
    private RedisTemplate redisTemplate;


    public SkuInfo getSkuInfoRedis(Long skuId) {
        try {
            //拼写redisKey
            String dataKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
            //先向redis中查找数据
            SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get(dataKey);

            //没有找到数据
            if (skuInfo == null) {
                String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                //获取锁
                Boolean flag = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, RedisConst.SKULOCK_EXPIRE_PX1, TimeUnit.SECONDS);
                if (flag) {
                    try {
                        //从数据库中获取数据
                        skuInfo = getSkuInfoDB(skuId);
                        if (skuInfo == null) {
                            skuInfo = new SkuInfo();
                            redisTemplate.opsForValue().set(dataKey, skuInfo, RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                        } else {
                            //设置随机时间，防止雪崩
                            Random random = new Random();
                            int randomInt = random.nextInt(100) + 1;
                            redisTemplate.opsForValue().set(dataKey, skuInfo, RedisConst.SKUKEY_TIMEOUT + randomInt, TimeUnit.SECONDS);
                            return skuInfo;
                        }
                    } finally {
                        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                        // 设置lua脚本返回的数据类型
                        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                        // 设置lua脚本返回类型为Long
                        redisScript.setResultType(Long.class);
                        redisScript.setScriptText(script);
                        redisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);

                    }

                } else {
                    try {
                        Thread.sleep(RedisConst.SKULOCK_EXPIRE_PX2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return getSkuInfo(skuId);
                }
            } else {
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //兜底方法
        return getSkuInfoDB(skuId);
    }

    @Override
    public SkuInfo getSkuInfo(Long skuId) {
//        return getSkuInfoRedis(skuId);
        return getSkuINfoRedisson(skuId);
    }

    @Autowired
    private RedissonClient redissonClient;

    private SkuInfo getSkuINfoRedisson(Long skuId) {
        try {

            String dataKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
            //先向redis中查找数据
            SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get(dataKey);
            if (skuInfo == null) {
                String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                //获取锁
                RLock lock = redissonClient.getLock(lockKey);
                boolean flag = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if (flag) {
                    try {
                        //从数据库中获取数据
                        skuInfo = getSkuInfoDB(skuId);
                        if (skuInfo == null) {
                            skuInfo = new SkuInfo();
                            redisTemplate.opsForValue().set(dataKey, skuInfo, RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                        } else {
                            //设置随机时间，防止雪崩
                            Random random = new Random();
                            int randomInt = random.nextInt(100) + 1;
                            redisTemplate.opsForValue().set(dataKey, skuInfo, RedisConst.SKUKEY_TIMEOUT + randomInt, TimeUnit.SECONDS);
                            return skuInfo;
                        }
                    } finally {
                        lock.unlock();
                    }
                } else {
                    Thread.sleep(100);
                    return getSkuINfoRedisson(skuId);
                }


            } else {
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getSkuInfoDB(skuId);
    }


    public SkuInfo getSkuInfoDB(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        QueryWrapper<SkuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId);
        List<SkuImage> skuImageList = skuImageMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(skuImageList)) {
            skuInfo.setSkuImageList(skuImageList);
        }
        return skuInfo;
    }

    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    @Override
    //不要去调用之前的方法，之前的数据放在缓存，而价格数据放在硬盘
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (null != skuInfo) {
            return skuInfo.getPrice();
        }
        return new BigDecimal("0");

    }

    @Override
    public List<SpuPoster> findSpuPosterBySpuId(Long spuId) {
        QueryWrapper<SpuPoster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuPoster> spuPosterList = spuPosterMapper.selectList(queryWrapper);
        return spuPosterList;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        HashMap<String, String> resultMap = new HashMap<>();
        List<Map> resultMapList = skuSaleAttrValueMapper.selectSkuValueIdsMap(spuId);
        if (!CollectionUtils.isEmpty(resultMapList)) {
            for (Map map : resultMapList) {
                resultMap.put(String.valueOf(map.get("value_ids")), String.valueOf(map.get("sku_id")));
            }
        }
        return resultMap;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoListBySkuId(skuId);
    }
}
