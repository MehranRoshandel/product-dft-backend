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

package com.catenax.dft.service;

import com.catenax.dft.api.ConnectorDiscoveryApi;
import com.catenax.dft.api.ContractOfferCatalogApi;
import com.catenax.dft.api.LegalEntityDataApi;
import com.catenax.dft.api.model.connector.ConnectorInfo;
import com.catenax.dft.entities.UsagePolicy;
import com.catenax.dft.entities.database.ContractNegotiationInfoEntity;
import com.catenax.dft.entities.edc.request.policies.ConstraintRequest;
import com.catenax.dft.entities.edc.request.policies.PolicyConstraintBuilderService;
import com.catenax.dft.enums.NegotiationState;
import com.catenax.dft.enums.PolicyAccessEnum;
import com.catenax.dft.enums.UsagePolicyEnum;
import com.catenax.dft.facilitator.AbstractEDCStepsHelper;
import com.catenax.dft.facilitator.ContractNegotiateManagement;
import com.catenax.dft.gateways.database.ContractNegotiationInfoRepository;
import com.catenax.dft.mapper.EDCAssetConstant;
import com.catenax.dft.model.asset.Asset;
import com.catenax.dft.model.contractnegotiation.ContractAgreementResponse;
import com.catenax.dft.model.contractnegotiation.ContractNegotiationDto;
import com.catenax.dft.model.contractnegotiation.ContractNegotiationsResponse;
import com.catenax.dft.model.contractoffers.ContractOffer;
import com.catenax.dft.model.contractoffers.ContractOffersCatalogResponse;
import com.catenax.dft.model.legalEntity.LegalEntityData;
import com.catenax.dft.model.policies.PolicyDefinition;
import com.catenax.dft.model.request.ConsumerRequest;
import com.catenax.dft.model.response.LegalEntityResponse;
import com.catenax.dft.model.response.QueryDataOfferModel;
import com.catenax.dft.util.KeycloakUtil;
import com.catenax.dft.util.UtilityFunctions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class ConsumerControlPanelService extends AbstractEDCStepsHelper {

    private static final Integer LIMIT = 10000;
    private final String edcDataUri;
    private final ContractOfferCatalogApi contractOfferCatalogApiProxy;
    private final ContractNegotiateManagement contractNegotiateManagement;

    private ContractNegotiationInfoRepository contractNegotiationInfoRepository;
    private PolicyConstraintBuilderService policyConstraintBuilderService;

    private LegalEntityDataApi legalEntityDataApi;
    private ConnectorDiscoveryApi connectorDiscoveryApi;

    private KeycloakUtil keycloakUtil;


    @Autowired
    public ConsumerControlPanelService(@Value("${edc.consumer.datauri}") String edcDataUri,
                                       ContractOfferCatalogApi contractOfferCatalogApiProxy, ContractNegotiateManagement contractNegotiateManagement, ContractNegotiationInfoRepository contractNegotiationInfoRepository, PolicyConstraintBuilderService policyConstraintBuilderService,
                                       LegalEntityDataApi legalEntityDataApi,
                                       ConnectorDiscoveryApi connectorDiscoveryApi,
                                       KeycloakUtil keycloakUtil) {
        this.edcDataUri = edcDataUri;
        this.contractOfferCatalogApiProxy = contractOfferCatalogApiProxy;
        this.contractNegotiateManagement = contractNegotiateManagement;
        this.contractNegotiationInfoRepository = contractNegotiationInfoRepository;
        this.policyConstraintBuilderService = policyConstraintBuilderService;
        this.legalEntityDataApi = legalEntityDataApi;
        this.connectorDiscoveryApi = connectorDiscoveryApi;
        this.keycloakUtil = keycloakUtil;

    }

    public List<QueryDataOfferModel> queryOnDataOffers(String providerUrl) {
        providerUrl = UtilityFunctions.removeLastSlashOfUrl(providerUrl);

        providerUrl += edcDataUri;

        List<QueryDataOfferModel> queryOfferResponse = new ArrayList<>();

        ContractOffersCatalogResponse contractOfferCatalog = contractOfferCatalogApiProxy.getContractOffersCatalog(
                getAuthHeader(),
                providerUrl, LIMIT);

        for (ContractOffer contractOffer : contractOfferCatalog.getContractOffers()) {
            Asset asset = contractOffer.getAsset();
            PolicyDefinition policy = contractOffer.getPolicy();

            //Populating usage policies response based on usage policy constraints
            List<UsagePolicy> usagePolicies = new ArrayList<>();
            policy.getPermissions().stream().forEach(permission -> {
                usagePolicies.addAll(UtilityFunctions.getUsagePolicies(permission.getConstraints().stream()));
            });

            UtilityFunctions.addCustomUsagePolicy(policy.getExtensibleProperties(), usagePolicies);
            //Later to be part of access policy
            List<String> bpnNumbers = new ArrayList<>();
            policy.getPermissions().stream().forEach(permission -> {
                permission.getConstraints().stream().forEach(constraint -> {
                    if (constraint.getLeftExpression().getValue().equals("BusinessPartnerNumber")) {
                        String value = constraint.getRightExpression().getValue().toString();
                        bpnNumbers.addAll(Arrays.asList(value.trim().substring(value.indexOf("[") + 1, value.indexOf("]")).split(",")));
                        return;
                    }
                });
            });

            queryOfferResponse.add(QueryDataOfferModel.builder().assetId(getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_ID))
                    .connectorOfferUrl(providerUrl + File.separator + getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_ID))
                    .offerId(contractOffer.getId())
                    .title(getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_NAME))
                    .description(getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_DESCRIPTION))
                    .created(getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_CREATED))
                    .modified(getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_MODIFIED))
                    .publisher(getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_PUBLISHER))
                    .typeOfAccess(!bpnNumbers.isEmpty() ? PolicyAccessEnum.RESTRICTED : PolicyAccessEnum.UNRESTRICTED)
                    .version(getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_VERSION))
                    .bpnNumbers(bpnNumbers)
                    .usagePolicies(usagePolicies)
                    .fileName(getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_FILENAME))
                    .fileContentType(getFieldFromAsset(asset, EDCAssetConstant.ASSET_PROP_CONTENTTYPE))
                    .connectorId(contractOfferCatalog.getId())
                    .build()
            );
        }
        return queryOfferResponse;
    }

    private String getFieldFromAsset(Asset asset, String field) {
        return asset.getProperties().getOrDefault(field, "");
    }

    @Async
    public void subscribeDataOffers(ConsumerRequest consumerRequest, String processId) {
        HashMap<String, String> extensibleProperty = new HashMap<>();
        String recipient = UtilityFunctions.removeLastSlashOfUrl(consumerRequest.getProviderUrl());
        AtomicReference<String> negotiateContractId = new AtomicReference<>();
        AtomicReference<ContractNegotiationsResponse> checkContractNegotiationStatus = new AtomicReference<>();
        var recipientURL = recipient + edcDataUri;
        List<UsagePolicy> policies = consumerRequest.getPolicies();
        UsagePolicy customPolicy = policies.stream().filter(type -> type.getType().equals(UsagePolicyEnum.CUSTOM)).findFirst().get();
        if (StringUtils.isNotBlank(customPolicy.getValue())) {
            extensibleProperty.put(customPolicy.getType().name(), customPolicy.getValue());
        }
        List<ConstraintRequest> constraintRequests = policyConstraintBuilderService.getUsagePolicyConstraints(policies);
        consumerRequest.getOffers().parallelStream().forEach((offer) -> {
            try {

                negotiateContractId.set(contractNegotiateManagement.negotiateContract(offer.getOfferId(),
                        recipientURL, offer.getAssetId(), constraintRequests, extensibleProperty));
                int retry = 3;
                int counter = 1;

                do {
                    Thread.sleep(3000);
                    checkContractNegotiationStatus.set(contractNegotiateManagement
                            .checkContractNegotiationStatus(negotiateContractId.get()));
                    counter++;
                } while (checkContractNegotiationStatus.get() != null && !checkContractNegotiationStatus.get().getState()
                        .equals("CONFIRMED") && !checkContractNegotiationStatus.get().getState().equals("DECLINED") && counter <= retry);


            } catch (Exception e) {
                log.error("Exception in subscribeDataOffers" + e.getMessage());
            } finally {
                // Local DB entry
                ContractNegotiationInfoEntity contractNegotiationInfoEntity = ContractNegotiationInfoEntity.builder()
                        .processId(processId)
                        .connectorId(consumerRequest.getConnectorId())
                        .offerId(offer.getOfferId())
                        .contractNegotiationId(negotiateContractId != null ? negotiateContractId.get() : null)
                        .status(checkContractNegotiationStatus.get() != null ? checkContractNegotiationStatus.get().getState() : "Failed:Exception")
                        .dateTime(LocalDateTime.now()).build();
                contractNegotiationInfoRepository.save(contractNegotiationInfoEntity);
            }
        });

    }

    public List<ContractAgreementResponse> getAllContractOffers(Integer limit, Integer offset) {
        List<ContractAgreementResponse> contractAgreementResponses = new ArrayList<>();
        List<ContractNegotiationDto> contractNegotiationDtoList = contractNegotiateManagement.getAllContractNegotiations(limit, offset);
        contractNegotiationDtoList.stream().forEach((contract) ->
                {
                    if (contract.getState().equals(NegotiationState.CONFIRMED.name())) {
                        String negotiationId = contract.getId();
                        if (StringUtils.isNotBlank(contract.getContractAgreementId())) {
                            ContractAgreementResponse agreementResponse = contractNegotiateManagement.getAgreementBasedOnNegotiationId(negotiationId);
                            agreementResponse.setCounterPartyAddress(contract.getCounterPartyAddress());
                            agreementResponse.setDateCreated(contract.getCreatedAt());
                            agreementResponse.setDateUpdated(contract.getUpdatedAt());
                            contractAgreementResponses.add(agreementResponse);
                        }
                    } else {
                        ContractAgreementResponse agreementResponse = ContractAgreementResponse.builder().contractAgreementId(StringUtils.EMPTY).organizationName(StringUtils.EMPTY)
                                .title(StringUtils.EMPTY).negotiationId(contract.getId()).state(contract.getState())
                                .contractAgreementInfo(null).counterPartyAddress(contract.getCounterPartyAddress())
                                .dateCreated(contract.getCreatedAt()).dateUpdated(contract.getUpdatedAt()).build();
                        contractAgreementResponses.add(agreementResponse);
                    }
                }
        );
        return contractAgreementResponses;
    }

    public ResponseEntity<LegalEntityResponse[]> fetchLegalEntitiesData(String searchText, Integer page, Integer size) {
        ResponseEntity<LegalEntityData> apiResponse = legalEntityDataApi.fetchLegalEntityData(searchText, page, size, UtilityFunctions.getAuthToken());
        LegalEntityData legalEntity = apiResponse.getBody();
        LegalEntityResponse[] legalEntitiesResponse = new LegalEntityResponse[legalEntity != null ? legalEntity.content.size() : 0];
        if (null != legalEntity) {
            final int[] counter = {0};
            legalEntity.getContent().stream().forEach(companyData -> {
                companyData.getLegalEntity().getNames().stream().forEach(name -> {
                    LegalEntityResponse legalEntityResponse = LegalEntityResponse.builder().bpn(companyData.getLegalEntity().getBpn()).name(name.getValue()).build();
                    legalEntitiesResponse[counter[0]] = legalEntityResponse;
                    counter[0]++;
                });
            });
        }
        return new ResponseEntity(legalEntitiesResponse, apiResponse.getStatusCode());
    }

    public ResponseEntity<ConnectorInfo[]> fetchConnectorInfo(String[] bpns) {
        String token = keycloakUtil.getKeycloakToken();
        return connectorDiscoveryApi.fetchConnectorInfo(bpns, "Bearer " + token);
    }
}
