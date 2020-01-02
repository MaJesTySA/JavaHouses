package com.inside.house.biz.mapper;

import com.inside.house.common.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserMapper {
    List<User> selectUsers();
    int insert(User account);
    int delete(String email);

    int update(User updateUser);
}
