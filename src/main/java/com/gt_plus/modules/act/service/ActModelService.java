/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.act.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.BaseService;
import com.gt_plus.modules.oa.entity.setting.TaskSetting;
import com.gt_plus.modules.oa.entity.setting.TaskSettingVersion;
import com.gt_plus.modules.oa.service.setting.TaskSettingService;
import com.gt_plus.modules.oa.service.setting.TaskSettingVersionService;

/**
 * 流程模型相关Controller
 * @author gt_plus
 * @version 2013-11-03
 */
@Service
@Transactional(readOnly = true)
public class ActModelService extends BaseService {

	@Autowired
	private RepositoryService repositoryService;

	/**
	 * 流程模型列表
	 */
	public Page<org.activiti.engine.repository.Model> modelList(Page<org.activiti.engine.repository.Model> page, String category) {

		ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc();
		
		if (StringUtils.isNotEmpty(category)){
			modelQuery.modelCategory(category);
		}
		
		page.setCount(modelQuery.count());
		page.setList(modelQuery.listPage(page.getFirstResult(), page.getMaxResults()));

		return page;
	}

	/**
	 * 创建模型
	 * @throws UnsupportedEncodingException 
	 */
	@Transactional(readOnly = false)
	public org.activiti.engine.repository.Model create(String name, String key, String description, String category) throws UnsupportedEncodingException {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode editorNode = objectMapper.createObjectNode();
		editorNode.put("id", "canvas");
		editorNode.put("resourceId", "canvas");
		ObjectNode stencilSetNode = objectMapper.createObjectNode();
		stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
		editorNode.put("stencilset", stencilSetNode);
		org.activiti.engine.repository.Model modelData = repositoryService.newModel();

		description = StringUtils.defaultString(description);
		modelData.setKey(StringUtils.defaultString(key));
		modelData.setName(name);
		modelData.setCategory(category);
		modelData.setVersion(Integer.parseInt(String.valueOf(repositoryService.createModelQuery().modelKey(modelData.getKey()).count()+1)));

		ObjectNode modelObjectNode = objectMapper.createObjectNode();
		modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
		modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
		modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
		modelData.setMetaInfo(modelObjectNode.toString());
			
		repositoryService.saveModel(modelData);
		repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
		
		return modelData;
	}
	
	@Autowired
	private TaskSettingService taskSettingService;
	
	@Autowired
	private TaskSettingVersionService taskSettingVersionService;

	/**
	 * 根据Model部署流程
	 */
	@Transactional(readOnly = false)
	public String deploy(String id) {
		String message = "";
		try {
			org.activiti.engine.repository.Model modelData = repositoryService.getModel(id);
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			ObjectNode editorNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
			byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
			
			String processName = modelData.getName();
			if (!StringUtils.endsWith(processName, ".bpmn20.xml")){
				processName += ".bpmn20.xml";
			}
//			System.out.println("========="+processName+"============"+modelData.getName());
			ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
			Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
					.addInputStream(processName, in).deploy();
//					.addString(processName, new String(bpmnBytes)).deploy();
			
			// 设置流程分类
			List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
			for (ProcessDefinition processDefinition : list) {
				repositoryService.setProcessDefinitionCategory(processDefinition.getId(), modelData.getCategory());
				message = "部署成功，流程ID=" + processDefinition.getId();
				/*
				 *	获取流程定义中的节点权限，随版本发布版本化的节点权限，oa_task_setting表数据 >> oa_task_setting_version表
				 *	此行开始
				 */
				//流程标识
				String procDefKey = editorNode.get("properties").get("process_id").textValue();
				//流程节点权限集合
				List<TaskSetting> taskSettingList = taskSettingService.findListByProcDefKey(procDefKey);
				if (taskSettingList.size() > 0) {
					//版本化流程节点权限集合
					List<TaskSettingVersion> taskSettingVersionList = Lists.newArrayList();
					//版本化流程定义ID
					String procDefId = processDefinition.getId();
					for (TaskSetting taskSetting : taskSettingList) {
						TaskSettingVersion taskSettingVersion = new TaskSettingVersion();
						taskSettingVersion.setProcDefId(procDefId);
						taskSettingVersion.setUserTaskId(taskSetting.getUserTaskId());
						taskSettingVersion.setUserTaskName(taskSetting.getUserTaskName());
						taskSettingVersion.setSettingValue(taskSetting.getSettingValue());
						taskSettingVersion.setPermission(taskSetting.getPermission());
						taskSettingVersion.setRuleArgs(taskSetting.getRuleArgs());
						taskSettingVersion.preInsert();
						taskSettingVersionList.add(taskSettingVersion);
					}
					//批量插入版本化节点权限数据
					taskSettingVersionService.batchSave(taskSettingVersionList);
				}
				/*
				 *	获取流程定义中的节点权限，随版本发布版本化的节点权限，oa_task_setting表数据 >> oa_task_setting_version表
				 *	此行结束
				 */
			}
			if (list.size() == 0){
				message = "部署失败，没有流程。";
			}
		} catch (Exception e) {
			System.out.println("问题："+e.getMessage());
			System.out.println("找到问题");
			throw new ActivitiException("设计模型图不正确，检查模型正确性，模型ID="+id, e);
		}
		return message;
	}
	
	/**
	 * 导出model的xml文件
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public void export(String id, HttpServletResponse response) {
		try {
			org.activiti.engine.repository.Model modelData = repositoryService.getModel(id);
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			ObjectNode editorNode = (ObjectNode)new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
			byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

			ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
			IOUtils.copy(in, response.getOutputStream());
			String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.flushBuffer();
		} catch (Exception e) {
			throw new ActivitiException("导出model的xml文件失败，模型ID="+id, e);
		}
		
	}

	/**
	 * 更新Model分类
	 */
	@Transactional(readOnly = false)
	public void updateCategory(String id, String category) {
		org.activiti.engine.repository.Model modelData = repositoryService.getModel(id);
		modelData.setCategory(category);
		repositoryService.saveModel(modelData);
	}
	
	/**
	 * 删除模型
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = false)
	public void delete(String id) {
		repositoryService.deleteModel(id);
	}
}
