package org.openlmis.integration.dhis2.web;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.codehaus.jackson.annotate.JsonSetter;
import org.openlmis.integration.dhis2.domain.Execution;
import org.openlmis.integration.dhis2.domain.ExecutionResponse;


/**
 * Model of ExecutionDto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ExecutionDto extends BaseDto implements Execution.Exporter {

  private boolean manualExecution;
  private UUID programId;
  private UUID facilityId;
  private UUID processingPeriodId;
  private String targetUrl;
  private ZonedDateTime startDate;
  private ZonedDateTime endDate;
  private ExecutionResponseDto response;

  /**
   * Creates new instance based on domain object.
   */

  public static ExecutionDto newInstance(Execution execution) {
    ExecutionDto dto = new ExecutionDto();
    execution.export(dto);
    return dto;
  }

  @JsonSetter("ExecutionResponseDto")
  public void setResponse(ExecutionResponseDto executionResponseDto) {
    this.response = executionResponseDto;
  }

  @Override
  public void setResponse(ExecutionResponse response) {
    this.response = new ExecutionResponseDto();
    response.export(this.response);
  }
}