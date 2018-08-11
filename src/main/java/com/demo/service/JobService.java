package com.demo.service;

import com.demo.domain.JobAndTrigger;
import com.github.pagehelper.PageInfo;

/**
 * Created by Franky on 2018/08/10
 */
public interface JobService {

    PageInfo<JobAndTrigger> getJobAndTriggerDetails(Integer pageNum, Integer pageSize);
}
