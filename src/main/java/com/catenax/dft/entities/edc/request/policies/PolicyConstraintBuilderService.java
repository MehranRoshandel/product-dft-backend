/********************************************************************************
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

package com.catenax.dft.entities.edc.request.policies;

import com.catenax.dft.entities.UsagePolicy;
import com.catenax.dft.entities.edc.request.policies.accesspolicy.AccessPolicyDTO;
import com.catenax.dft.entities.edc.request.policies.usagepolicy.DurationPolicyDTO;
import com.catenax.dft.entities.edc.request.policies.usagepolicy.PurposePolicyDTO;
import com.catenax.dft.entities.edc.request.policies.usagepolicy.RolePolicyDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyConstraintBuilderService {

    public List<ConstraintRequest> getAccessConstraints(List<String> bpnNumbers) {
        List<ConstraintRequest> constraints = new ArrayList<>();

        if (bpnNumbers != null && !bpnNumbers.isEmpty()) {
            AccessPolicyDTO accessPolicy = AccessPolicyDTO.builder().bpnNumbers(bpnNumbers).build();
            constraints.add(accessPolicy.toConstraint());
        }
        return constraints;
    }

    public List<ConstraintRequest> getUsagePolicyConstraints(List<UsagePolicy> usagePolicies) {
        List<ConstraintRequest> usageConstraintList = new ArrayList<>();
        if (usagePolicies != null && !usagePolicies.isEmpty()) {
            usagePolicies.stream().forEach(policy ->
            {
                usagePolicy(usageConstraintList, policy);
            });
        }
        return usageConstraintList;
    }

    private void usagePolicy(List<ConstraintRequest> usageConstraintList, UsagePolicy policy) {
        switch (policy.getType()) {
            case DURATION:
                ConstraintRequest request = DurationPolicyDTO.fromUsagePolicy(policy).toConstraint();
                if (request != null) {
                    usageConstraintList.add(request);
                }
                break;
            case PURPOSE:
                ConstraintRequest purposeRequest = PurposePolicyDTO.fromUsagePolicy(policy).toConstraint();
                if (purposeRequest != null) {
                    usageConstraintList.add(purposeRequest);
                }
                break;
            case ROLE:
                ConstraintRequest roleRequest = RolePolicyDTO.fromUsagePolicy(policy).toConstraint();
                if (roleRequest != null) {
                    usageConstraintList.add(roleRequest);
                }
                break;
            default:
                break;
        }
    }
}
