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
        menu4.addItem("移除学生", action.multi("remove","确定删除选中学生的审核结果?"));

        bar.addItem("自动审核", action.multi('audit'));

        var menu2=bar.addMenu("设置发布", null);
        menu2.addItem("发布", action.multi("publish","确定发布?","publish=1"));
        menu2.addItem("取消发布", action.multi("publish","确定取消发布?","publish=0"));

        bar.addItem("清空结果", "clearResult()", "edit-delete.png");

        bar.addItem("导出",action.exportData("examinee.code:考生号,std.code:学号,std.name:姓名,"+
               "std.gender.name:性别,std.person.birthday:出生日期,std.person.code:身份证号,"+
               "std.person.politicalStatus.code:政治面貌代码,std.person.politicalStatus.name:政治面貌,std.person.nation.code:民族代码,"+
               "std.person.nation.name:民族,"+
               "std.state.grade.code:年级,std.state.campus.name:校区,std.level.name:培养层次,std.studyType.name:学习形式,"+
               "std.state.department.name:院系,std.state.major.name:专业,std.state.direction.name:方向,"+
               "std.state.squad.code:班级代码,std.state.squad.name:班级名称,std.duration:学制,"+
               "std.beginOn:入学日期,std.graduateOn:预计毕业日期,std.state.status.name:学籍状态,"+
               "std.state.squad.mentor.name:辅导员,std.state.squad.master.name:班主任,passed:是否通过," +
               "degree.name:拟授予学位,graduate.diplomaNo:学位证书编号,passedItems:通过明细,failedItems:不通过明细",
                null,'fileName=学位审核结果'));
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
        [@b.col title="是否通过" width="10%" property="passed"]
          [#if (result.passed)??]
            [#if result.passed]通过 <span class="text-muted" style="font-size:0.8em">${(result.degree.name)!}</span>[#else]<span style='color:red'>未通过</span>[/#if]
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
  function clearResult() {
        var ids = bg.input.getCheckBoxValues("result.id");
    if(ids==null||ids==""){
      alert("请选择记录进行操作!");  return;
    }
    if(isPublished(ids)) {
      return;
    }
    if(confirm("确定要清空审核结果吗？")) {
      bg.form.submit(document.auditResultListForm, "${b.base}/degree/audit!clearResult.action");
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
