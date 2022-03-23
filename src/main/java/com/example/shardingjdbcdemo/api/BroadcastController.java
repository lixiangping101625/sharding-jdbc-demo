package com.example.shardingjdbcdemo.api;

import com.example.shardingjdbcdemo.dao.GenderInfoMapper;
import com.example.shardingjdbcdemo.pojo.GenderInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lixiangping
 * @createTime 2022年03月23日 20:36
 * @decription: Sharding-jdbc 分库分表广播表测试
 */
@RestController
@RequestMapping("/gender_info")
public class BroadcastController {

    @Resource
    private GenderInfoMapper genderInfoMapper;

    /**
     * 向广播表添加数据
     * @param genderInfo
     * @return
     */
    @PostMapping("/add")
    public String add(@RequestBody GenderInfo genderInfo){
        int i = genderInfoMapper.insert(genderInfo);
        if (i > 0) {
            return "添加成功";
        }
        return "添加失败";
    }

    /**
     * 查询广播表数据详情
     * @param genderInfoId
     * @return
     */
    @GetMapping("/detail/{genderInfoId}")
    public GenderInfo add(@PathVariable("genderInfoId") Integer genderInfoId){
        GenderInfo genderInfo = new GenderInfo();
        genderInfo.setId(genderInfoId);
        List<GenderInfo> list = genderInfoMapper.select(genderInfo);
        System.out.println("查询到记录数：" + list.size());

        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
