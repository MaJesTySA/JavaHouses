package com.inside.house.biz.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.inside.house.biz.mapper.HouseMapper;
import com.inside.house.common.model.*;
import com.inside.house.common.page.PageData;
import com.inside.house.common.page.PageParams;
import com.inside.house.common.utils.BeanHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseService {

    @Value("${file.prefix}")
    private String imgPrefix;

    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private AgencyService agencyService;
    @Autowired
    private MailService mailService;

    public PageData<House> queryHouse(House query, PageParams pageParams) {
        if (!Strings.isNullOrEmpty(query.getName())) {
            Community community = new Community();
            community.setName(query.getName());
            List<Community> communities = houseMapper.selectCommunity(community);
            if (!communities.isEmpty()) {
                query.setCommunityId(communities.get(0).getId());
            }
        }
        List<House> houses = queryAndSetImg(query, pageParams);
        Long count = houseMapper.selectPageCount(query);
        return PageData.buildPage(houses, count, pageParams.getPageSize(), pageParams.getPageNum());
    }

    private List<House> queryAndSetImg(House query, PageParams pageParams) {
        List<House> houses = houseMapper.selectPageHouses(query, pageParams);
        houses.forEach(h -> {
            h.setFirstImg(imgPrefix + h.getFirstImg());
            h.setImageList(h.getImageList().stream().map(img -> imgPrefix + img).collect(Collectors.toList()));
            h.setFloorPlanList(h.getFloorPlanList().stream().map(pic -> imgPrefix + pic).collect(Collectors.toList()));
        });
        return houses;
    }

    public House queryOneHouse(Long id) {
        House query = new House();
        query.setId(id);
        List<House> houses = queryAndSetImg(query, PageParams.build(1, 1));
        if (!houses.isEmpty())
            return houses.get(0);
        return null;
    }

    public void addUserMsg(UserMsg userMsg) {
        BeanHelper.onInsert(userMsg);
        houseMapper.insertUserMsg(userMsg);
        User agent = agencyService.getAgentDetail(userMsg.getAgentId());
        mailService.sendMail("来自用户" + userMsg.getEmail() + "的留言", userMsg.getMsg(), agent.getEmail());
    }

    public HouseUser getHouseUser(Long houseId) {
        return houseMapper.selectSaleHouseUser(houseId);
    }
}
