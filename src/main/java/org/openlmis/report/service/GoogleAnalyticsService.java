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

package org.openlmis.report.service;

import static org.openlmis.report.i18n.ReportImageMessageKeys.ERROR_NOT_FOUND;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.Dimension;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openlmis.report.dto.VisitorDto;
import org.openlmis.report.dto.external.referencedata.UserDto;
import org.openlmis.report.exception.NotFoundMessageException;
import org.openlmis.report.service.referencedata.UserReferenceDataService;
import org.openlmis.report.utils.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleAnalyticsService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String APPLICATION_NAME = "Hello Analytics Reporting";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  private static final String KEY_FILE_LOCATION = "ga.json";

  @Value("${google.analytics.view.id}")
  private String viewId;

  @Value("${google.analytics.view.visitors.dimension}")
  private String visitorsReportDimension;

  @Value("${google.analytics.view.visitors.metric}")
  private String visitorsReportMetric;

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  private static ArrayList<VisitorDto> visitorDtos = new ArrayList<>();

  /**
   * Queries the Analytics Reporting API V4.
   *
   * @param service An authorized Analytics Reporting API V4 service object.
   * @param startDate A filter of start date.
   * @param endDate A filter of end date.
   * @return GetReportResponse The Analytics Reporting API V4 response.
   * @throws IOException IO Exception
   */
  public List<VisitorDto> getVisitorsReport(AnalyticsReporting service, String startDate,
                                            String endDate) throws IOException {
    DateRange dateRange = new DateRange();
    dateRange.setStartDate(startDate);
    dateRange.setEndDate(endDate);

    // Create the Metrics object.
    Metric sessions = new Metric()
            .setExpression(visitorsReportMetric);
    Dimension pageTitle = new Dimension().setName(visitorsReportDimension);

    // Create the ReportRequest object.
    ReportRequest request = new ReportRequest()
            .setViewId(viewId)
            .setDateRanges(Arrays.asList(dateRange))
            .setMetrics(java.util.Arrays.asList(sessions))
            .setDimensions(Arrays.asList(pageTitle));

    ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
    requests.add(request);

    // Create the GetReportsRequest object.
    GetReportsRequest getReport = new GetReportsRequest()
            .setReportRequests(requests);

    // Call the batchGet method.
    GetReportsResponse response = service.reports().batchGet(getReport).execute();


    return parseResponse(response);
  }

  /**
   * Initializes an Analytics Reporting API V4 service object.
   *
   * @return An authorized Analytics Reporting API V4 service object.
   * @throws IOException IO Exception
   * @throws GeneralSecurityException Security Exception
   */
  public AnalyticsReporting initializeAnalyticsReporting() throws GeneralSecurityException,
          IOException {

    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

    GoogleCredential credential = GoogleCredential
            .fromStream(this.getClass().getClassLoader().getResourceAsStream(KEY_FILE_LOCATION))
            .createScoped(AnalyticsReportingScopes.all());

    // Construct the Analytics Reporting service object.
    return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME).build();
  }

  /**
   * Parses and prints the Analytics Reporting API V4 response.
   *
   * @param List of VisitorDto .
   */
  private List<VisitorDto> parseResponse(GetReportsResponse response) {

    for (Report report : response.getReports()) {
      List<ReportRow> rows = report.getData().getRows();

      if (rows == null) {
        logger.info("No data found");
        new NotFoundMessageException(new Message(ERROR_NOT_FOUND));
      }

      List<UserDto> userDtos = userReferenceDataService.findAll();
      for (ReportRow row : rows) {
        UserDto userDto =
                userReferenceDataService.getUser(row.getDimensions().get(0),
                        userDtos);
        VisitorDto visitorDto = new VisitorDto();
        visitorDto.setPageView(row.getMetrics().get(0).getValues().get(0));
        if (userDto != null) {
          visitorDto.setUserDto(userDto);
        }
        visitorDto.setUsername(row.getDimensions().get(0));

        visitorDtos.add(visitorDto);
      }
    }

    return visitorDtos;
  }
}
