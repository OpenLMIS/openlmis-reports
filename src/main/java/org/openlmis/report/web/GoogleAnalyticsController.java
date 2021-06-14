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

package org.openlmis.report.web;

import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import javax.transaction.Transactional;
import org.openlmis.report.dto.VisitorDto;
import org.openlmis.report.exception.BaseLocalizedException;
import org.openlmis.report.exception.ValidationMessageException;
import org.openlmis.report.i18n.MessageKeys;
import org.openlmis.report.service.GoogleAnalyticsService;
import org.openlmis.report.utils.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Transactional
@RequestMapping(value = "/api/report/googleAnalytics")
public class GoogleAnalyticsController extends BaseController {

  @Autowired
  private GoogleAnalyticsService googleAnalyticsService;

  /**
   * Retrieve OpenLMIS visitor report form Google Analytics account.
   * on specific period.
   *
   * @param startDate start date
   * @param endDate   end date
   * @return all visitors.
   */
  @GetMapping("/visitors")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<VisitorDto> getVisitorReport(@RequestParam(value = "startDate", required =
          false) String startDate, @RequestParam(value = "endDate", required = false)
          String endDate, Pageable pageable) throws BaseLocalizedException,
          GeneralSecurityException, IOException {

    if (startDate == null || endDate == null) {
      throw new ValidationMessageException(MessageKeys.ERROR_MISSING_MANDATORY_FIELD);
    }

    AnalyticsReporting service = googleAnalyticsService.initializeAnalyticsReporting();

    List<VisitorDto> visitorDtos = googleAnalyticsService.getVisitorsReport(service,
            startDate, endDate);
    return Pagination.getPage(visitorDtos, pageable, visitorDtos.size());
  }
}