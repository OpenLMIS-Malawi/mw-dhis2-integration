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

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.openlmis.integration.dhis2.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;




@Service
public class PayloadService extends BaseController {

  private static Logger LOGGER = LoggerFactory.getLogger(PayloadService.class);

  /**
   * Get data to send. Now it is hardcoded for development, in the future it will be replaced by
   * repository.
   */

  private PayloadDto getPayload() {
    ObjectMapper objectMapper = new ObjectMapper();
    PayloadDto payload = new PayloadDto();
    try {
      payload = objectMapper.readValue(new File("src/tb.json"), PayloadDto.class);
    } catch (IOException e) {
      payload.setDescription("Brak pliku");
      //e.printStackTrace();
    }
    return payload;
  }

  /**
   * Method is responsible for sending payload to Interop layer. Response is a status (202, 500 or
   * 503), message and notificationsChannel.
   */

  public void postPayload(String url) {
    RestTemplate restTemplate = new RestTemplate();
    String resourceUrl = "https://" + url + ".mock.pstmn.io"; // we have to add correct URL
    ResponseEntity response = restTemplate.postForEntity(resourceUrl, getPayload(),
        String.class);
    HttpStatus status = response.getStatusCode();
    response.getStatusCodeValue();
    ResponseBody responseBody = new ResponseBody();
    try {
      responseBody = new ObjectMapper().readValue(response.getBody().toString(),
          ResponseBody.class);
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
    }
    LOGGER.info("Response status: " + status.toString() + "; Message: "
        + responseBody.getMessage());
    System.out.println("Response status: " + status.toString() + "; Message: "
        + responseBody.getMessage());
  }
}
