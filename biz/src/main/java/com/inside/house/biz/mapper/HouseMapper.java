package com.inside.house.biz.mapper;

import com.inside.house.common.model.Community;
import com.inside.house.common.model.House;
import com.inside.house.common.model.HouseUser;
import com.inside.house.common.model.UserMsg;
import com.inside.house.common.page.PageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface HouseMapper {
    List<House> selectPageHouses(@Param("house") House house, @Param("pageParams") PageParams pageParams);
    Long selectPageCount(@Param("house") House query);
    List<Community> selectCommunity(Community community);

    int insertUserMsg(UserMsg userMsg);

    HouseUser selectSaleHouseUser(@Param("id") Long houseId);
//    int insert(User account);

}
