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

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.ZonedDateTime;
import org.openlmis.integration.dhis2.domain.Execution;
import org.openlmis.integration.dhis2.domain.ExecutionResponse;
import org.openlmis.integration.dhis2.repository.ExecutionRepository;
import org.openlmis.integration.dhis2.service.referencedata.ProcessingPeriodDto;
import org.openlmis.integration.dhis2.service.referencedata.ProgramReferenceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class PayloadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PayloadService.class);
  private static final XLogger X_LOGGER = XLoggerFactory.getXLogger(PayloadService.class);

  @Autowired
  private ExecutionRepository executionRepository;

  @Autowired
  private ProgramReferenceDataService programReferenceDataService;

  @Autowired
  private PayloadBuilder payloadBuilder;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Clock clock;

  private RestTemplate restTemplate = new RestTemplate();

  /**
   * Method is responsible for sending payload to Interop layer. Response is a status (202, 500 or
   * 503), message and notificationsChannel.
   */
  @Async
  public void postPayload(PayloadRequest payloadRequest) {
    X_LOGGER.entry(payloadRequest);

    Profiler profiler = new Profiler("POST_PAYLOAD");
    profiler.setLogger(X_LOGGER);

    try {
      Execution execution = createExecution(payloadRequest, profiler);
      String requestBody = createRequestBody(payloadRequest, execution, profiler);
      ExecutionResponse response = sendRequestBody(payloadRequest, execution, requestBody, profiler);

      LOGGER.info("Response status: {}; Message: {}", response.getStatusCode(), response.getBody());
      X_LOGGER.exit();
    } catch (Exception exp) {
      IllegalStateException illegalStateException = new IllegalStateException(exp);

      X_LOGGER.throwing(illegalStateException);
      throw illegalStateException;
    } finally {
      profiler.stop().log();
    }
  }

  private Execution createExecution(PayloadRequest payloadRequest, Profiler profiler) {
    profiler.start("CREATE_EXECUTION");
    Execution execution = payloadRequest.createExecution(clock);

    profiler.start("SAVE_TO_DB");
    executionRepository.saveAndFlush(execution);

    return execution;
  }

  private String createRequestBody(PayloadRequest payloadRequest, Execution execution,
      Profiler profiler) throws JsonProcessingException {
    profiler.start("CREATE_PAYLOAD");
    Payload payload = createPayload(payloadRequest);

    profiler.start("CONVERT_PAYLOAD_TO_JSON");
    String requestBody = objectMapper.writeValueAsString(payload);

    profiler.start("SET_REQUEST_BODY");
    execution.setRequestBody(requestBody);

    profiler.start("UPDATE_EXECUTION");
    executionRepository.saveAndFlush(execution);

    return requestBody;
  }

  private Payload createPayload(PayloadRequest request) {
    String programName = getProgramName(request);
    ProcessingPeriodDto period = request.getPeriod();

    return payloadBuilder
        .build(period.getStartDate(), period.getEndDate(), programName, request.getFacilityId());
  }

  private String getProgramName(PayloadRequest request) {
    if (null == request.getProgramId()) {
      return null;
    }

    return programReferenceDataService
        .findOne(request.getProgramId())
        .getName();
  }

  private ExecutionResponse sendRequestBody(PayloadRequest payloadRequest, Execution execution,
      String requestBody, Profiler profiler) {
    profiler.start("SEND_PAYLOAD");
    ExecutionResponse response = sendPayload(payloadRequest, requestBody);

    profiler.start("MARK_AS_DONE");
    execution.markAsDone(response, clock);

    profiler.start("UPDATE_EXECUTION");
    executionRepository.saveAndFlush(execution);

    return response;
  }

  private ExecutionResponse sendPayload(PayloadRequest request, String body) {
    try {
      RequestHeaders headers = setHeaders(request);
      HttpEntity<String> entity = RequestHelper.createEntity(headers, body);

      ResponseEntity<String> response = restTemplate
          .exchange(request.getTargetUrl(), HttpMethod.POST, entity, String.class);

      return new ExecutionResponse(ZonedDateTime.now(clock), response.getStatusCodeValue(),
          response.getBody());
    } catch (RestClientResponseException exp) {
      return new ExecutionResponse(ZonedDateTime.now(clock), exp.getRawStatusCode(),
          exp.getResponseBodyAsString());
    } catch (Exception exp) {
      return new ExecutionResponse(ZonedDateTime.now(clock), INTERNAL_SERVER_ERROR.value(),
          exp.getMessage());
    }
  }

  private RequestHeaders setHeaders(PayloadRequest request) {
    return RequestHeaders
        .init()
        .set(HttpHeaders.AUTHORIZATION, request.getAuthorizationHeader())
        .set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
  }

}
