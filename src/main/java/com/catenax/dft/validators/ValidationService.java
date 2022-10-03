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

package com.catenax.dft.validators;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.catenax.dft.entities.UsagePolicy;
import com.catenax.dft.enums.PolicyAccessEnum;

@Service
public class ValidationService {

    public boolean isValid(List<UsagePolicy> usagePolicies) {
        if(!CollectionUtils.isEmpty(usagePolicies))
        {
            boolean validateFlag = false;
            for(UsagePolicy usagePolicy : usagePolicies) {
                switch (usagePolicy.getType()) {
                    case DURATION:
                        validateFlag = validateDuration(usagePolicy);
                        break;
                    default:
                        validateFlag = validatePolicy(usagePolicy);
                        break;
                }
                if(!validateFlag){
                    break;
                }
            }
            return validateFlag;
        }
       return true;
    }

	private boolean validatePolicy(UsagePolicy usagePolicy) {

		boolean isValid = false;
		if (usagePolicy.getTypeOfAccess().equals(PolicyAccessEnum.UNRESTRICTED)
				|| (usagePolicy.getTypeOfAccess().equals(PolicyAccessEnum.RESTRICTED)
						&& StringUtils.isNotBlank(usagePolicy.getValue()))) {
			isValid = true;
		}

		return isValid;
	}

	private boolean validateDuration(UsagePolicy usagePolicy) {

		boolean isValid = false;
		if (usagePolicy.getTypeOfAccess().equals(PolicyAccessEnum.UNRESTRICTED)
				|| usagePolicy.getTypeOfAccess().equals(PolicyAccessEnum.RESTRICTED)
						&& StringUtils.isNotBlank(usagePolicy.getValue()) && usagePolicy.getDurationUnit() != null) {
			isValid = true;
		}
		return isValid;
	}
}
