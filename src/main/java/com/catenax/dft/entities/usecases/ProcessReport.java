/********************************************************************************
 * Copyright (c) 2022 Critical TechWorks GmbH
 * Copyright (c) 2022 BMW GmbH
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package com.catenax.dft.entities.usecases;

import java.time.LocalDateTime;
import java.util.List;

import com.catenax.dft.enums.CsvTypeEnum;
import com.catenax.dft.enums.ProgressStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessReport {

    private String processId;
    private CsvTypeEnum csvType;
    private int numberOfItems;
    private int numberOfFailedItems;
    private int numberOfSucceededItems;
    private ProgressStatusEnum status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> bpnNumbers;
    private String typeOfAccess;
    private String usagePolicies;

}
