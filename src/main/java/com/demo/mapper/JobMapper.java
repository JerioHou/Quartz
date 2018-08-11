package com.demo.mapper;

import com.demo.domain.JobAndTrigger;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Franky on 2018/08/10
 */
@Component
public interface JobMapper {
    List<JobAndTrigger> getJobAndTriggerDetails();
}
