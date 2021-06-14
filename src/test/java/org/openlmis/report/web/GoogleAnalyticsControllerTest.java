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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.report.dto.VisitorDto;
import org.openlmis.report.dto.external.referencedata.UserDto;
import org.openlmis.report.exception.BaseLocalizedException;
import org.openlmis.report.service.GoogleAnalyticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.TooManyMethods"})
public class GoogleAnalyticsControllerTest {

  @Mock
  private Pageable pageable;

  @Mock
  private GoogleAnalyticsService service;

  @InjectMocks
  private final GoogleAnalyticsController controller = new GoogleAnalyticsController();


  private final VisitorDto visitorDto;

  private List<VisitorDto> visitorDtos;

  DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");


  /**
   * Constructor for test.
   */
  public GoogleAnalyticsControllerTest() {
    initMocks(this);

    visitorDto = new VisitorDto();
    visitorDto.setUsername("username");
    visitorDto.setPageView("12");
    visitorDto.setUserDto(new UserDto());

    visitorDtos = new ArrayList<>();
    visitorDtos.add(visitorDto);

  }


  @Test
  public void shouldGetVisitorReport() throws BaseLocalizedException,
          GeneralSecurityException, IOException {
    //given
    when(service.getVisitorsReport(any(), any(), any())).thenReturn(visitorDtos);

    //when
    Page<VisitorDto> visitorDtoPage = controller.getVisitorReport(dateFormat.format(new Date()),
            dateFormat.format(new Date()), pageable);

    //then
    assertThat(visitorDtoPage.getTotalElements())
            .isEqualTo(visitorDtos.size());
  }


}
