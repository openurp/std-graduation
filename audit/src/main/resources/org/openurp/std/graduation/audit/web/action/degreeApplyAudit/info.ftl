[#ftl]
[@b.toolbar title="${degreeApply.std.user.name}的跨校交流"]
  bar.addBack();
[/@]
<div class="panel panel-default">
  <div class="panel-heading">
    <h3 class="panel-title">
         <span class="glyphicon glyphicon-bookmark"></span>${degreeApply.school.name}<span style="font-size:0.8em">(${degreeApply.beginOn?string("yyyy-MM")}~${degreeApply.endOn?string("yyyy-MM")})</span>
         [#if degreeApply.state!="通过"]
         <div class="btn-group">
         [@b.a onclick="return audit('${degreeApply.id}',1)" class="btn btn-sm btn-info"]<span class="glyphicon glyphicon-edit"></span>审核通过[/@]
         </div>
         [/#if]
         [@b.a onclick="return audit('${degreeApply.id}',0)" class="btn btn-sm btn-warning"]<span class="glyphicon glyphicon-remove"></span>退回修改[/@]
    </h3>
  </div>
[#assign std= degreeApply.std/]
<table class="infoTable">
    <tr>
      <td class="title" width="10%">学号：</td>
      <td width="23%">${(std.user.code)!}</td>
      <td class="title" width="10%">姓名：</td>
      <td width="23%">${std.user.name?html}</td>
      <td class="title" width="10%">修读专业：</td>
      <td>${(degreeApply.majorName?html)!}</td>
    </tr>
    <tr>
      <td class="title">培养层次：</td>
      <td>${degreeApply.level.name}</td>
      <td class="title">教学类别：</td>
      <td>${degreeApply.category.name}</td>
      <td class="title">填写时间：</td>
      <td>${(degreeApply.updatedAt?string("yyyy-MM-dd HH:mm"))!}</td>
    </tr>
    <tr>
      <td class="title">累计学分：</td>
      <td>${degreeApply.credits}分,冲抵${degreeApply.exemptionCredits}分</td>
      <td class="title">成绩材料：</td>
      <td>[#if transcriptPath??]
         <a href="${transcriptPath}" target="_blank"><span class="glyphicon glyphicon-download"></span>下载附件</a>
         [#else]--[/#if]
      </td>
      <td class="title">审核状态：</td>
      <td><span class="[#if degreeApply.state=="通过"]text-success[#else]text-danger[/#if]">${degreeApply.state}${degreeApply.auditOpinion!}</span></td>
    </tr>
  </table>
    [@b.grid items=degreeApply.grades sortable="false" var="grade" ]
        [@b.row]
            [@b.col title="序号" width="5%"]${grade_index+1}[/@]
            [@b.col property="courseName" title="课程名称" width="25%"/]
            [@b.col property="credits"  title="学分" width="5%"/]
            [@b.col property="scoreText" title="成绩" width="5%"/]
            [@b.col property="acquiredOn" title="获得年月" width="10%"]${grade.acquiredOn?string("yyyy-MM")}[/@]
            [@b.col title="免修冲抵" width="30%"]
               [#list grade.courses as c]
                 ${c.code} ${c.name} ${c.credits}分[#if c_has_next]<br>[/#if]
               [/#list]
            [/@]
            [@b.col property="remark" title="说明" width="20%"/]
        [/@]
    [/@]
</div>
  [@b.form name="degreeApplyAuditForm" action="!audit"]
    <input name="passed" value="" type="hidden"/>
    <input name="id" value="" type="hidden"/>
  [/@]
<script>
   function audit(id,passed){
       var msg="确定审核通过?"
       if(passed=="0"){
          msg="确定审核不通过?"
       }
       if(confirm(msg)){
         var form=document.degreeApplyAuditForm;
         form['id'].value=id;
         form['passed'].value=passed;
         bg.form.submit(form);
         return false;
       }else{
         return false;
       }
   }
</script>