package com.inside.house.biz.mapper;

import com.inside.house.common.model.User;
import com.inside.house.common.page.PageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface AgencyMapper {

    List<User> selectAgent(@Param("user")User user, @Param("pageParams")PageParams pageParams);
}
