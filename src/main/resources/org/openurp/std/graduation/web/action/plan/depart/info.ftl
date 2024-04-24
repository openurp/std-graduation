[@b.head/]
<div class="container">
  [#assign std=result.std/]
  [@b.toolbar title="${std.project.school.name} 学生计划完成情况"]
    bar.addPrint();
  [/@]
  [#include "../progress.ftl"/]
  <table align="center" class="infoTable">
   <tr>
    <td class="title" width="10%">学号:</td>
    <td class="content" width="20%">${std.code}</td>
    <td class="title" width="10%">姓名:</td>
    <td class="content" width="25%">${std.name}</td>
    <td class="title" width="10%">年级:</td>
    <td class="content" width="25%">${std.state.grade!}</td>
   </tr>
   <tr>
    <td class="title">院系:</td>
    <td class="content">${std.state.department.name}</td>
    <td class="title">专业/方向:</td>
    <td class="content">${std.state.major.name}&nbsp;${(std.state.direction.name)!}</td>
    <td class="title">班级:</td>
    <td class="content">${(std.state.squad.name)!}</td>
   </tr>
   <tr>
    <td class="title">层次/类别:</td>
    <td class="content">${std.level.name} / ${std.stdType.name}</td>
    <td class="title">要求/实修:</td>
    <td class="content">${result.requiredCredits}&nbsp;/&nbsp;${result.passedCredits}</td>
    <td class="title">更新时间:</td>
    <td class="content">
    [#if result.persisted]
      ${(result.updatedAt?string('yyyy-MM-dd HH:mm:ss'))!}
      [@b.a href="!lastest?student.id="+result.std.id]最新计划完成情况[/@]
    [/#if]
    </td>
   </tr>
   [#if result.owedCredits>0]
   <tr>
     <td class="title">学分缺口:</td>
     <td><span style="color:red">${result.owedCredits}分</span>[#if result.owedCredits!=result.owedCredits3](在读课程通过后，[#if result.owedCredits3>0]<span style="color:red">缺${result.owedCredits3}分</span>[#else]可完成计划[/#if])[/#if]</td>
     <td class="title">预计说明:</td>
     <td colspan="3" class="text-muted">表示如果毕业论文等成绩通过后，计划审核的预计结果</td>
   </tr>
   [/#if]
  </table>
<div class="grid">
<table width="100%" class="grid-table">
  <thead class="grid-head">
    <tr>
      <th width="5%">课程</th>
      <th width="10%">代码</th>
      <th width="30%">名称</th>
      <th>学分</th>
      <th>完成学分</th>
      <th>成绩</th>
      <th>完成否</th>
      <th width="25%">备注</th>
    </tr>
  </thead>
  [@groupsData result.topGroupResults?sort_by('indexno'),1/]
</table>
</div>
<br><br>
</div>
[@b.foot/]
