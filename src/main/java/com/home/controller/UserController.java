package com.home.controller;

import com.home.framework.stereotype.LQDAutowired;
import com.home.framework.stereotype.LQDController;
import com.home.framework.stereotype.LQDRequestMapping;
import com.home.framework.stereotype.LQDRequestParam;
import com.home.framework.web.servlet.LqdModelAndView;
import com.home.pojo.UserEntity;
import com.home.service.UserService;

import java.util.HashMap;
import java.util.Map;

@LQDController
public class UserController {

    @LQDAutowired
    private UserService userService;

    @LQDRequestMapping("/getById.json")
    public LqdModelAndView getById(@LQDRequestParam("id") String id) {
        UserEntity user = userService.getById(id);
        LqdModelAndView mv = new LqdModelAndView();
        Map<String, Object> model = new HashMap<>();
        model.put("id", user.getId());
        model.put("date", user.getDate().toLocaleString());
        mv.setModel(model);
        mv.setViewName("first");
        return mv;
    }

    @LQDRequestMapping("/error.json")
    public LqdModelAndView getError(@LQDRequestParam("id") String id) {
        throw new RuntimeException("自定义异常");
    }
}
