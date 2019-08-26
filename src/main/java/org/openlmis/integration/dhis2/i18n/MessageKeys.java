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

package org.openlmis.integration.dhis2.i18n;

import java.util.Arrays;

public abstract class MessageKeys {

  private static final String DELIMITER = ".";

  private static final String SERVICE_PREFIX = "integration.dhis2";
  private static final String ERROR = "error";

  private static final String CONFIGURATION = "configuration";
  private static final String INTEGRATION = "integration";
  private static final String JAVERS = "javers";

  private static final String NAME = "name";
  private static final String TARGET_URL = "targetUrl";
  private static final String PROGRAM_ID = "programId";

  private static final String DUPLICATED = "duplicated";

  private static final String ERROR_PREFIX = join(SERVICE_PREFIX, ERROR);

  public static final String ERROR_CONFIGURATION_NAME_DUPLICATED =
      join(ERROR_PREFIX, CONFIGURATION, NAME, DUPLICATED);
  public static final String ERROR_CONFIGURATION_TARGET_URL_DUPLICATED =
      join(ERROR_PREFIX, CONFIGURATION, TARGET_URL, DUPLICATED);

  public static final String ERROR_INTEGRATION_NAME_DUPLICATED =
      join(ERROR_PREFIX, INTEGRATION, NAME, DUPLICATED);
  public static final String ERROR_INTEGRATION_PROGRAM_ID_DUPLICATED =
      join(ERROR_PREFIX, INTEGRATION, PROGRAM_ID, DUPLICATED);

  public static final String ERROR_JAVERS_EXISTING_ENTRY =
      join(ERROR_PREFIX, JAVERS, "entryAlreadyExists");

  private MessageKeys() {
    throw new UnsupportedOperationException();
  }

  private static String join(String... params) {
    return String.join(DELIMITER, Arrays.asList(params));
  }
}
