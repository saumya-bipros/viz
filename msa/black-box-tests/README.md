
## Black box tests execution
To run the black box tests with using Docker, the local Docker images of Vizzionnaire's microservices should be built. <br />
- Build the local Docker images in the directory with the Vizzionnaire's main [pom.xml](./../../pom.xml):
        
        mvn clean install -Ddockerfile.skip=false
- Verify that the new local images were built: 

        docker image ls
As result, in REPOSITORY column, next images should be present:
        
        vizzionnaire/tb-coap-transport
        vizzionnaire/tb-lwm2m-transport
        vizzionnaire/tb-http-transport
        vizzionnaire/tb-mqtt-transport
        vizzionnaire/tb-snmp-transport
        vizzionnaire/tb-node
        vizzionnaire/tb-web-ui
        vizzionnaire/tb-js-executor

- Run the black box tests in the [msa/black-box-tests](../black-box-tests) directory with Redis standalone:

        mvn clean install -DblackBoxTests.skip=false

- Run the black box tests in the [msa/black-box-tests](../black-box-tests) directory with Redis cluster:

        mvn clean install -DblackBoxTests.skip=false -DblackBoxTests.redisCluster=true

- Run the black box tests in the [msa/black-box-tests](../black-box-tests) directory in Hybrid mode (postgres + cassandra):

        mvn clean install -DblackBoxTests.skip=false -DblackBoxTests.hybridMode=true



