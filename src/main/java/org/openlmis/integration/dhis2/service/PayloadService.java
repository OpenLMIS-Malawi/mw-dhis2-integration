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

package org.openlmis.integration.dhis2.service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.openlmis.integration.dhis2.domain.Execution;
import org.openlmis.integration.dhis2.domain.ExecutionResponse;
import org.openlmis.integration.dhis2.domain.Integration;
import org.openlmis.integration.dhis2.repository.ExecutionRepository;
import org.openlmis.integration.dhis2.web.BaseController;
import org.openlmis.integration.dhis2.web.FacilitiesDto;
import org.openlmis.integration.dhis2.web.PayloadDto;
import org.openlmis.integration.dhis2.web.PayloadMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PayloadService extends BaseController {

  private static Logger LOGGER = LoggerFactory.getLogger(PayloadService.class);

  @Autowired
  private ExecutionRepository executionRepository;

  /**
   * Method is responsible for sending payload to Interop layer. Response is a status (202, 500 or
   * 503), message and notificationsChannel.
   */

  public void postPayload(PayloadMap payloadMap) {
    RestTemplate restTemplate = new RestTemplate();
    PayloadDto payloadDto = new PayloadDto();
    Set<FacilitiesDto> facilities = new HashSet<>(); //get from repository

    Integration integration = payloadMap.getIntegration();

    payloadDto.setDescription("Some description here");
    payloadDto.setFacilities(facilities);
    payloadDto.setReportingPeriod(payloadMap.getPeriodId().toString());

    ResponseEntity response =
        restTemplate.postForEntity(integration.getTargetUrl(),
            payloadDto,
        String.class);
    int status = response.getStatusCodeValue();

    UUID facilityId = payloadMap.getFacilityId();
    UUID processingPeriodId = payloadMap.getPeriodId();

    Clock clock = Clock.systemUTC();
    Execution execution;
    if (payloadMap.isManualExecution()) {
      execution = Execution.forManualExecution(integration, facilityId,
          processingPeriodId, clock);
    } else {
      execution = Execution.forAutomaticExecution(integration, processingPeriodId, clock);
    }

    ExecutionResponse executionResponse = new ExecutionResponse(ZonedDateTime.now(), status,
        response.getBody().toString());
    execution.markAsDone(executionResponse, clock);
    executionRepository.save(execution);

    LOGGER.info("Response status: " + status + "; Message: "
        + response.getBody().toString());
    System.out.println("Response status: " + status + "; Message: "
        + response.getBody().toString());

  }
}
