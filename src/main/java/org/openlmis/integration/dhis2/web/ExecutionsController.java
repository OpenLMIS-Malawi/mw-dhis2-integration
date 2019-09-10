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

package org.openlmis.integration.dhis2.web;

import static org.openlmis.integration.dhis2.web.ExecutionsController.RESOURCE_PATH;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.openlmis.integration.dhis2.domain.Execution;
import org.openlmis.integration.dhis2.domain.Integration;
import org.openlmis.integration.dhis2.exception.NotFoundException;
import org.openlmis.integration.dhis2.i18n.MessageKeys;
import org.openlmis.integration.dhis2.repository.ExecutionRepository;
import org.openlmis.integration.dhis2.repository.IntegrationRepository;
import org.openlmis.integration.dhis2.service.PayloadRequest;
import org.openlmis.integration.dhis2.service.PayloadService;
import org.openlmis.integration.dhis2.service.referencedata.PeriodReferenceDataService;
import org.openlmis.integration.dhis2.service.referencedata.ProcessingPeriodDto;
import org.openlmis.integration.dhis2.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Transactional
@RestController
@RequestMapping(RESOURCE_PATH)
public class ExecutionsController extends BaseController {

  public static final String RESOURCE_PATH = API_PATH + "/integrationExecutions";
  public static final String ID_URL = "/{id}";

  @Autowired
  private PayloadService payloadService;

  @Autowired
  private IntegrationRepository integrationRepository;

  @Autowired
  private PeriodReferenceDataService periodReferenceDataService;

  @Autowired
  private ExecutionRepository executionRepository;

  /**
   * This method is used to manual trigger Integration.
   *
   * @return returning some data - not precized yet
   */

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ManualIntegrationDto manualIntegration(
      @RequestBody ManualIntegrationDto manualIntegrationDto) {

    Integration integration = integrationRepository.findByProgramId(
        manualIntegrationDto.getProgramId());

    ProcessingPeriodDto period = periodReferenceDataService
        .findOne(manualIntegrationDto.getPeriodId());

    PayloadRequest payloadRequest = PayloadRequest.forManualExecution(integration,
        manualIntegrationDto.getFacilityId(), period);

    payloadService.postPayload(payloadRequest);

    return manualIntegrationDto;
  }

  /**
   * Retrieves all historical executions. Note that an empty collection rather than a 404 should be
   * returned if no historical executions exist.
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<ExecutionDto> getAllHistoricalExecutions(Pageable pageable) {
    Page<Execution> page = executionRepository.findAll(pageable);
    List<ExecutionDto> content = page
        .getContent()
        .stream()
        .map(ExecutionDto::newInstance)
        .collect(Collectors.toList());
    return Pagination.getPage(content, pageable, page.getTotalElements());
  }

  /**
   * Retrieves the historical execution.
   */
  @GetMapping(value = ID_URL)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ExecutionDto getSpecifiedHisoricalExecution(@PathVariable("id") UUID id) {
    Execution execution = executionRepository.findOne(id);

    if (execution == null) {
      throw new NotFoundException(MessageKeys.ERROR_EXECUTION_NOT_FOUND);
    }
    return ExecutionDto.newInstance(execution);
  }
}