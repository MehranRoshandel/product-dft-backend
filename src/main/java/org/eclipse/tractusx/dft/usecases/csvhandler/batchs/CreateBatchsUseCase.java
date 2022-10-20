/********************************************************************************
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.dft.usecases.csvhandler.batchs;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.eclipse.tractusx.dft.entities.SubmodelJsonRequest;
import org.eclipse.tractusx.dft.entities.batch.BatchRequest;
import org.eclipse.tractusx.dft.enums.CsvTypeEnum;
import org.eclipse.tractusx.dft.usecases.processreport.ProcessReportUseCase;
import org.springframework.stereotype.Service;

@Service
public class CreateBatchsUseCase {
    private final MapFromBatchRequestUseCase useCase;
    private final ProcessReportUseCase processReportUseCase;

    public CreateBatchsUseCase(MapFromBatchRequestUseCase useCase, ProcessReportUseCase processReportUseCase) {
        this.useCase = useCase;
        this.processReportUseCase = processReportUseCase;
    }

    public void createBatchs(SubmodelJsonRequest<BatchRequest> batchInputs, String processId) throws JsonProcessingException {
        List<BatchRequest> rowData = batchInputs.getRowData();
		processReportUseCase.startBuildProcessReport(processId, CsvTypeEnum.BATCH, rowData.size(),
                batchInputs.getBpnNumbers(), batchInputs.getTypeOfAccess(), batchInputs.getUsagePolicies());
      
		for(int i=0; i<rowData.size();i++){
            BatchRequest batch = rowData.get(i);
            batch.setRowNumber(i);
            batch.setProcessId(processId);
            batch.setBpnNumbers(batchInputs.getBpnNumbers());
            batch.setUsagePolicies(batchInputs.getUsagePolicies());
            useCase.run(batch, processId);
        }

        processReportUseCase.finishBuildBatchProgressReport(processId);
    }
}