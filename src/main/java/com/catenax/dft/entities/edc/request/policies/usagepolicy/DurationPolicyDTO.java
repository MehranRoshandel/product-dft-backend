/********************************************************************************
 * Copyright (c) 2022 T-Systems International Gmbh
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

package com.catenax.dft.entities.edc.request.policies.usagepolicy;

import com.catenax.dft.entities.UsagePolicy;
import com.catenax.dft.entities.edc.request.policies.ConstraintRequest;
import com.catenax.dft.entities.edc.request.policies.Expression;
import com.catenax.dft.enums.DurationEnum;
import com.catenax.dft.enums.PolicyAccessEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DurationPolicyDTO extends UsagePolicyDTO{
    private static final String DATASPACECONNECTOR_LITERALEXPRESSION = "dataspaceconnector:literalexpression";
    private DurationEnum durationUnit;

    public static DurationPolicyDTO fromUsagePolicy(UsagePolicy usagePolicy)
    {
        return DurationPolicyDTO.builder().type(usagePolicy.getType()).typeOfAccess(usagePolicy.getTypeOfAccess())
                .value(usagePolicy.getValue()).durationUnit(usagePolicy.getDurationUnit()).build();

    }
    @Override
    public ConstraintRequest toConstraint() {
        if (getTypeOfAccess().equals(PolicyAccessEnum.RESTRICTED)) {
            Expression lExpression = Expression.builder()
                    .edcType(DATASPACECONNECTOR_LITERALEXPRESSION)
                    .value("idsc:ELAPSED_TIME")
                    .build();

            String operator = "LEQ";
            Expression rExpression = null;
            String value = "P";
            switch (this.durationUnit) {
                case YEAR:
                    value = value + getValue()+ "Y0M0DT0H0M0S";
                    break;
                case MONTH:
                    value = value + "0Y" + getValue() + "M0DT0H0M0S";
                    break;
                case DAY:
                    value = value + "0Y0M" + getValue() + "DT0H0M0S";
                    break;
                case HOUR:
                    value = value + "0Y0M0DT" + getValue() + "H0M0S";
                    break;
                case MINUTE:
                    value = value + "0Y0M0DT0H" + getValue() + "M0S";
                    break;
                case SECOND:
                    value = value + "0Y0M0DT0H0M" + getValue() + "S";
                    break;
                default:
                    break;
            }
            rExpression = Expression.builder()
                    .edcType(DATASPACECONNECTOR_LITERALEXPRESSION)
                    .value(value)
                    .build();

            return ConstraintRequest.builder().edcType("AtomicConstraint")
                    .leftExpression(lExpression)
                    .rightExpression(rExpression)
                    .operator(operator)
                    .build();

        }

        return null;
    }
}
