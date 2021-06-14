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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import guru.nidi.ramltester.junit.RamlMatchers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.openlmis.report.dto.PageDto;
import org.openlmis.report.dto.VisitorDto;
import org.openlmis.report.dto.external.referencedata.UserDto;
import org.openlmis.report.service.GoogleAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;




@SuppressWarnings({"PMD.TooManyMethods"})
public class GoogleAnalyticsControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/report/googleAnalytics";
  private static final String VISITORS = RESOURCE_URL + "/visitors";

  private VisitorDto visitorDto;

  private List<VisitorDto> visitorDtos;

  @Autowired
  private GoogleAnalyticsService service;

  /**
   * Constructor for test class.
   */
  public GoogleAnalyticsControllerIntegrationTest() {
    visitorDto = new VisitorDto();
    visitorDto.setUsername("username");
    visitorDto.setPageView("12");
    visitorDto.setUserDto(new UserDto());

    visitorDtos = new ArrayList<>();
    visitorDtos.add(visitorDto);
  }

  @Test
  public void shouldGetVisitorReport() throws IOException {

    given(service.getVisitorsReport(any(), any(), any()))
            .willReturn(visitorDtos);

    PageDto response = restAssured
            .given()
            .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
            .queryParam("startDate", new Date())
            .queryParam("endDate", new Date())
            .when()
            .get(VISITORS)
            .then()
            .statusCode(200)
            .extract().as(PageDto.class);

    assertNotNull(response);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }
}
