/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */
package org.openlmis.integration.dhis2.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

@Service
public class TaskScheduler implements SchedulingConfigurer {

    private static Logger LOGGER = LoggerFactory.getLogger(TaskScheduler.class);
    private ScheduledTaskRegistrar newTaskRegistrar;
    private Set<String> cronExpresions = new HashSet<>();

    /**
     * Creates new poolScheduler.
     */
    @Bean
    public org.springframework.scheduling.TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }

    /**
     * Creates new task by cron expressions from DB.
     */

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        newTaskRegistrar = taskRegistrar;
        newTaskRegistrar.setScheduler(poolScheduler());
//        cronExpresions.add(schedulerRepository.findAll());

        // this is only for testing, in final version cronExpresions list will come from db
        cronExpresions.add("* * * * * ?");
        cronExpresions.add("0/2 * * * * ?");
        cronExpresions.add("0/3 * * * * ?");
        cronExpresions.add("0/4 * * * * ?");
        cronExpresions.add("0/5 * * * * ?");
        cronExpresions.add("0/6 * * * * ?");
        cronExpresions.add("0/7 * * * * ?");
        for (String cron: cronExpresions) {
                CronTrigger croneTrigger = new CronTrigger(cron, TimeZone.getDefault());
                newTaskRegistrar.addCronTask(new CronTask(() -> scheduleCron(cron), croneTrigger));
        }
    }

    /**
     * Place for init task
     */
    public void scheduleCron(String cron) {
        LOGGER.info("Next execution time of this taken from cron expression -> {}", cron);
        System.out.println("Next execution time of this taken from cron expression -> " + cron);
        if(cron == "0/7 * * * * ?"){  //just for testing
            cancelAllTask();
        }
    }

    /**
     * Delete all existing Task.
     */
    public void cancelAllTask() {
        newTaskRegistrar.destroy();
        System.out.println("Cancel all tasks");
    }

}
