# README
## Table Of Contents
-   [Demo](#demo)
-   [Run configuration](#run-configuration)
-   [Dependencies](#dependencies)
-   [Repository organizing](#repository-organizing)

## Demo
[demo video in dropbox](https://www.dropbox.com/s/gi2rrvfkl17vlng/vessel.mp4?dl=0)

## Run configuration
[Run Configuration](Run_Configuration.md)

## Dependencies
-   [`The Simulator of vessel and wagon`](https://www.github.com/sonnyhcl/Frontend)

    > Attention: In order to perform as [demo](#demo) shows, The `Activiti Backend` project must be coordinated with the `Vessel Frontend` project.
-   [`Coordinator`](https://github.com/sonnyhcl/coordinator)
    
    This repo simulates aws lambda locally.
    
## Repository organizing
```bash
.
├── image       # images in README
├── pom.xml     # maven pom file
├── README.md   # README
└── src
    └── main
        ├── java
        │   └── supplychain
        │       ├── activiti # Closely related to the activiti engine our bussiness process execution.       
        │       │   ├── conf # Java configuration files including configuring activiti engine and web context.
        │       │   │   ├── ActivitiEngineConfiguration.java
        │       │   │   ├── AsyncConfiguration.java
        │       │   │   ├── Bootstrapper.java
        │       │   │   ├── ContentStorageConfiguration.java
        │       │   │   ├── CustomSecurityConfig.java
        │       │   │   ├── DatabaseConfiguration.java
        │       │   │   ├── EmailConfiguration.java
        │       │   │   ├── JacksonConfiguration.java
        │       │   │   ├── MyApplicationConfiguration.java
        │       │   │   ├── MyCorsRegistration.java
        │       │   │   ├── RestApiConfiguration.java
        │       │   │   ├── RestTemplateConfiguration.java
        │       │   │   └── SchedulingConfiguration.java
        │       │   ├── coord # Impletation of 4 Coordinator Service , among them , VWC is the most complex one.
        │       │   │   ├── MSCoordinator.java
        │       │   │   ├── SWCoordinator.java
        │       │   │   ├── VMCoordinator.java
        │       │   │   └── VWCoordinator.java
        │       │   ├── listener  # Some of the exection/task listeners set in process model.
        │       │   │   ├── AnchorStartListener.java
        │       │   │   ├── DockTaskEndListener.java
        │       │   │   ├── FlowIntoVoyaListener.java
        │       │   │   ├── InitListener.java
        │       │   │   ├── InitWeagonListener.java
        │       │   │   ├── RecvMsgFromLambdaListener.java
        │       │   │   ├── RunEndListener.java
        │       │   │   ├── RunListener.java
        │       │   │   ├── SendArraInfoToSWC.java
        │       │   │   ├── SendMsgToVWC.java
        │       │   │   ├── SendOrderToMSC.java
        │       │   │   ├── SengMsgToLambdaListener.java
        │       │   │   ├── VoyagingListener.java
        │       │   │   └── VoyaTaskStartListener.java
        │       │   ├── rest # Some convertors between custom type defined by myself and RestVariable Type build in engine.
        │       │   │   └── service
        │       │   │       └── api
        │       │   │           ├── CustomActivitiTaskActionService.java
        │       │   │           ├── CustomArrayListRestVariableConverter.java
        │       │   │           ├── CustomBaseExcutionVariableResource.java
        │       │   │           ├── CustomBaseVariableCollectionResource.java
        │       │   │           ├── CustomDateRestVariableConverter.java
        │       │   │           ├── CustomRestResponseFactory.java
        │       │   │           ├── LocationRestVariableConverter.java
        │       │   │           ├── VPortRestVariableConverter.java
        │       │   │           ├── WeagonRestVariableConverter.java
        │       │   │           └── WPortRestVariableConverter.java
        │       │   ├── service  # Some JavaDelegate Class bound to Service Task in process.
        │       │   │   ├── CustomActivitiTaskFormService.java
        │       │   │   ├── SendApplyToVMCService.java
        │       │   │   ├── SendMsgToAWSService.java
        │       │   │   ├── SendMsgToWVCService.java
        │       │   │   ├── UpdateVesselInfoService.java
        │       │   │   └── UpdateWeagonInfoService.java
        │       │   └── servlet  # About other web configuration 
        │       │       ├── MyApiDispatcherServletConfiguration.java # servlet dispatcher for url pattern /api/*
        │       │       ├── MyCorsFilter.java # used to solve CORS problem.
        │       │       ├── MyWebConfigurer.java # configure two servlet dispatcher , respectively for '/api/*' and '/app/*'.
        │       │       ├── SystemWebSocketHandler.java # websocket haven't been used , you can ignore them.
        │       │       └── WebsocketHandshakeInterceptor.java # Implementation of REST interfaces to provide endpoints to front-end or AWS to access.
        │       ├── entity # Some custom Types for process model.
        │       │   ├── IotVessel.java
        │       │   ├── Location.java
        │       │   ├── Path.java
        │       │   ├── VesselVariablesResponse.java
        │       │   ├── VPort.java
        │       │   ├── Weagon.java
        │       │   └── WPort.java
        │       ├── event
        │       │   ├── ACTFEvent.java
        │       │   ├── EventType.java
        │       │   └── VWFEvent.java
        │       ├── global  # Implementation of global cache and message queue
        │       │   ├── GlobalEventQueue.java
        │       │   ├── GlobalMqttClient.java
        │       │   └── GlobalVariables.java
        │       ├── repository
        │       │   └── LocationRepository.java
        │       ├── service
        │       │   ├── BaseService.java
        │       │   ├── LocationServiceImpl.java
        │       │   └── LocationService.java
        │       ├── util
        │       │   ├── DateUtil.java
        │       │   ├── EqualUtil.java
        │       │   └── Topic.java
        │       └── web
        │           ├── AbstractController.java
        │           ├── CoordController.java
        │           ├── ProcessInstancesResource.java
        │           ├── TaskController.java
        │           ├── TaskFormResource.java
        │           ├── VesselController.java
        │           └── VesselProcessInstanceVariableDataResource.java
        └── resources
            ├── activiti.cfg.xml
            ├── log4j.properties    # logger conf
            ├── META-INF
            │   └── activiti-app
            │       └── activiti-app.properties # activiti-app conf
            ├── processes # the process modle we use
            │   ├── Vessel.bpmn # same to the one below
            │   └── Vessel.bpmn20.xml
            └── version.properties
```
