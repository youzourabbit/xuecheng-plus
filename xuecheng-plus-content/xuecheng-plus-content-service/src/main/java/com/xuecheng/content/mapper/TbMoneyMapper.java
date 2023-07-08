package com.xuecheng.content.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

@Service
public interface TbMoneyMapper {
    @Update("update tb_money set money = money + #{money} where name = #{name}")
    void inMoney(@Param("name") String name, @Param("money") int money);

    @Update("update tb_money set money = money - #{money} where name = #{name}")
    void outMoney(@Param("name") String name, @Param("money") int money);
}
