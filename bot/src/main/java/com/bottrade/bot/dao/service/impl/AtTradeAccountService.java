package com.bottrade.bot.dao.service.impl;

import cn.hutool.core.date.DateUtil;
import com.bottrade.bot.dao.entity.AtTradeAccount;
import com.bottrade.bot.dao.mapper.AtTradeAccountMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bottrade.bot.utils.AtCacheConstants;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dayun
 * @since 2024-03-27
 */
@Service
public class AtTradeAccountService extends ServiceImpl<AtTradeAccountMapper, AtTradeAccount> {

    public String getAccountValKey(String... accountName) {//at:accountName:Val:yyyy-MM-dd  (系统设置或转化为美东时间)
        if (null == accountName || accountName.length == 0) {
            return AtCacheConstants.MAIN_ACCOUNT_VAL + DateUtil.formatDate(new Date());
        } else {
            return AtCacheConstants.ACCOUNT_VAL + accountName + ":" + DateUtil.formatDate(new Date());
        }
    }

}
