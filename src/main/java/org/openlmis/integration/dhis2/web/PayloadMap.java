package org.openlmis.integration.dhis2.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayloadMap {

  private String targetUrl;
  private String programId;
  private String facilityId;
  private String periodId;
  private String configurationId;
  private String cronExpression;
  private boolean manualExecution;


}
