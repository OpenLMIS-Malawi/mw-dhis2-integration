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
import java.util.stream.Collectors;

import org.openlmis.integration.dhis2.domain.Integration;
import org.openlmis.integration.dhis2.repository.IntegrationRepository;
import org.openlmis.integration.dhis2.util.Pagination;
import org.openlmis.integration.dhis2.web.ConfigurationAuthenticationDetailsDto;
import org.openlmis.integration.dhis2.web.ConfigurationDto;
import org.openlmis.integration.dhis2.web.IntegrationDto;
import org.openlmis.integration.dhis2.web.PayloadMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

  private Pageable pageable = new PageRequest(Pagination.DEFAULT_PAGE_NUMBER, 2000);
  private static Logger LOGGER = LoggerFactory.getLogger(DynamicTaskScheduler.class);
  private ScheduledTaskRegistrar newTaskRegistrar;
  //  @Autowired
  //  private PayloadService payloadService;

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

    Page<Integration> page = integrationRepository.findAllWithoutSnapshots(pageable);
    List<IntegrationDto> content = page
        .getContent()
        .stream()
        .map(IntegrationDto::newInstance)
        .collect(Collectors.toList());

    //  for testing <start>
    ConfigurationAuthenticationDetailsDto confa1 = new ConfigurationAuthenticationDetailsDto(
        "usrname", "paswd");
    ConfigurationAuthenticationDetailsDto confa2 = new ConfigurationAuthenticationDetailsDto(
        UUID.randomUUID().toString());

    ConfigurationDto conf1 = new ConfigurationDto("name1", "tarteg url", confa1);
    ConfigurationDto conf2 = new ConfigurationDto("name1", "tarteg url", confa2);

    IntegrationDto object1 = new IntegrationDto("Name", UUID.randomUUID(),
        "* * * * * ?",  conf1);
    IntegrationDto object2 = new IntegrationDto("Name", UUID.randomUUID(),
        "0/2 * * * * ?", conf2);
    IntegrationDto object5 = new IntegrationDto("Name", UUID.randomUUID(),
        "0/5 * * * * ?", conf1);

    content.add(object1);
    content.add(object2);
    content.add(object5);
    // </end>

    for (IntegrationDto integrationDto : content) {
      CronTrigger croneTrigger = new CronTrigger(integrationDto.getCronExpression(),
          TimeZone.getDefault());
      newTaskRegistrar.addCronTask(new CronTask(() -> scheduleCron(integrationDto), croneTrigger));
    }
  }

  /**
   * Place for init task.
   */
  private void scheduleCron(IntegrationDto integrationDto) {
    // println only for testing
    System.out.println("Next execution time of this taken from cron expression -> "
        + integrationDto.getCronExpression());

    ConfigurationDto configurationDto =
        integrationDto.getConfiguration();

    PayloadMap payloadMap = new PayloadMap();
    payloadMap.setTargetUrl(configurationDto.getTargetUrl());
    payloadMap.setProgramId(integrationDto.getProgramId());
    payloadMap.setConfigurationId(configurationDto.getId());
    payloadMap.setManualExecution(false);

    // enable when postPayload is ready.
    // payloadService.postPayload(payloadMap);
    if (integrationDto.getCronExpression().equals("0/5 * * * * ?")) {
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
