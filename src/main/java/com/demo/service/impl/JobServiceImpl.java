package com.demo.service.impl;

import com.demo.domain.JobAndTrigger;
import com.demo.mapper.JobMapper;
import com.demo.service.JobService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Franky on 2018/08/10
 */
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobMapper jobMapper;

    @Override
    public PageInfo<JobAndTrigger> getJobAndTriggerDetails(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<JobAndTrigger> list = jobMapper.getJobAndTriggerDetails();
        PageInfo<JobAndTrigger> jobAndTriggerPageInfo = new PageInfo<>(list);
        return jobAndTriggerPageInfo;
    }
}
