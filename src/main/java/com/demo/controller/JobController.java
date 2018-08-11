package com.demo.controller;

import com.demo.domain.JobAndTrigger;
import com.demo.job.BaseJob;
import com.demo.service.JobService;
import com.github.pagehelper.PageInfo;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Franky on 2018/08/10
 */
@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    @Qualifier("scheduler")
    private Scheduler scheduler;

    @PostMapping(value="/addjob")
    public void addjob(@RequestParam(value="jobClassName")String jobName,
                       @RequestParam(value="jobClassName")String jobClassName,
                       @RequestParam(value="jobGroupName")String jobGroupName,
                       @RequestParam(value="jobClassName")String triggerName,
                       @RequestParam(value="jobGroupName")String triggerGroupName,
                       @RequestParam(value="cronExpression")String cronExpression) throws Exception
    {
        addJobByCronTrigger(jobName,jobClassName, jobGroupName,triggerName,triggerGroupName, cronExpression);
//        addJobBySimpleTrigger(jobName,jobClassName, jobGroupName,triggerName,triggerGroupName);
    }

    /**
     * 简单定时任务
     * @param jobClassName
     * @param jobGroupName
     */
    private void addJobBySimpleTrigger(String jobName,String jobClassName, String jobGroupName,
                                        String triggerName,String triggerGroupName) {
        try {
            //构建jobDataMap
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("Trigger","Simple");

            //构建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(getClass(jobClassName))
                    .setJobData(jobDataMap).withIdentity(jobName,jobGroupName).build();

            //构建schedulerBuilder
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(10).repeatForever();

            //构建trigger
            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName,triggerGroupName)
//                    .endAt(new Date(System.currentTimeMillis()+60000))
                    .withSchedule(simpleScheduleBuilder).build();
            scheduler.scheduleJob(jobDetail,simpleTrigger);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Cron表达式的定时任务
     * @param jobClassName
     * @param jobGroupName
     * @param cronExpression
     */
    private void addJobByCronTrigger(String jobName,String jobClassName, String jobGroupName,
                                     String triggerName,String triggerGroupName, String cronExpression) {

        try {
            //构建jobDataMap
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("Trigger","Cron");

            //构建job信息
            JobDetail jobdetail = JobBuilder.newJob(getClass(jobClassName)).setJobData(jobDataMap)
                    .withIdentity(jobName,jobGroupName).build();

            //表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder schedulerBuilder =CronScheduleBuilder.cronSchedule(cronExpression);

            //构建trigger
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName,triggerGroupName)
                    .withSchedule(schedulerBuilder).build();
            //执行任务
            scheduler.scheduleJob(jobdetail,cronTrigger);
            //启动调度器
            scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value="/queryjob")
    public Map<String, Object> queryjob(@RequestParam(value="pageNum")Integer pageNum, @RequestParam(value="pageSize")Integer pageSize)
    {
        PageInfo<JobAndTrigger> jobAndTrigger = jobService.getJobAndTriggerDetails(pageNum, pageSize);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("JobAndTrigger", jobAndTrigger);
        map.put("number", jobAndTrigger.getTotal());
        return map;
    }

    /**
     * 暂停
     * @param jobName
     * @param jobGroupName
     * @throws Exception
     */
    @PostMapping(value="/pausejob")
    public void pausejob(@RequestParam(value="jobClassName")String jobName, @RequestParam(value="jobGroupName")String jobGroupName) throws Exception
    {
        jobPause(jobName, jobGroupName);
    }

    private void jobPause(String jobName, String jobGroupName) throws Exception
    {
        scheduler.pauseJob(JobKey.jobKey(jobName, jobGroupName));
    }

    /**
     * 恢复任务
     * @param jobName
     * @param jobGroupName
     * @throws Exception
     */
    @PostMapping(value="/resumejob")
    public void resumejob(@RequestParam(value="jobClassName")String jobName, @RequestParam(value="jobGroupName")String jobGroupName) throws Exception
    {
        jobresume(jobName, jobGroupName);
    }

    private void jobresume(String jobName, String jobGroupName) throws Exception
    {
        scheduler.resumeJob(JobKey.jobKey(jobName, jobGroupName));
    }


    /**
     *
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param cronExpression
     * @throws Exception
     */
    @PostMapping(value="/reschedulejob")
    public void rescheduleJob(@RequestParam(value="jobClassName")String jobName,
                              @RequestParam(value="jobGroupName")String jobGroupName,
                              @RequestParam(value="jobClassName")String triggerName,
                              @RequestParam(value="jobGroupName")String triggerGroupName,
                              @RequestParam(value="cronExpression")String cronExpression) throws Exception
    {
        jobreschedule(jobName, jobGroupName,triggerName,triggerGroupName,cronExpression);
    }

    private void jobreschedule(String jobName, String jobGroupName,
                               String triggerName,String triggerGroupName,String cronExpression) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName,triggerGroupName);
            CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (cronTrigger == null) {
                return;
            }
            cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName,triggerGroupName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();
            scheduler.rescheduleJob(triggerKey, cronTrigger);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除任务
     * @param jobName
     * @param jobGroupName
     * @throws Exception
     */
    @PostMapping(value="/deletejob")
    public void deletejob(@RequestParam(value="jobClassName")String jobName, @RequestParam(value="jobGroupName")String jobGroupName) throws Exception
    {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroupName));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroupName));
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
    }

    /**
     * 根据类名 获取class对象
     * @param className
     * @return
     */
    private Class<BaseJob> getClass(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class clazz = Class.forName(className);
        Object obj = clazz.newInstance();
        if (obj instanceof BaseJob){
            return (Class<BaseJob>)clazz;
        }
        throw new RuntimeException("不是BaseJob类型");
    }
}
