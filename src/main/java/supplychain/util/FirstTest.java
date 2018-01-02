package supplychain.util;
///*package supplychain.activiti.conf;
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.activiti.engine.ProcessEngine;
//import org.activiti.engine.ProcessEngines;
//import org.activiti.engine.RepositoryService;
//import org.activiti.engine.RuntimeService;
//import org.activiti.engine.TaskService;
//import org.activiti.engine.impl.cmd.StartProcessInstanceByMessageCmd;
//import org.activiti.engine.repository.Deployment;
//import org.activiti.engine.runtime.ProcessInstance;
//import org.activiti.engine.task.Task;
//import org.drools.KnowledgeBase;
//import org.drools.builder.KnowledgeBuilder;
//import org.drools.builder.KnowledgeBuilderFactory;
//import org.drools.builder.ResourceType;
//import org.drools.core.factmodel.BuildUtils;
//import org.drools.definition.KnowledgePackage;
//import org.drools.io.ResourceFactory;
//import org.drools.runtime.StatefulKnowledgeSession;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mvel2.asm.ClassWriter;
//import org.mvel2.asm.FieldVisitor;
//import org.mvel2.asm.MethodVisitor;
//import org.mvel2.asm.Opcodes;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javassist.bytecode.Opcode;
//import zbq.entity.Person;
//import zbq.entity.Sale;
//import zbq.entity.SaleItem;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:activiti.cfg.xml")
//public class FirstTest {
//	@Test 
//	public void TestDrools() {
//		KnowledgeBuilder  kbBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();//创建一个KnowledgeBuilder ， 用于保存资源文件
//		//kBuilder添加资源后，根据传入的资源类型，决定下一步操作，如果是规则文件，便将这些规则文件解析、编译、注册，经过一系列的工作后，就会将这些规则的相关内容转换为
//		//一个注册包PackageRegistry , 并且在KnowledgeBuilder中会有一个Map来缓存这些PackageRegistry实例
//		kbBuilder.add(ResourceFactory.newClassPathResource("rule/first.drl"), ResourceType.DRL);//添加规则资源到KnowledgeBuilder
//		Collection<KnowledgePackage> pkgs = kbBuilder.getKnowledgePackages(); //会使用PackageRegistry来创建一个知识包集合返回
//		KnowledgeBase kBase = kbBuilder.newKnowledgeBase();//创建KnowledgeBase实例
//		kBase.addKnowledgePackages(pkgs);//将知识包部署到KnowledgeBase中 , 保存全部的应用的知识定义，这些知识也包括业务规则定义
//		StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();//使用KnowledgeBase创建StatefulKnowledgeSession ， 保存运行时的数据
//		Person person = new Person();
//		person.setName("Tom");
//		kSession.insert(person);//向StatefulKnowledgeSession中加入事实
//		Person person1 = new Person();
//		person1.setName("Nick");
//		kSession.insert(person1);
//		kSession.fireAllRules();//匹配规则
//		kSession.dispose();//关闭当前session的资源
//	}
//	
//	@Test
//	public void  TestAsm() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
//		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//		//访问类的头部
//		cw.visit(Opcodes.V1_5,Opcodes.ACC_PUBLIC, "zbq/base/MyObjruntimeService.setVariable(executionId ,\"circleCnt\", x+1);ect", null , "java/lang/Object", null);
//		//访问方法，创造构造器
//		MethodVisitor construct = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
//		construct.visitCode();
//		construct.visitVarInsn(Opcodes.ALOAD , 0);
//		construct.visitMethodInsn(Opcodes.INVOKESPECIAL , "java/lang/Object" , "<init>" , "()V" , false);
//		construct.visitInsn(Opcode.RETURN);
//		construct.visitMaxs(0, 0);
//		construct.visitEnd();//结束方法访问
//		//访问属性，创建userName属性
//		FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE, "userName", BuildUtils.getTypeDescriptor("String"), null, null);
//		fv.visitEnd(); //结束属性访问
//		cw.visitEnd(); //结束类访问
//		final byte[] code = cw.toByteArray();
//		//根据字节数组创建Class
//		Class clazz = new ClassLoader() {
//			protected Class findClass(String name) throws ClassNotFoundException{
//				return defineClass(name, code, 0, code.length);
//			}
//		}.loadClass("zbq.base.MyObject");
//		
//		//实例化对象
//		Object object = clazz.newInstance();
//		System.out.println(object);
//	}
//
//	@Test
//	public void testSaleProcess() {
//		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//		RepositoryService repositoryService = engine.getRepositoryService();
//		TaskService taskService = engine.getTaskService();
//		RuntimeService runtimeService = engine.getRuntimeService();
//	
//		String depId = null;
//		try {
//			depId = repositoryService.createDeploymentQuery().deploymentName("rule").singleResult().getId();
//		}catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("\033[1;31m ×××××××××第一次部署该流程××××××××\033[0m");
//		}
//		
//		if(depId == null) {
//			repositoryService.createDeployment()
//			.addClasspathResource("rule/sale.drl")
//			.addClasspathResource("diagrams/Rule.bpmn")
//			.name("rule").deploy();
//		}else {
//			System.out.println("\033[1;32m ×××××××××该流程已部署过××××××××\033[0m");
//		}
//		ProcessInstance pi = runtimeService.startProcessInstanceByKey("RuleProcess");
//		Calendar calendar = Calendar.getInstance();
//		//创建实施实例，满足周末打9折
//		Sale s1 = new Sale(createDate("2017-12-02"), "001");
//		SaleItem s1Item1 = new SaleItem("矿泉水" , new BigDecimal(4) , new BigDecimal(20));
//		s1.addItem(s1Item1);
//		
//		//满100打8折
//		Sale s2 = new Sale(createDate("2017-12-01"), "002");
//		SaleItem s2Item1 = new SaleItem("爆米花", new BigDecimal(5), new BigDecimal(2));
//		s2.addItem(s2Item1);
//		
//		//满足200 打9折
//		Sale s3 = new Sale(createDate("2017-12-04" ), "003");
//		SaleItem s3Item1 = new SaleItem("可乐一箱", new BigDecimal(3), new BigDecimal(70));
//		s3.addItem(s3Item1);
//		
//		//周末满200
//		Sale s4 = new Sale(createDate("2017-12-03"), "004");
//		SaleItem s4Item1 = new SaleItem("啤酒一箱", new BigDecimal(3), new BigDecimal(80));
//		 s4.addItem(s4Item1);
//		 
//		 Map<String, Object> vars = new HashMap<String, Object>();
//		 vars.put("sale1" , s1);
//		 vars.put("sale2" , s2);
//		 vars.put("sale3" , s3);
//		 vars.put("sale4" , s4);
//		
//		 //查找任务
//		 Task task = (Task) taskService.createTaskQuery()
//				 	.processInstanceId(pi.getId())
//				 	.singleResult();
//		taskService.complete(task.getId() , vars);
//	}
//	
//	@Test
//	public void deployProcess() throws InterruptedException{
//		String bpmnName = "diagrams/eventSubProcess.bpmn";
//		String dpName = "event-subprocess";
//		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//		RepositoryService repositoryService = engine.getRepositoryService();
//		TaskService taskService = engine.getTaskService();
//		RuntimeService runtimeService = engine.getRuntimeService();
//		String depId = null;
//		try {
//			depId = repositoryService.createDeploymentQuery().deploymentName(dpName).singleResult().getId();
//		}catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("\033[1;31m ×××××××××第一次部署该流程××××××××\033[0m");
//		}
//		
//		if(depId == null) {
//			repositoryService.createDeployment()
//			.addClasspathResource(bpmnName)
//			.name(dpName).deploy();
//		}else {
//			System.out.println("\033[1;32m ×××××××××该流程已部署过××××××××\033[0m");
//		}
//		 Map<String, Object> vars = new HashMap<String, Object>();
//		 vars.put("circleCnt" , 1);
//		ProcessInstance pi = runtimeService.startProcessInstanceByKey(dpName , vars);
//		 
//		Thread.sleep(1000 * 60 * 7);
////		 Map<String, Object> vars = new HashMap<String, Object>();
////		 //查找任务
////		 Task task = (Task) taskService.createTaskQuery()
////				 	.processInstanceId(pi.getId())
////				 	.singleResult();
////		taskService.complete(task.getId() , vars);
//	}
//	@Test
//	public void deleteDeploy() {
//		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//		RepositoryService repositoryService = engine.getRepositoryService();
//		List<Deployment> deployments = repositoryService.createDeploymentQuery().orderByDeploymenTime().desc().list();
//		for(Deployment dep : deployments) {
//			repositoryService.deleteDeployment(dep.getId(), true);
//		}
//		
//	}
//	
//	public Date createDate(String dateStr) {
//		Calendar calendar = Calendar.getInstance();
//		Date date = null;
//		try {
//			date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return date;
//	}
//	
//	@Test
//	public void startByMsg() {
//		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
//		RuntimeService runtimeService = engine.getRuntimeService();
//		runtimeService.startProcessInstanceByMessage("Msg_Start");
//	}
//	
//	@Test
//	public void printColor() {
//		System.out.println(" color : ");
//		System.out.println("\033[1;92m\033[0;100m\033[31;1mRed\033[0m");
//		System.out.println("\033[47m\033[32;1mGreen\033[0m");
//		System.out.println("\033[33;1mYello\033[0m");
//		System.out.println("\033[34;1mBlue\033[0m");
//		System.out.println("\033[35;1mMegenta\033[0m");
//		System.out.println("\033[35;1mCyan\033[0m");
//	}
//}
