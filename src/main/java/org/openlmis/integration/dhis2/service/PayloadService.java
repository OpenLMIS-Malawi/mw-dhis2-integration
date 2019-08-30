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
    PayloadDto payload = new PayloadDto();
    Set<FacilitiesDto> facilities = new HashSet<>(); //get from repository

    payload.setDescription("Some description here");
    payload.setFacilities(facilities);
    payload.setReportingPeriod(payloadMap.getPeriodId().toString());

    ResponseEntity response = restTemplate.postForEntity(payloadMap.getTargetUrl(), payload,
        String.class);
    int status = response.getStatusCodeValue();


    //IntegrationDto integrationDto = new IntegrationDto(); //get from api/db
    //ConfigurationAuthenticationDetailsDto authenticationDetailsDto =
    //    new ConfigurationAuthenticationDetailsDto("6648df1f-542b-46b0-b66e-0709fc444cfe");
    //ConfigurationDto configurationDto = new ConfigurationDto("Name", payloadMap.getTargetUrl(),
    //authenticationDetailsDto);
    //    integrationDto.setConfiguration(configurationDto);


    UUID facilityId = payloadMap.getFacilityId();
    //    UUID facilityId = UUID.randomUUID();
    UUID processingPeriodId = payloadMap.getPeriodId();
    //    UUID processingPeriodId = UUID.randomUUID();
    Clock clock = Clock.systemUTC();
    Integration integration = new Integration();
    Execution execution = Execution.forManualExecution(integration, facilityId,
        processingPeriodId, clock);
    ExecutionResponse executionResponse = new ExecutionResponse(ZonedDateTime.now(), status,
        response.getBody().toString());
    execution.markAsDone(executionResponse, clock);
    executionRepository.save(execution);

    // save to DB here.
    LOGGER.info("Response status: " + status + "; Message: "
        + response.getBody().toString());
    System.out.println("Response status: " + status + "; Message: "
        + response.getBody().toString());

  }
}
