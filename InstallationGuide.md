# Installation Guide
## Product DFT
Install from the command line:

`docker pull ghcr.io/catenax-ng/product-dft-backend:latest`


It is necessary to inject the environment variables, credentials and URLs that can be found on application.properties file.
#### CatenaX variables
| Property       | Value          | Description        | Example |
|----------------|----------------|--------------------|---------|
| manufacturerId | MANUFACTURERID | Id of manufacturer | CatenaX |


#### Digital Twins variables:
| Property name                             | Environment Variable Name                 | Description                                    | Example Value                    |
|-------------------------------------------|-------------------------------------------|------------------------------------------------|----------------------------------|
| digital-twins.hostname                    | DIGITAL-TWINS_HOSTNAME                    | hostname for Digital Twins                     | https://                         |
| digital-twins.authentication.url          | DIGITAL-TWINS_AUTHENTICATION_URL          | authentication url for Digital Twins           | https://                         |
| digital-twins.authentication.clientId     | DIGITAL-TWINS_AUTHENTICATION_CLIENTID     | client ID authentication for Digital Twins     | sa-cl6-cx-4                      |
| digital-twins.authentication.clientSecret | DIGITAL-TWINS_AUTHENTICATION_CLIENTSECRET | client secret authentication for Digital Twins | 
VrL8uSG5Tn3NrFiY39vs0klTmlvsRRmo |
| digital-twins.authentication.granttype    | DIGITAL-TWINS_AUTHENTICATION_GRANTTYPE.   | grantType authentication for Digital Twins     | client_credentials

The values are on the [Vault](https://vault.vault.demo.catena-x.net/).
*<i><b>Must create a GitHub token to access</b></i>
#### EDC variables:
| Property name    | Environment Variable Name | Description                                   | Example Value |
|------------------|---------------------------|-----------------------------------------------|---------------|
| edc.hostname     | EDC_HOSTNAME              | edc hostname                                  | https://      |
| edc.apiKeyHeader | EDC_APIKEYHEADER          | API KEY header for edc                        | X-Api_Key     |
| edc.apiKey       | EDC_APIKEY                | API KEY for edc                               | 123456        |
| dft.hostname     | DFT_HOSTNAME              | hostname for DFT                              | https://      |
| dft.apiKeyHeader | DFT_APIKEYHEADER          | url authentication key for edc asset payload  | Api-Key       |
| dft.apiKey       | DFT_APIKEY                | url authentication code for edc asset payload | someCode      |
| edc.enabled      | EDC_ENABLED               | enable / disable edc                          | true / false  |
| edc.consumer.hostname | EDC_CONSUMER_HOSTNAME | edc consumer hostname                         | https://           
| edc.consumer.apikeyheader | EDC_CONSUMER_APIKEYHEADER | API KEY header for edc               | X-Api_Key 
| edc.consumer.apikey | EDC_CONSUMER_APIKEY | API KEY for edc   										  | 123456
| edc.consumer.datauri | EDC_CONSUMER_DATAURI | consumer data uri  									  | /api/v1/ids/data

The values are in the [Vault](https://vault.vault.demo.catena-x.net/).
*<i><b>Must create a GitHub token to access</b></i> 

#### KEYCLOAK variables:

| Property name    | Environment Variable Name | Description                                   | Example Value |
|------------------|---------------------------|-----------------------------------------------|---------------|
| keycloak.realm    | KEYCLOAK_REALM             |                                 | REALM NAME     |
| keycloak.auth-server-url | KEYCLOAK_AUTH_SERVER_URL          |                        | Server URl    |
| keycloak.ssl-required      | KEYCLOAK_SSL-REQUIRED                |                              | External       |
| keycloak.resource    | KEYCLOAK_REASOURCE             |                              |   Keycloak Username    |
| keycloak.use-resource-role-mappings | KEYCLOAK_USE-RESOURCE-ROLE-MAPPINGS         |  | true       |
| keycloak.bearer-only      | KEYCLOAK_BEARER-ONLY                |  | someCode      | true         |

## Upload a file:
When a file .csv is uploaded, the program checks whether the file is a SerialPartTypization or an AssemblyPartRelationship and there is a pipeline for each one.

<b>For Serial Part Typization:</b>

1. Maps the content of the line with an Aspect.
2. Generates the UUID if it does not contain a UUID.
3. Registers in DigitalTwins.
4. Stores the line in the database.

<b>For Assembly Part Relationship:</b>

1. Maps the content of the line with an Aspect Relationship.
2. checks if an Aspect exists so it can be related to that Aspect.
3. Registers in DigitalTwins.
4. Stores the line in the database.

The file .csv is loaded in memory, the content is saved and then, the file is removed from memory.


If the file is not .csv, it is read, processed and is considered as FAILED



