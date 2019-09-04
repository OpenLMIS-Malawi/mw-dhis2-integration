package org.openlmis.integration.dhis2.web;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
public final class ExecutionResponseDto extends BaseDto implements ExecutionResponse.Exporter {

  private UUID id;
  private ZonedDateTime responseDate;
  private int statusCode;
  private String body;

  /**
   * Creates new instance based on domain object.
   */

  public static ExecutionResponseDto newInstance(ExecutionResponse executionResponse) {
    ExecutionResponseDto dto = new ExecutionResponseDto();
    executionResponse.export(dto);
    return dto;
  }

}