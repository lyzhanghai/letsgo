package com.umessage.letsgo.dao.security;

import com.umessage.letsgo.domain.po.security.WxTeamEntity;

import java.util.List;

/**
 * Created by admin on 2016/11/23.
 */
public interface WxTeamDao {

    int delete(Long id);

    int insert(WxTeamEntity wxTeamEntity);

    int update(WxTeamEntity wxTeamEntity);


    List<WxTeamEntity> selectWxTeamByUnionid(String unionid);


    /**
     * 根据微信唯一标识查找微信team 信息
     * @param unionID
     * @return
     */
    List<WxTeamEntity> selectWxTeamInfoListByUnionid(String unionID);
}
