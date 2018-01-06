### README

---

- ##### How to configure and run the project :

  - `Development environment` :
    - `STS` ( spring-tool-suite-3.9.0.RELEASE ) .
  - `Source Code  ` : git clone git@github.com:sonnyhcl/Backend.git 
  - `Import ` :  To import Backend as a Maven project in STS, follow the steps below : `File -> Import -> Maven -> Existing Maven Projects->Select Backend project `
  
  - Right-click the project name` Backend`, then enter ->` Maven` -> `Update Project`, waiting to load dependencies, after loading is completed , right-click the project name, followed by -> `Run As `-> `Run configurations ...` , Click `Maven Build`, add `jetty-run`,` jetty-stop` command.
  - Enter `localhost: 8084 / activiti-app / index` in the browser, enter the login page, user `name`: admin, `password`: test.
  - After running the project and logging in  the system , upload process models under the root directory `src/main/resources `  , such as `Supply_Chain_pool.bpmn`  、`Weagon_Test.bpmn` , then create  app for it  and publish your app  , then  go to processes page to  start the selected process.
  - `Attention` :   In order to perform well , The `Backend` project must be coordinated with the `Frontend` project.


