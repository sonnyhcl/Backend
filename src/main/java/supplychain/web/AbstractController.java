package supplychain.web;

import org.activiti.engine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 抽象Controller，提供一些基础的方法、属性
 */
@RestController
public class AbstractController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ProcessEngine processEngine;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected IdentityService identityService;
    @Autowired
    protected ManagementService managementService;
    @Autowired
    protected FormService formService;
}
