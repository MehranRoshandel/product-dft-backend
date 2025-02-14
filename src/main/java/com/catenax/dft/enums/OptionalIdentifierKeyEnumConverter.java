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

package com.catenax.dft.enums;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class OptionalIdentifierKeyEnumConverter implements AttributeConverter<OptionalIdentifierKeyEnum, String> {

    @Override
    public String convertToDatabaseColumn(OptionalIdentifierKeyEnum optionalIdentifierKeyEnum) {
        if (optionalIdentifierKeyEnum == null) {
            return null;
        }
        return optionalIdentifierKeyEnum.getPrettyName();
    }

    @Override
    public OptionalIdentifierKeyEnum convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }

        return Stream.of(OptionalIdentifierKeyEnum.values())
                .filter(c -> c.getPrettyName().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
