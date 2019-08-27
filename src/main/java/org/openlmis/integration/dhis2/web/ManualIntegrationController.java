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

import org.openlmis.integration.dhis2.service.PayloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Transactional
@RequestMapping("/api")
public class ManualIntegrationController {

  //  @Autowired
  //  private PayloadService payloadService;

  /**
   * This method is used to manual trigger Integration.
   *
   * @return returning some data - not precized yet
   */

  @PostMapping("manualIntegrationExecution")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ManualIntegrationDto manualIntegration(
      @RequestBody ManualIntegrationDto manualIntegrationDto) {
    PayloadMap payloadMap = new PayloadMap();
    payloadMap.setTargetUrl(manualIntegrationDto.getTargetUrl());
    payloadMap.setPeriodId(manualIntegrationDto.getPeriodId());
    payloadMap.setManualExecution(true);
    payloadMap.setProgramId(manualIntegrationDto.getProgramId());
    // uncomment bellow when postPayload is ready.
    //    payloadService.postPayload(payloadMap);
    return manualIntegrationDto;
  }
}
