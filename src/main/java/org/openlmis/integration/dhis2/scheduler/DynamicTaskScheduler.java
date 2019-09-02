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

import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.openlmis.integration.dhis2.domain.Configuration;
import org.openlmis.integration.dhis2.domain.ConfigurationAuthenticationDetails;
import org.openlmis.integration.dhis2.domain.Integration;
import org.openlmis.integration.dhis2.repository.IntegrationRepository;
import org.openlmis.integration.dhis2.service.PayloadService;
import org.openlmis.integration.dhis2.web.PayloadMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class DynamicTaskScheduler implements SchedulingConfigurer {

  private static Logger LOGGER = LoggerFactory.getLogger(DynamicTaskScheduler.class);
  private ScheduledTaskRegistrar newTaskRegistrar;
  @Autowired
  private PayloadService payloadService;

  @Autowired
  private IntegrationRepository integrationRepository;

  /**
   * Creates new poolScheduler.
   */
  @Bean
  private TaskScheduler poolScheduler() {
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
    List<Integration> integrationList = integrationRepository.findAll();

    //  for testing <start>
    ConfigurationAuthenticationDetails confa1 = new ConfigurationAuthenticationDetails(
        "usrname", "paswd");
    ConfigurationAuthenticationDetails confa2 = new ConfigurationAuthenticationDetails(
        UUID.randomUUID().toString());

    Configuration conf1 = new Configuration("name1", "https://ae7b4d39-c556-484e-a168-4098a9adec21.mock.pstmn.io", confa1);
    Configuration conf2 = new Configuration("name1", "https://ae7b4d39-c556-484e-a168-4098a9adec21.mock.pstmn.io", confa2);

    Integration object1 = new Integration("Name", UUID.randomUUID(),
        "* * * * * ?",  conf1);
    Integration object2 = new Integration("Name", UUID.randomUUID(),
        "0/2 * * * * ?", conf2);
    Integration object5 = new Integration("Name", UUID.randomUUID(),
        "0/5 * * * * ?", conf1);

    integrationList.add(object1);
    integrationList.add(object2);
    integrationList.add(object5);
    // </end>

    for (Integration integration : integrationList) {
      CronTrigger croneTrigger = new CronTrigger(integration.getCronExpression(),
          TimeZone.getDefault());
      newTaskRegistrar.addCronTask(new CronTask(() -> scheduleCron(integration), croneTrigger));
    }
  }

  /**
   * Place for init tasks.
   */

  private void scheduleCron(Integration integration) {
    // println only for testing
    LOGGER.info("Scheduled task named: " + integration.getName());
    System.out.println("Next execution time of this taken from cron expression -> "
        + integration.getCronExpression());

    PayloadMap payloadMap = new PayloadMap();
    payloadMap.setIntegration(integration);
    payloadMap.setManualExecution(false);
    payloadMap.setPeriodId(UUID.randomUUID());
    // enable when postPayload is ready.
    payloadService.postPayload(payloadMap);
    if (integration.getCronExpression().equals("0/5 * * * * ?")) {
      //just for testing
      cancelAllTask();
    }
  }

  /**
   * Delete all existing Task.
   */
  private void cancelAllTask() {
    newTaskRegistrar.destroy();
    System.out.println("Cancel all tasks");
  }

}
