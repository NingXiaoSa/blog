package com.example.blog.mapper;

import com.example.blog.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2021-03-03
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
