package com.inside.house.web.controller;

import com.inside.house.biz.service.AgencyService;
import com.inside.house.biz.service.HouseService;
import com.inside.house.common.model.House;
import com.inside.house.common.page.PageData;
import com.inside.house.common.page.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HouseController {
    @Autowired
    private HouseService houseService;
    @Autowired
    private AgencyService agencyService;
    @RequestMapping("house/list")
    public String houseList(Integer pageSize, Integer pageNum, House query, ModelMap modelMap) {
        PageData<House> ps = houseService.queryHouse(query, PageParams.build(pageSize, pageNum));
        modelMap.put("ps", ps);
        modelMap.put("vo", query);
        return "house/listing";
    }

    @RequestMapping("house/detail")
    public String houseDetail(Long id, ModelMap modelMap) {
        House house = houseService.queryOneHouse(id);
        if (house.getUserId() != null && house.getUserId().equals(0)) {
            modelMap.put("agent", agencyService.getAgentDetail(house.getUserId()));
        }
        modelMap.put("house", house);
        return "/house/detail";
    }

}
