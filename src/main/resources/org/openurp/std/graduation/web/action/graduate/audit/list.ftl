[#ftl/]
[@b.head/]
[#function passedStr o]
    [#if o.passed??]
        [#local resultValue]${o.passed?string("<font color='green'>通过</font>", "<font color='red'>未通过</font>")}[/#local]
        [#return resultValue/]
    [#else]
        [#local resultValue]<font color="blue">未审核</font>[/#local]
        [#return resultValue/]
    [/#if]
[/#function]

  <input type="hidden" name="_params" value="${b.paramstring}"/>
  [@b.grid items=results var="result"]
    [@b.gridbar]
        var menu4 = bar.addMenu("学生名单", null);
        menu4.addItem("初始化名单", action.method("init","按照毕业日期初始化名单？"));
        menu4.addItem("添加学生(按学号)", "addStudents()");
        menu4.addItem("移除延期学生", action.method("removeDeferred","按照毕业日期移出延期学生？"));
        menu4.addItem("移除学生", action.multi("remove","确定删除选中学生的审核结果?"));

        var menu3 = bar.addMenu("自动审核", action.multi('audit'));
        menu3.addItem("手动审核", "manualAuditPrompt()");

        /*var menu1 = bar.addMenu("设置结论", null);
        var stateMap = {};
        [#list graduateStates! as state]
        stateMap["${state.id}"] = "${state.name}";
        menu1.addItem("${state.name}", "setConclusion('${state.id}')");
        [/#list]
        menu1.addItem("取消结论", "setConclusion()", "edit-delete.png");
        */

        //var menu2=bar.addMenu("设置发布", null);
        //menu2.addItem("发布","publish(1)");
        //menu2.addItem("取消发布","publish(0)");

        //bar.addItem("清空结果", "clearResult()", "edit-delete.png");

        var exportMenu = bar.addMenu("导出",action.exportData("std.code:学号,std.name:姓名,std.gender.name:性别,std.person.code:身份证号,"+
               "std.state.grade.code:年级,std.state.campus.name:校区,std.level.name:培养层次,std.state.department.name:院系," +
               "std.state.major.name:专业,std.state.direction.name:方向,std.state.squad.name:班级,std.studyType.name:学习形式,"+
               "std.studyOn:入学日期,std.graduateOn:预计毕业日期,std.state.status.name:学籍状态,passed:是否通过," +
               "passedItems:通过明细,failedItems:不通过明细",
                null,'fileName=毕业审核结果'));
        //exportMenu.addItem("导出教委格式", "exportKSYFormat()");

      function exportKSYFormat(){
        var form = document.searchForm;
        bg.form.addInput(form, "fileName", "${batch.name}毕业审核名单(考试院)");
        bg.form.submit(form, "${b.url('!index')}","_self");
      }
      [/@]
    [@b.row]
      [@b.boxcol/]
        [@b.col title="学号" property="std.code" width="12%"][/@]
        [@b.col title="姓名" property="std.name" width="8%"]
           <div class="text-ellipsis" title="${result.std.name}">${(result.std.name)!}</div>
        [/@]
        [@b.col title="年级" property="std.state.grade" width="6%"][/@]
        [@b.col title="培养层次" property="std.level.name" width="6%"][/@]
        [@b.col title="院系" property="std.state.department.name" width="16%"][/@]
        [@b.col title="专业/方向" property="std.state.major.name" width="19%"]
          <div class="text-ellipsis">${(result.std.state.major.name)!} ${(result.std.state.direction.name)!}</div>
        [/@]
        [@b.col title="是否通过" width="7%" property="passed"]
          [#if (result.passed)??]
            ${result.passed?string("通过", "<span style='color:red'>未通过</span>")}
          [#else]
            未审核
          [/#if]
           <input type="hidden" id="passed_${(result.id)!}" value="${(result.passed?string('1', '0'))!}" />
        [/@]
        [@b.col title="不通过明细" property="failedItems" ]
          <span title="${result.passedItems!}">${result.failedItems!'--'}</span>
        [/@]
        [@b.col title="状态" width="7%"]
           [#if result.locked]
           ${result.published?string("发布", "未发布")}
           [#else]未确认[/#if]
           <span id="published_${(result.id)!}" style="display:none"${result.locked?string('是','否')}</span>
        [/@]
      [/@]
  [/@]
  [@b.form name="auditResultListForm" method="post" action="!index"][/@]

[#include "manualAuditForm.ftl"/]
[#include "graduateStateTable.ftl"/]

[#-- 根据学号添加学生的colorbox --]
<div style="display:none;">
  <div id='addStudentsDiv'>
    [@b.form name='addStudentsForm' theme='list' action='!add' target="contentDiv" onsubmit="closeAddForm"]
      [@b.textarea label="学号" name="codes" rows="9" cols="60" required="true" maxlength="500000" /]
      [@b.formfoot]
        <input type="hidden" name="result.batch.id" value="${Parameters['result.batch.id']!}" />
        [@b.submit value="添加" /]
      [/@]
    [/@]
  </div>
</div>

<script>
  function publish(publish) {
        var ids = bg.input.getCheckBoxValues("result.id");
    if(ids==null||ids==""){
      alert("请选择记录进行操作!");  return;
    }
    if(publish) {
      if(isNotAudited(ids)) {
        return;
      }
      if(isNotSetConclusion(ids)) {
        return;
      }
    }
    bg.form.submit(document.auditResultListForm, "${b.base}/audit/graduate!publish.action?publish=" + publish);
  }

  function clearResult() {
        var ids = bg.input.getCheckBoxValues("result.id");
    if(ids==null||ids==""){
      alert("请选择记录进行操作!");  return;
    }
    if(isPublished(ids)) {
      return;
    }
    if(confirm("确定要清空审核结果吗？")) {
      bg.form.submit(document.auditResultListForm, "${b.base}/audit/graduate!clearResult.action");
    }
  }

    //修改毕业审核的毕结业结论
    function setConclusion(stateId) {
        var ids = bg.input.getCheckBoxValues("result.id");
        if(ids==null||ids==""){
          alert("请选择记录进行操作!");  return;
        }
        if(isNotAudited(ids)) {
          return;
        }
        if(isPublished(ids)) {
          return;
        }
        if(stateId && confirm("确定将当前学生的毕结业结论设置为“" + stateMap[stateId] + "”吗？")){
          bg.form.submit(document.auditResultListForm, "${b.base}/audit/graduate!setConclusion.action?stateId=" + stateId);
        }
        if(!stateId && confirm("确定要取消当前学生的毕结业结论吗？")){
          bg.form.submit(document.auditResultListForm, "${b.base}/audit/graduate!setConclusion.action");
        }
    }

  function autoAudit(){
    var ids = bg.input.getCheckBoxValues("result.id");
    if(ids == null || ids == "") {
      alert("请选择记录进行操作!");
      return;
    }
    if(isPublished(ids)) {
      return;
    }
    bg.form.submit(document.auditResultListForm,"${b.base}/audit/graduate!autoAudit.action");
  }

  function addStudents() {
     bg.load(["jquery-colorbox"],function(){
       jQuery.colorbox({title : '添加学生', transition : 'none', overClose : false, width:"800px",height:"300px",inline:true, href:"#addStudentsDiv"});
     });
  }
  function closeAddForm(form){
    jQuery.colorbox.close();
    return true;
  }
</script>
[@b.foot/]
