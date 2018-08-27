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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openlmis.report.domain.JasperTemplate;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.view.jasperreports.AbstractJasperReportsView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({JasperReportsViewService.class})
public class JasperReportsViewServiceTest {

  private JasperTemplate jasperTemplate;
  private HttpServletRequest httpServletRequest;
  private JasperReportsViewService jasperReportsViewService;

  @Before
  public void setUp() {
    jasperTemplate = mock(JasperTemplate.class);
    httpServletRequest = mock(HttpServletRequest.class);

    jasperReportsViewService = spy(new JasperReportsViewService());
  }

  @Test
  public void shouldAddFormatMappings() throws Exception {
    doReturn("/")
        .when(jasperReportsViewService, "getReportUrlForReportData", any(JasperTemplate.class));
    doReturn(null)
        .when(jasperReportsViewService, "getApplicationContext", any(HttpServletRequest.class));

    JasperReportsMultiFormatView report =
        jasperReportsViewService.getJasperReportsView(jasperTemplate, httpServletRequest);

    Map<String, Class<? extends AbstractJasperReportsView>> formatMappings =
        (Map) ReflectionTestUtils.getField(report, "formatMappings");

    assertEquals(5, formatMappings.size());
    assertTrue(formatMappings.containsKey("pdf"));
    assertTrue(formatMappings.containsKey("csv"));
    assertTrue(formatMappings.containsKey("html"));
    assertTrue(formatMappings.containsKey("xls"));
    assertTrue(formatMappings.containsKey("xlsx"));

    // Make sure the HTML View is coming from our code, not Spring's
    assertEquals(formatMappings.get("html"), JasperReportsHtmlView.class);
  }

}
