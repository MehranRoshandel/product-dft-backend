/********************************************************************************
 * Copyright (c) 2022 Critical TechWorks GmbH
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

package com.catenax.dft.entities.aspectrelationship;

import java.util.List;

import com.catenax.dft.entities.UsagePolicy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class AspectRelationshipRequest {
    @JsonIgnore
    private int rowNumber;
    @JsonIgnore
    private String processId;
    @JsonIgnore
    private List<String> bpnNumbers;
    @JsonIgnore
    private String typeOfAccess;
    @JsonIgnore
    private List<UsagePolicy> usagePolicies;

    @JsonProperty(value = "uuid")
    private String childUuid;

    @JsonProperty(value = "parent_uuid")
    private String parentUuid;

    @JsonProperty(value = "parent_part_instance_id")
    private String parentPartInstanceId;

    @JsonProperty(value = "parent_manufacturer_part_id")
    private String parentManufacturerPartId;

    @JsonProperty(value = "parent_optional_identifier_key")
    private String parentOptionalIdentifierKey;

    @JsonProperty(value = "parent_optional_identifier_value")
    private String parentOptionalIdentifierValue;

    @JsonProperty(value = "part_instance_id")
    private String childPartInstanceId;

    @JsonProperty(value = "manufacturer_part_id")
    private String childManufacturerPartId;

    @JsonProperty(value = "optional_identifier_key")
    private String childOptionalIdentifierKey;

    @JsonProperty(value = "optional_identifier_value")
    private String childOptionalIdentifierValue;

    @JsonProperty(value = "lifecycle_context")
    private String lifecycleContext;

    @JsonProperty(value = "quantity_number")
    private String quantityNumber;

    @JsonProperty(value = "measurement_unit_lexical_value")
    private String measurementUnitLexicalValue;

    @JsonProperty(value = "datatype_uri")
    private String dataTypeUri;

    @JsonProperty(value = "assembled_on")
    private String assembledOn;

}
